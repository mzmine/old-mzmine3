/*
 * Copyright 2006-2016 The MZmine 3 Development Team
 * 
 * This file is part of MZmine 3.
 * 
 * MZmine 3 is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * MZmine 3 is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * MZmine 3; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

package io.github.mzmine.modules.featuredetection.msmsdetection;

import io.github.msdk.util.tolerances.MaximumMzTolerance;
import io.github.msdk.util.tolerances.RTTolerance;
import io.github.mzmine.parameters.ParameterSet;
import io.github.mzmine.parameters.ParameterValidator;
import io.github.mzmine.parameters.parametertypes.PercentParameter;
import io.github.mzmine.parameters.parametertypes.StringParameter;
import io.github.mzmine.parameters.parametertypes.selectors.RawDataFilesParameter;
import io.github.mzmine.parameters.parametertypes.selectors.ScanSelection;
import io.github.mzmine.parameters.parametertypes.selectors.ScanSelectionParameter;
import io.github.mzmine.parameters.parametertypes.tolerances.MZToleranceParameter;
import io.github.mzmine.parameters.parametertypes.tolerances.RTToleranceParameter;

public class MsMsDetectionParameters extends ParameterSet {

    public static final RawDataFilesParameter rawDataFiles = new RawDataFilesParameter();

    public static final ScanSelectionParameter scanSelection = new ScanSelectionParameter(
            new ScanSelection(2));

    public static final MZToleranceParameter mzTolerance = new MZToleranceParameter(
            "m/z tolerance",
            "Maximum allowed difference between two m/z values to be considered same.\n"
                    + "The value is specified both as absolute tolerance (in m/z) and relative tolerance (in ppm).\n"
                    + "The tolerance range is calculated using maximum of the absolute and relative tolerances.",
            "Algorithm Parameters",
            ParameterValidator.createNonEmptyValidator(),
            new MaximumMzTolerance(0.001, 5.0));

    public static final RTToleranceParameter rtTolerance = new RTToleranceParameter(
            "RT tolerance",
            "Maximum allowed retention time difference between the peak apex and the retention times in the above feature input list.",
            "Algorithm Parameters",
            ParameterValidator.createNonEmptyValidator(),
            new RTTolerance(10, true));

    public static final PercentParameter intensityTolerance = new PercentParameter(
            "Intensity tolerance",
            "Maximum allowed deviation of the peak chromatogram from the expected /\\ shape.",
            "Algorithm Parameters",
            ParameterValidator.createNonEmptyValidator(), 0.15);

    public static final StringParameter nameSuffix = new StringParameter(
            "Name suffix",
            "Suffix to be added to the raw data file(s) when creating the feature table(s)",
            "Output", " msmsDetection");

    /**
     * Create the parameter set.
     */
    public MsMsDetectionParameters() {
        super(rawDataFiles, scanSelection, mzTolerance, rtTolerance,
                intensityTolerance, nameSuffix);
    }

}
