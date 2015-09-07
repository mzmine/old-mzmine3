/*
 * Copyright 2006-2015 The MZmine 3 Development Team
 * 
 * This file is part of MZmine 2.
 * 
 * MZmine 2 is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * MZmine 2 is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * MZmine 2; if not, write to the Free Software Foundation, Inc., 51 Franklin
 * St, Fifth Floor, Boston, MA 02110-1301 USA
 */

package io.github.mzmine.modules.rawdataimport.fileformats;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.mzmine.datamodel.ChromatographyData;
import io.github.mzmine.datamodel.DataPoint;
import io.github.mzmine.datamodel.MZmineProject;
import io.github.mzmine.datamodel.MassSpectrumType;
import io.github.mzmine.datamodel.MsMsScan;
import io.github.mzmine.datamodel.MsScan;
import io.github.mzmine.datamodel.RawDataFile;
import io.github.mzmine.datamodel.impl.MZmineObjectBuilder;
import io.github.mzmine.modules.rawdataimport.RawDataFileType;
import io.github.mzmine.taskcontrol.AbstractTask;
import io.github.mzmine.util.DataPointSorter;
import io.github.mzmine.util.ScanUtils;
import io.github.mzmine.util.SortingDirection;
import io.github.mzmine.util.SortingProperty;
import uk.ac.ebi.pride.tools.jmzreader.JMzReader;
import uk.ac.ebi.pride.tools.jmzreader.JMzReaderException;
import uk.ac.ebi.pride.tools.jmzreader.model.Param;
import uk.ac.ebi.pride.tools.jmzreader.model.Spectrum;
import uk.ac.ebi.pride.tools.jmzreader.model.impl.CvParam;
import uk.ac.ebi.pride.tools.jmzreader.model.impl.ParamGroup;
import uk.ac.ebi.pride.tools.jmzreader.model.impl.UserParam;
import uk.ac.ebi.pride.tools.mzdata_parser.MzDataFile;
import uk.ac.ebi.pride.tools.mzml_wrapper.MzMlWrapper;
import uk.ac.ebi.pride.tools.mzxml_parser.MzXMLFile;
import uk.ac.ebi.pride.tools.mzxml_parser.MzXMLParsingException;

/**
 * This class reads XML-based mass spec data formats (mzData, mzXML, and mzML)
 * using the jmzreader library.
 */
public class XMLReadTask extends AbstractTask {

    private Logger logger = Logger.getLogger(this.getClass().getName());

    private final MZmineProject project;
    private final File sourceFile;
    private final RawDataFileType fileType;

    private RawDataFile newMZmineFile;
    private long totalScans = 0, parsedScans;

    private int lastScanNumber = 0;

    private Map<String, Integer> scanIdTable = new Hashtable<String, Integer>();

    public XMLReadTask(MZmineProject project, File sourceFile,
            RawDataFileType fileType) {
        this.project = project;
        this.sourceFile = sourceFile;
        this.fileType = fileType;
    }

    /**
     * @throws MzXMLParsingException
     * @throws JMzReaderException
     * @throws IOException
     */
    @Override
    public void run() {

        logger.info("Started parsing file " + sourceFile);

        newMZmineFile = MZmineObjectBuilder.getRawDataFile();
        newMZmineFile.setName(sourceFile.getName());

        JMzReader parser = null;

        try {

            switch (fileType) {
            case MZDATA:
                parser = new MzDataFile(sourceFile);
                break;
            case MZML:
                parser = new MzMlWrapper(sourceFile);
                break;
            case MZXML:
                parser = new MzXMLFile(sourceFile);
                break;
            default:
                setErrorMessage(
                        "This reader cannot read file type " + fileType);
                return;
            }
        } catch (Throwable e) {
            e.printStackTrace();
            setErrorMessage("Error opening file: " + e);
            return;
        }

        totalScans = parser.getSpectraCount();

        Iterator<Spectrum> iterator = parser.getSpectrumIterator();

        while (iterator.hasNext()) {

            if (isCanceled())
                return;

            Spectrum spectrum = iterator.next();

            // Create a new MsScan or MsMsScan instance depending on the MS
            // level
            Integer msLevel = spectrum.getMsLevel();
            MsScan scan;
            if ((msLevel != null) && (msLevel > 1)) {
                MsMsScan msMsScan = MZmineObjectBuilder
                        .getMsMsScan(newMZmineFile);
                scan = msMsScan;
                Double precursorMz = spectrum.getPrecursorMZ();
                msMsScan.setPrecursorMz(precursorMz);
                Integer precursorCharge = spectrum.getPrecursorCharge();
                msMsScan.setPrecursorCharge(precursorCharge);
            } else {
                scan = MZmineObjectBuilder.getMsScan(newMZmineFile);
            }

            // Store the scan MS level
            scan.setMSLevel(msLevel);

            // Store the scan number
            String scanId = spectrum.getId();
            int scanNumber = convertScanIdToScanNumber(scanId);
            scan.setScanNumber(scanNumber);

            // Get parent scan number
            int parentScan = extractParentScanNumber(spectrum);

            // Store the chromatography data
            ChromatographyData chromData = extractChromatographyData(spectrum);
            scan.setChromatographyData(chromData);

            // Store the scan data points
            DataPoint dataPoints[] = extractDataPoints(spectrum);
            scan.setDataPoints(dataPoints);

            // Auto-detect whether this scan is centroided
            MsSpectrumType spectrumType = ScanUtils
                    .detectSpectrumType(dataPoints);
            scan.setSpectrumType(spectrumType);

            // Add the scan to the final raw data file
            newMZmineFile.addScan(scan);

            parsedScans++;

        }

        // Add the new file to the project
        project.addFile(newMZmineFile);

        logger.info("Finished importing " + sourceFile + ", parsed "
                + parsedScans + " scans");

    }

