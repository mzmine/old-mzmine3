/*
 * Copyright 2006-2014 The MZmine 2 Development Team
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

package net.sf.mzmine.modules.rawdataimport.fileformats;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.concurrent.Task;
import net.sf.mzmine.datamodel.ChromatographyData;
import net.sf.mzmine.datamodel.DataPoint;
import net.sf.mzmine.datamodel.MassSpectrumType;
import net.sf.mzmine.datamodel.MsScan;
import net.sf.mzmine.datamodel.RawDataFile;
import net.sf.mzmine.datamodel.impl.MZmineObjectBuilder;
import net.sf.mzmine.util.ScanUtils;
import uk.ac.ebi.jmzml.model.mzml.CVParam;
import uk.ac.ebi.jmzml.model.mzml.PrecursorList;
import uk.ac.ebi.jmzml.model.mzml.ScanList;
import uk.ac.ebi.jmzml.model.mzml.SelectedIonList;
import uk.ac.ebi.pride.tools.jmzreader.JMzReader;
import uk.ac.ebi.pride.tools.jmzreader.JMzReaderException;
import uk.ac.ebi.pride.tools.jmzreader.model.Spectrum;
import uk.ac.ebi.pride.tools.jmzreader.model.impl.ParamGroup;
import uk.ac.ebi.pride.tools.mzdata_parser.MzDataFile;
import uk.ac.ebi.pride.tools.mzdata_parser.mzdata.model.Precursor;
import uk.ac.ebi.pride.tools.mzml_wrapper.MzMlWrapper;
import uk.ac.ebi.pride.tools.mzxml_parser.MzXMLFile;
import uk.ac.ebi.pride.tools.mzxml_parser.MzXMLParsingException;
import uk.ac.ebi.pride.tools.mzxml_parser.mzxml.model.Scan;

/**
 * This class reads XML-based mass spec data formats (mzData, mzXML, and mzML)
 * using the jmzreader library.
 */
public class XMLReadTask extends Task<RawDataFile> {

    private Logger logger = Logger.getLogger(this.getClass().getName());

    private File sourceFile;
    private RawDataFile newMZmineFile;
    private long totalScans = 0, parsedScans;

    private int lastScanNumber = 0;

    private Map<String, Integer> scanIdTable = new Hashtable<String, Integer>();

    public XMLReadTask(File sourceFile) {
	this.sourceFile = sourceFile;

	updateTitle("Importing file " + sourceFile.getName());
    }

