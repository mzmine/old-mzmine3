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

package io.github.mzmine.modules.filtering.featurefilter;

import com.google.common.collect.Range;

import io.github.mzmine.main.MZmineCore;
import io.github.mzmine.parameters.ParameterSet;
import io.github.mzmine.parameters.ParameterValidator;
import io.github.mzmine.parameters.parametertypes.BooleanParameter;
import io.github.mzmine.parameters.parametertypes.OptionalParameter;
import io.github.mzmine.parameters.parametertypes.StringParameter;
import io.github.mzmine.parameters.parametertypes.ranges.DoubleRangeParameter;
import io.github.mzmine.parameters.parametertypes.ranges.IntegerRangeParameter;
import io.github.mzmine.parameters.parametertypes.selectors.FeatureTablesParameter;

public class FeatureFilterParameters extends ParameterSet {

    public static final FeatureTablesParameter featureTables = new FeatureTablesParameter();

    public static final OptionalParameter<DoubleRangeParameter> duration = new OptionalParameter<DoubleRangeParameter>(
            new DoubleRangeParameter("Duration (sec)",
                    "Permissible range of peak durations.", "Filters",
                    MZmineCore.getConfiguration().getRTFormat(),
                    ParameterValidator.createNonEmptyValidator(),
                    Range.closed(0.0, 20.0)));

    public static final OptionalParameter<DoubleRangeParameter> area = new OptionalParameter<DoubleRangeParameter>(
            new DoubleRangeParameter("Area", "Permissible range of peak areas.",
                    "Filters",
                    MZmineCore.getConfiguration().getIntensityFormat(),
                    ParameterValidator.createNonEmptyValidator(),
                    Range.closed(0.0, 1E7)));

    public static final OptionalParameter<DoubleRangeParameter> height = new OptionalParameter<DoubleRangeParameter>(
            new DoubleRangeParameter("Height",
                    "Permissible range of peak heights.", "Filters",
                    MZmineCore.getConfiguration().getIntensityFormat(),
                    ParameterValidator.createNonEmptyValidator(),
                    Range.closed(0.0, 1E7)));

    public static final OptionalParameter<IntegerRangeParameter> dataPoints = new OptionalParameter<IntegerRangeParameter>(
            new IntegerRangeParameter("# data points",
                    "Permissible range of the number of data points over the peak.",
                    "Filters", ParameterValidator.createNonEmptyValidator(),
                    Range.closed(8, 999)));

    public static final OptionalParameter<DoubleRangeParameter> fwhm = new OptionalParameter<DoubleRangeParameter>(
            new DoubleRangeParameter("FWHM (sec)",
                    "Permissible range of full width at half maximum (FWHM) for a peak.",
                    "Filters", MZmineCore.getConfiguration().getRTFormat(),
                    ParameterValidator.createNonEmptyValidator(),
                    Range.closed(0.0, 10.0)));

    public static final OptionalParameter<DoubleRangeParameter> tailingFactor = new OptionalParameter<DoubleRangeParameter>(
            new DoubleRangeParameter("Tailing factor",
                    "Permissible range of tailing factor for a peak.",
                    "Filters", ParameterValidator.createNonEmptyValidator(),
                    Range.closed(0.5, 2.0)));

    public static final OptionalParameter<DoubleRangeParameter> asymmetryFactor = new OptionalParameter<DoubleRangeParameter>(
            new DoubleRangeParameter("Asymmetry factor",
                    "Permissible range of asymmetry factor for a peak.",
                    "Filters", ParameterValidator.createNonEmptyValidator(),
                    Range.closed(0.5, 2.0)));

    public static final StringParameter nameSuffix = new StringParameter(
            "Name suffix", "Suffix to be added to the feature table name.",
            "Output", " rowFiltered");

    public static final BooleanParameter removeOldTable = new BooleanParameter(
            "Remove original table?",
            "If checked, the original feature table will be removed.", "Output",
            false);

    /**
     * Create the parameter set.
     */
    public FeatureFilterParameters() {
        super(featureTables, duration, area, height, dataPoints, fwhm,
                tailingFactor, asymmetryFactor, nameSuffix, removeOldTable);
    }

}
