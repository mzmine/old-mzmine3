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
 * MZmine 2; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

package net.sf.mzmine.modules.rawdatamethods.peakpicking.manual;

import javax.annotation.Nonnull;

import net.sf.mzmine.datamodel.Feature;
import net.sf.mzmine.datamodel.PeakListRow;
import net.sf.mzmine.datamodel.RawDataFile;
import net.sf.mzmine.main.MZmineCore;
import net.sf.mzmine.modules.MZmineModule;
import net.sf.mzmine.parameters.ParameterSet;
import net.sf.mzmine.util.ExitCode;
import net.sf.mzmine.util.Range;

public class ManualPeakPickerModule implements MZmineModule {

    /**
     * @see net.sf.mzmine.modules.MZmineProcessingModule#getName()
     */
    public @Nonnull String getName() {
	return "Manual peak detector";
    }

    public static ExitCode runManualDetection(RawDataFile dataFile,
	    PeakListRow peakListRow) {
	return runManualDetection(new RawDataFile[] { dataFile }, peakListRow);
    }

    public static ExitCode runManualDetection(RawDataFile dataFiles[],
	    PeakListRow peakListRow) {

	Range mzRange = null, rtRange = null;

	// Check the peaks for selected data files
	for (RawDataFile dataFile : dataFiles) {
	    Feature peak = peakListRow.getPeak(dataFile);
	    if (peak == null)
		continue;
	    if ((mzRange == null) || (rtRange == null)) {
		mzRange = new Range(peak.getRawDataPointsMZRange());
		rtRange = new Range(peak.getRawDataPointsRTRange());
	    } else {
		mzRange.extendRange(peak.getRawDataPointsMZRange());
		rtRange.extendRange(peak.getRawDataPointsRTRange());
	    }

	}

	// If none of the data files had a peak, check the whole row
	if (mzRange == null) {
	    for (Feature peak : peakListRow.getPeaks()) {
		if (peak == null)
		    continue;
		if ((mzRange == null) || (rtRange == null)) {
		    mzRange = new Range(peak.getRawDataPointsMZRange());
		    rtRange = new Range(peak.getRawDataPointsRTRange());
		} else {
		    mzRange.extendRange(peak.getRawDataPointsMZRange());
		    rtRange.extendRange(peak.getRawDataPointsRTRange());
		}

	    }
	}

	ManualPickerParameters parameters = new ManualPickerParameters();

	if (mzRange != null) {
	    parameters.getParameter(ManualPickerParameters.retentionTimeRange)
		    .setValue(rtRange);
	    parameters.getParameter(ManualPickerParameters.mzRange).setValue(
		    mzRange);
	}

	ExitCode exitCode = parameters.showSetupDialog();

	if (exitCode != ExitCode.OK)
	    return exitCode;

	ManualPickerTask task = new ManualPickerTask(peakListRow, dataFiles,
		parameters);

	MZmineCore.getTaskController().addTask(task);
	return exitCode;
    }

    @Override
    public @Nonnull Class<? extends ParameterSet> getParameterSetClass() {
	return ManualPickerParameters.class;
    }

}
