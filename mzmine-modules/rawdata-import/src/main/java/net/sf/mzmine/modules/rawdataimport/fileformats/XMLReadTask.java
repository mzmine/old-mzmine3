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
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.mzmine.datamodel.DataPoint;
import net.sf.mzmine.datamodel.RawDataFile;
import net.sf.mzmine.datamodel.impl.MZmineObjectBuilder;
import net.sf.mzmine.taskcontrol.AbstractTask;
import net.sf.mzmine.taskcontrol.TaskStatus;
import net.sf.mzmine.util.ScanUtils;

import org.apache.http.util.ExceptionUtils;

import uk.ac.ebi.jmzml.model.mzml.BinaryDataArray;
import uk.ac.ebi.jmzml.model.mzml.BinaryDataArrayList;
import uk.ac.ebi.jmzml.model.mzml.CVParam;
import uk.ac.ebi.jmzml.model.mzml.PrecursorList;
import uk.ac.ebi.jmzml.model.mzml.ScanList;
import uk.ac.ebi.jmzml.model.mzml.SelectedIonList;
import uk.ac.ebi.pride.tools.jmzreader.JMzReader;
import uk.ac.ebi.pride.tools.jmzreader.model.Spectrum;
import uk.ac.ebi.pride.tools.jmzreader.model.impl.ParamGroup;
import uk.ac.ebi.pride.tools.mzdata_parser.MzDataFile;
import uk.ac.ebi.pride.tools.mzdata_parser.mzdata.model.Precursor;
import uk.ac.ebi.pride.tools.mzml_wrapper.MzMlWrapper;
import uk.ac.ebi.pride.tools.mzxml_parser.MzXMLFile;
import uk.ac.ebi.pride.tools.mzxml_parser.mzxml.model.Scan;

/**
 * This class reads XML-based mass spec data formats (mzData, mzXML, and mzML)
 * using the jmzreader library.
 */
public class XMLReadTask extends AbstractTask {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	private File file;
	private RawDataFile newMZmineFile;
	private int totalScans = 0, parsedScans;

	private int lastScanNumber = 0;

	private Map<String, Integer> scanIdTable = new Hashtable<String, Integer>();

	public XMLReadTask(File fileToOpen, RawDataFile newMZmineFile) {
		this.file = fileToOpen;
		this.newMZmineFile = newMZmineFile;
	}

	/**
	 * @see net.sf.mzmine.taskcontrol.Task#getFinishedPercentage()
	 */
	@Override
	public double getFinishedPercentage() {
		return totalScans == 0 ? 0.0 : (double) parsedScans / totalScans;
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {

		setStatus(TaskStatus.PROCESSING);
		logger.info("Started parsing file " + file);

		JMzReader parser;

		// Check the first 512 bytes of the file, to determine the file type
		FileReader reader = new FileReader(file);
		char buffer[] = new char[512];
		reader.read(buffer);
		reader.close();
		String fileHeader = new String(buffer);
		if (fileHeader.contains("mzXML")) {
			parser = new MzXMLFile(file);
		}
		if (fileHeader.contains("mzData")) {
			parser = new MzDataFile(file);
		}
		if (fileHeader.contains("mzML")) {
			parser = new MzMlWrapper(file);
		}

		if (parser == null) {
			setErrorMessage("Cannot determine file type of file " + file);
			return;
		}

		totalScans = parser.getSpectraCount();

		Iterator<Spectrum> iterator = parser.getSpectrumIterator();

		try {

			while (iterator.hasNext()) {
				Spectrum spectrum = iterator.next();

				String scanId = spectrum.getId();
				int scanNumber = convertScanIdToScanNumber(scanId);

				int msLevel = extractMSLevel(spectrum);
				double retentionTime = extractRetentionTime(spectrum);

				// Get parent scan number
				int parentScan = extractParentScanNumber(spectrum);
				double precursorMz = extractPrecursorMz(spectrum);
				int precursorCharge = extractPrecursorCharge(spectrum);

				DataPoint dataPoints[] = extractDataPoints(spectrum);

				// Auto-detect whether this scan is centroided
				boolean centroided = ScanUtils.isCentroided(dataPoints);

				SimpleScan scan = new SimpleScan(null, scanNumber, msLevel,
						retentionTime, parentScan, precursorMz,
						precursorCharge, null, optimizedDataPoints, centroided);

				for (SimpleScan s : parentStack) {
					if (s.getScanNumber() == parentScan) {
						s.addFragmentScan(scanNumber);
					}
				}

				newMZmineFile.addScan(scan);

				parsedScans++;

			}

		
		} catch (Throwable e) {
			setStatus(TaskStatus.ERROR);
			errorMessage = "Error parsing mzML: "
					+ ExceptionUtils.exceptionToString(e);
			e.printStackTrace();
			return;
		}

		if (parsedScans == 0) {
			setStatus(TaskStatus.ERROR);
			errorMessage = "No scans found";
			return;
		}

		logger.info("Finished parsing " + file + ", parsed " + parsedScans
				+ " scans");
		setStatus(TaskStatus.FINISHED);

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
			scanIdTable.put(scanId, scanNumber);
			return scanNumber;
		}

		int scanNumber = lastScanNumber + 1;
		lastScanNumber++;
		scanIdTable.put(scanId, scanNumber);
		return scanNumber;
	}

	private int extractMSLevel(Spectrum spectrum) {
		// Browse the spectrum parameters
		List<CVParam> cvParams = spectrum.getCvParam();
		if (cvParams == null)
			return 1;
		for (CVParam param : cvParams) {
			String accession = param.getAccession();
			String value = param.getValue();
			if ((accession == null) || (value == null))
				continue;

			// MS level MS:1000511
			if (accession.equals("MS:1000511")) {
				int msLevel = Integer.parseInt(value);
				return msLevel;
			}
		}
		return 1;
	}

	private double extractRetentionTime(Spectrum spectrum) {

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
		BinaryDataArrayList dataList = spectrum.getBinaryDataArrayList();

		if ((dataList == null) || (dataList.getCount().equals(0)))
			return new DataPoint[0];

		BinaryDataArray mzArray = dataList.getBinaryDataArray().get(0);
		BinaryDataArray intensityArray = dataList.getBinaryDataArray().get(1);
		Number mzValues[] = mzArray.getBinaryDataAsNumberArray();
		Number intensityValues[] = intensityArray.getBinaryDataAsNumberArray();
		DataPoint dataPoints[] = new DataPoint[mzValues.length];
		for (int i = 0; i < dataPoints.length; i++) {
			double mz = mzValues[i].doubleValue();
			double intensity = intensityValues[i].doubleValue();
			dataPoints[i] = MZmineObjectBuilder.getDataPoint(mz, intensity);
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

	@Override
	public String getTaskDescription() {
		return "Opening file" + file;
	}

}