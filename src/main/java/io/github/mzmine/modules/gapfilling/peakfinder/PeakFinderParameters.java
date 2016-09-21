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

package io.github.mzmine.modules.gapfilling.peakfinder;

import io.github.msdk.util.tolerances.MaximumMzTolerance;
import io.github.msdk.util.tolerances.RTTolerance;
import io.github.mzmine.parameters.ParameterSet;
import io.github.mzmine.parameters.ParameterValidator;
import io.github.mzmine.parameters.parametertypes.BooleanParameter;
import io.github.mzmine.parameters.parametertypes.OptionalParameter;
import io.github.mzmine.parameters.parametertypes.PercentParameter;
import io.github.mzmine.parameters.parametertypes.StringParameter;
import io.github.mzmine.parameters.parametertypes.selectors.FeatureTablesParameter;
import io.github.mzmine.parameters.parametertypes.tolerances.MZToleranceParameter;
import io.github.mzmine.parameters.parametertypes.tolerances.RTToleranceParameter;

public class PeakFinderParameters extends ParameterSet {

    public static final FeatureTablesParameter featureTables = new FeatureTablesParameter();

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
            "Maximum allowed retention time difference between two features.",
            "Algorithm Parameters",
            ParameterValidator.createNonEmptyValidator(),
            new RTTolerance(10, true));

    public static final OptionalParameter<PercentParameter> intensityTolerance = new OptionalParameter<PercentParameter>(
            new PercentParameter("Intensity tolerance",
                    "Maximum allowed deviation of the peak chromatogram from the expected /\\ shape.",
                    "Algorithm Parameters",
                    ParameterValidator.createNonEmptyValidator(), 0.15));

    public static final BooleanParameter sameRT = new BooleanParameter(
            "Use rows RT range?",
            "If checked, the retention time range where the new peaks will be sought are obtained\nusing the ranges of the rest of the peaks in the same row.",
            "Algorithm Parameters", true);

    public static final BooleanParameter sameMZ = new BooleanParameter(
            "Use rows m/z range?",
            "If checked, the m/z range where the new peaks will be sought are obtained using the\nranges of the rest of the peaks in the same row.",
            "Algorithm Parameters", true);

    public static final StringParameter nameSuffix = new StringParameter(
            "Name suffix", "Suffix to be added to the feature table name.",
            "Output", " gapFilled");

    public static final BooleanParameter removeOldTable = new BooleanParameter(
            "Remove original table?",
            "If checked, the original feature table will be removed.", "Output",
            false);

    /**
     * Create the parameter set.
     */
    public PeakFinderParameters() {
        super(featureTables, mzTolerance, rtTolerance, intensityTolerance,
                sameRT, sameMZ, nameSuffix, removeOldTable);
    }

}