    /**
     * @throws MzXMLParsingException
     * @throws JMzReaderException
     * @throws IOException
     * @see java.lang.Runnable#run()
     */
    @Override
    public RawDataFile call() throws MzXMLParsingException, JMzReaderException,
	    IOException {

	logger.info("Started parsing file " + sourceFile);

	newMZmineFile = MZmineObjectBuilder.getRawDataFile();
	newMZmineFile.setName(sourceFile.getName());

	JMzReader parser = null;

	// Check the first 512 bytes of the file, to determine the file type
	FileReader reader = new FileReader(sourceFile);
	char buffer[] = new char[512];
	reader.read(buffer);
	reader.close();
	String fileHeader = new String(buffer);
	if (fileHeader.contains("mzXML")) {
	    parser = new MzXMLFile(sourceFile);
	}
	if (fileHeader.contains("mzData")) {
	    parser = new MzDataFile(sourceFile);
	}
	if (fileHeader.contains("mzML")) {
	    parser = new MzMlWrapper(sourceFile);
	}

	if (parser == null) {
	    updateMessage("Cannot determine file type of file " + sourceFile);
	    return null;
	}

	totalScans = parser.getSpectraCount();

	Iterator<Spectrum> iterator = parser.getSpectrumIterator();

	while (iterator.hasNext()) {

	    if (isCancelled())
		return null;

	    Spectrum spectrum = iterator.next();

	    String scanId = spectrum.getId();
	    int scanNumber = convertScanIdToScanNumber(scanId);

	    Integer msLevel = spectrum.getMsLevel();
	    ChromatographyData chromData = extractChromatography(spectrum);

	    // Get parent scan number
	    int parentScan = extractParentScanNumber(spectrum);
	    Double precursorMz = spectrum.getPrecursorMZ();
	    Integer precursorCharge = spectrum.getPrecursorCharge();

	    DataPoint dataPoints[] = extractDataPoints(spectrum);

	    // Auto-detect whether this scan is centroided
	    MassSpectrumType spectrumType = ScanUtils
		    .detectSpectrumType(dataPoints);

	    MsScan scan = null;

	    if ((msLevel != null) && (msLevel > 1))
		scan = MZmineObjectBuilder.getMsMsScan(newMZmineFile);
	    else
		scan = MZmineObjectBuilder.getMsScan(newMZmineFile);

	    newMZmineFile.addScan(scan);

	    parsedScans++;
	    updateProgress(parsedScans, totalScans);

	}

	logger.info("Finished importing " + sourceFile + ", parsed "
		+ parsedScans + " scans");

	return newMZmineFile;

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

    private ChromatographyData extractChromatography(Spectrum spectrum) {

	ParamGroup params = spectrum.getAdditional();
	params.getCvParams();
	
	ScanList scanListElement = spectrum.getScanList();
	if (scanListElement == null)
	    return 0;
	List<Scan> scanElements = scanListElement.getScan();
	if (scanElements == null)
	    return 0;

	for (Scan scan : scanElements) {
	    List<CVParam> cvParams = scan.getCvParam();
	    if (cvParams == null)
		continue;

	    for (CVParam param : cvParams) {
		String accession = param.getAccession();
		String unitAccession = param.getUnitAccession();
		String value = param.getValue();
		if ((accession == null) || (value == null))
		    continue;

		// Retention time (actually "Scan start time") MS:100001
		if (accession.equals("MS:1000016")) {
		    // MS:1000038 is used in mzML 1.0, while UO:0000031
		    // is used in mzML 1.1.0 :-/
		    double retentionTime;
		    if ((unitAccession == null)
			    || (unitAccession.equals("MS:1000038"))
			    || unitAccession.equals("UO:0000031")) {
			retentionTime = Double.parseDouble(value);
		    } else {
			retentionTime = Double.parseDouble(value) / 60d;
		    }
		    return retentionTime;

		}
	    }
	}

	return 0;
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
	return dataPoints;

    }

    private int extractParentScanNumber(Spectrum spectrum) {
	PrecursorList precursorListElement = spectrum.getPrecursorList();
	if ((precursorListElement == null)
		|| (precursorListElement.getCount().equals(0)))
	    return -1;

	List<Precursor> precursorList = precursorListElement.getPrecursor();
	for (Precursor parent : precursorList) {
	    // Get the precursor scan number
	    String precursorScanId = parent.getSpectrumRef();
	    if (precursorScanId == null) {
		logger.warning("Missing precursor spectrumRef tag for spectrum ID "
			+ spectrum.getId());
		return -1;
	    }
	    int parentScan = convertScanIdToScanNumber(precursorScanId);
	    return parentScan;
	}
	return -1;
    }

    private double extractPrecursorMz(Spectrum spectrum) {

	PrecursorList precursorListElement = spectrum.getPrecursorList();
	if ((precursorListElement == null)
		|| (precursorListElement.getCount().equals(0)))
	    return 0;

	List<Precursor> precursorList = precursorListElement.getPrecursor();
	for (Precursor parent : precursorList) {

	    SelectedIonList selectedIonListElement = parent
		    .getSelectedIonList();
	    if ((selectedIonListElement == null)
		    || (selectedIonListElement.getCount().equals(0)))
		return 0;
	    List<ParamGroup> selectedIonParams = selectedIonListElement
		    .getSelectedIon();
	    if (selectedIonParams == null)
		continue;

	    for (ParamGroup pg : selectedIonParams) {
		List<CVParam> pgCvParams = pg.getCvParam();
		for (CVParam param : pgCvParams) {
		    String accession = param.getAccession();
		    String value = param.getValue();
		    if ((accession == null) || (value == null))
			continue;
		    // MS:1000040 is used in mzML 1.0,
		    // MS:1000744 is used in mzML 1.1.0
		    if (accession.equals("MS:1000040")
			    || accession.equals("MS:1000744")) {
			double precursorMz = Double.parseDouble(value);
			return precursorMz;
		    }
		}

	    }
	}
	return 0;
    }

    private int extractPrecursorCharge(Spectrum spectrum) {
	PrecursorList precursorListElement = spectrum.getPrecursorList();
	if ((precursorListElement == null)
		|| (precursorListElement.getCount().equals(0)))
	    return 0;

	List<Precursor> precursorList = precursorListElement.getPrecursor();
	for (Precursor parent : precursorList) {

	    SelectedIonList selectedIonListElement = parent
		    .getSelectedIonList();
	    if ((selectedIonListElement == null)
		    || (selectedIonListElement.getCount().equals(0)))
		return 0;
	    List<ParamGroup> selectedIonParams = selectedIonListElement
		    .getSelectedIon();
	    if (selectedIonParams == null)
		continue;

	    for (ParamGroup pg : selectedIonParams) {
		List<CVParam> pgCvParams = pg.getCvParam();
		for (CVParam param : pgCvParams) {
		    String accession = param.getAccession();
		    String value = param.getValue();
		    if ((accession == null) || (value == null))
			continue;
		    if (accession.equals("MS:1000041")) {
			int precursorCharge = Integer.parseInt(value);
			return precursorCharge;
		    }

		}

	    }
	}
	return 0;
    }

}