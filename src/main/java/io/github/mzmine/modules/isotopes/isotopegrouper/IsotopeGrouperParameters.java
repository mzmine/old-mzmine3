/*
 * Copyright 2006-2015 The MZmine 3 Development Team
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

package io.github.mzmine.modules.isotopes.isotopegrouper;

import java.util.Arrays;

import io.github.msdk.util.MZTolerance;
import io.github.msdk.util.RTTolerance;
import io.github.mzmine.parameters.ParameterSet;
import io.github.mzmine.parameters.ParameterValidator;
import io.github.mzmine.parameters.parametertypes.BooleanParameter;
import io.github.mzmine.parameters.parametertypes.ComboParameter;
import io.github.mzmine.parameters.parametertypes.IntegerParameter;
import io.github.mzmine.parameters.parametertypes.StringParameter;
import io.github.mzmine.parameters.parametertypes.selectors.FeatureTablesParameter;
import io.github.mzmine.parameters.parametertypes.tolerances.MZToleranceParameter;
import io.github.mzmine.parameters.parametertypes.tolerances.RTToleranceParameter;

public class IsotopeGrouperParameters extends ParameterSet {

    public static final FeatureTablesParameter featureTables = new FeatureTablesParameter();

    public static final MZToleranceParameter mzTolerance = new MZToleranceParameter(
            "m/z tolerance",
            "Maximum allowed difference between two m/z values to be considered same.\n"
                    + "The value is specified both as absolute tolerance (in m/z) and relative tolerance (in ppm).\n"
                    + "The tolerance range is calculated using maximum of the absolute and relative tolerances.",
            "Algorithm Parameters",
            ParameterValidator.createNonEmptyValidator(),
            new MZTolerance(0.001, 5.0));

    public static final RTToleranceParameter rtTolerance = new RTToleranceParameter(
            "RT tolerance",
            "Maximum allowed retention time difference between the peak apex and the retention times in the above feature input list.",
            "Algorithm Parameters",
            ParameterValidator.createNonEmptyValidator(),
            new RTTolerance(10, true));

    public static final IntegerParameter maximumCharge = new IntegerParameter(
            "Maximum charge",
            "Maximum charge to consider for detecting the isotope patterns.",
            "Algorithm Parameters",
            ParameterValidator.createNonEmptyValidator(), 2);

    public static final ComboParameter<RepresentativeIsotope> representativeIsotope = new ComboParameter<>(
            "Representative isotope",
            "Which peak should represent the whole isotope pattern.\n"
                    + "For small molecular weight compounds with monotonically decreasing isotope pattern, the most intense isotope should be representative.\n"
                    + "For high molecular weight peptides, the lowest m/z isotope may be the representative.",
            "Algorithm Parameters",
            Arrays.asList(RepresentativeIsotope.values()),
            RepresentativeIsotope.MOST_INTENSE);

    public static final BooleanParameter monotonicShape = new BooleanParameter(
            "Monotonic shape?",
            "If checked, then then a monotonically decreasing height of the isotope pattern in required.",
            "Algorithm Parameters", false);

    public static final StringParameter nameSuffix = new StringParameter(
            "Name suffix", "Suffix to be added to the feature table name.",
            "Output", " deisotoped");

    public static final BooleanParameter removeOldTable = new BooleanParameter(
            "Remove original table?",
            "If checked, the original feature table will be removed.", "Output",
            false);

    /**
     * Create the parameter set.
     */
    public IsotopeGrouperParameters() {
        super(featureTables, mzTolerance, rtTolerance, maximumCharge,
                representativeIsotope, monotonicShape, nameSuffix,
                removeOldTable);
    }

}
