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

package io.github.mzmine.modules.alignment.joinaligner;

import io.github.msdk.util.tolerances.MaximumMzTolerance;
import io.github.msdk.util.tolerances.RTTolerance;
import io.github.mzmine.parameters.ParameterSet;
import io.github.mzmine.parameters.ParameterValidator;
import io.github.mzmine.parameters.parametertypes.BooleanParameter;
import io.github.mzmine.parameters.parametertypes.IntegerParameter;
import io.github.mzmine.parameters.parametertypes.StringParameter;
import io.github.mzmine.parameters.parametertypes.selectors.FeatureTablesParameter;
import io.github.mzmine.parameters.parametertypes.tolerances.MZToleranceParameter;
import io.github.mzmine.parameters.parametertypes.tolerances.RTToleranceParameter;

public class JoinAlignerParameters extends ParameterSet {

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

    public static final IntegerParameter mzWeight = new IntegerParameter(
            "Weight for m/z", "Score for perfectly matching m/z values",
            "Algorithm Parameters",
            ParameterValidator.createNonEmptyValidator(), 100);

    public static final IntegerParameter rtWeight = new IntegerParameter(
            "Weight for RT",
            "Score for perfectly matching retention time (RT) values",
            "Algorithm Parameters",
            ParameterValidator.createNonEmptyValidator(), 100);

    public static final BooleanParameter requireSameAnnotation = new BooleanParameter(
            "Require same annotation?",
            "If checked, only features with the same annoation (or no annotation) will be aligned.",
            "Algorithm Parameters", false);

    public static final BooleanParameter requireSameCharge = new BooleanParameter(
            "Require same charge?",
            "If checked, only features with the same charge will be aligned.",
            "Algorithm Parameters", false);

    public static final StringParameter featureTableName = new StringParameter(
            "Feature table name", "Name of the aligned feature table.",
            "Output", "Aligned Feature Table");

    public static final BooleanParameter removeOldTable = new BooleanParameter(
            "Remove original tables?",
            "If checked, the original feature tables will be removed.",
            "Output", false);

    /**
     * Create the parameter set.
     */
    public JoinAlignerParameters() {
        super(featureTables, mzTolerance, rtTolerance, mzWeight, rtWeight,
                requireSameAnnotation, requireSameCharge, featureTableName,
                removeOldTable);
    }

}