    private int convertScanIdToScanNumber(String scanId) {

        if (scanIdTable.containsKey(scanId))
            return scanIdTable.get(scanId);

        final Pattern pattern = Pattern.compile("scan=([0-9]+)");
        final Matcher matcher = pattern.matcher(scanId);
        boolean scanNumberFound = matcher.find();

        // Some vendors include scan=XX in the ID, some don't, such as
        // mzML converted from WIFF files. See the definition of nativeID in
        // http://psidev.cvs.sourceforge.net/viewvc/psidev/psi/psi-ms/mzML/controlledVocabulary/psi-ms.obo
        if (scanNumberFound) {
            int scanNumber = Integer.parseInt(matcher.group(1));
            lastScanNumber = scanNumber;
            scanIdTable.put(scanId, scanNumber);
            return scanNumber;
        }

        int scanNumber = lastScanNumber + 1;
        lastScanNumber++;
        scanIdTable.put(scanId, scanNumber);
        return scanNumber;
    }

    private ChromatographyData extractChromatographyData(Spectrum spectrum) {

        ParamGroup params = spectrum.getAdditional();

        ParamGroup additional = spectrum.getAdditional();
        // access all cvParams in the ParamGroup
        for (CvParam cvParam : additional.getCvParams()) {
            // process the additional information
            System.out.println(cvParam.getAccession() + " - "
                    + cvParam.getName() + " = " + cvParam.getValue());
        }
        // access all userParams in the ParamGroup
        for (UserParam userParam : additional.getUserParams()) {
            // process the information
            System.out.println(
                    userParam.getName() + " = " + userParam.getValue());
        }

        List<CvParam> cvParams = params.getCvParams();
        List<Param> paramsList = params.getParams();

        for (CvParam param : cvParams) {
            String accession = param.getAccession();
            // String unitAccession = param.getUnitAccession();
            String value = param.getValue();
            if ((accession == null) || (value == null))
                continue;

            // Retention time (actually "Scan start time") MS:1000016
            if (accession.equals("MS:1000016")) {
                // MS:1000038 is used in mzML 1.0, while UO:0000031
                // is used in mzML 1.1.0 :-/
                double retentionTime;
                String unitAccession = "UO:0000031";
                if ((unitAccession == null)
                        || (unitAccession.equals("MS:1000038"))
                        || unitAccession.equals("UO:0000031")) {
                    retentionTime = Double.parseDouble(value);
                } else {
                    retentionTime = Double.parseDouble(value) / 60d;
                }
                final ChromatographyData newChromData = MZmineObjectBuilder
                        .getChromatographyData();
                newChromData.setRetentionTime(retentionTime);
                return newChromData;

            }
        }

        return null;
    }

    private DataPoint[] extractDataPoints(Spectrum spectrum) {
        Map<Double, Double> jmzreaderPeakList = spectrum.getPeakList();
        DataPoint dataPoints[] = new DataPoint[jmzreaderPeakList.size()];
        int i = 0;
        for (Double mz : jmzreaderPeakList.keySet()) {
            Double intensity = jmzreaderPeakList.get(mz);
            dataPoints[i] = MZmineObjectBuilder.getDataPoint(mz, intensity);
            i++;
        }
        Arrays.sort(dataPoints, new DataPointSorter(SortingProperty.MZ,
                SortingDirection.Ascending));
        return dataPoints;

    }

    private int extractParentScanNumber(Spectrum spectrum) {

        /*
         * PrecursorList precursorListElement = spectrum.getPrecursorList(); if
         * ((precursorListElement == null) ||
         * (precursorListElement.getCount().equals(0))) return -1;
         * 
         * List<Precursor> precursorList = precursorListElement.getPrecursor();
         * for (Precursor parent : precursorList) { // Get the precursor scan
         * number String precursorScanId = parent.getSpectrumRef(); if
         * (precursorScanId == null) { logger.warning(
         * "Missing precursor spectrumRef tag for spectrum ID " +
         * spectrum.getId()); return -1; } int parentScan =
         * convertScanIdToScanNumber(precursorScanId); return parentScan; }
         */
        return -1;
    }

    @Override
    public String getTaskDescription() {
        return "Importing file " + sourceFile.getName();
    }

    @Override
    public double getFinishedPercentage() {
        return totalScans == 0 ? 0 : (double) parsedScans / totalScans;
    }

}