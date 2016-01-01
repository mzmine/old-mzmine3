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

package io.github.mzmine.modules.filtering.rowfilter;

import com.google.common.collect.Range;

import io.github.mzmine.parameters.ParameterSet;
import io.github.mzmine.parameters.ParameterValidator;
import io.github.mzmine.parameters.parametertypes.BooleanParameter;
import io.github.mzmine.parameters.parametertypes.IntegerParameter;
import io.github.mzmine.parameters.parametertypes.OptionalModuleParameter;
import io.github.mzmine.parameters.parametertypes.OptionalParameter;
import io.github.mzmine.parameters.parametertypes.StringParameter;
import io.github.mzmine.parameters.parametertypes.ranges.DoubleRangeParameter;
import io.github.mzmine.parameters.parametertypes.selectors.FeatureTablesParameter;

public class RowFilterParameters extends ParameterSet {

    public static final FeatureTablesParameter featureTables = new FeatureTablesParameter();

    public static final OptionalParameter<DoubleRangeParameter> mzRange = new OptionalParameter<DoubleRangeParameter>(
            new DoubleRangeParameter("m/z range",
                    "Range of acceptable m/z values.", "Filters",
                    ParameterValidator.createNonEmptyValidator(),
                    Range.closed(200.0, 1700.0)));

    public static final OptionalParameter<DoubleRangeParameter> rtRange = new OptionalParameter<DoubleRangeParameter>(
            new DoubleRangeParameter("Retention time range (min)",
                    "Range of acceptable retention time values.", "Filters",
                    ParameterValidator.createNonEmptyValidator(),
                    Range.closed(0.0, 30.0)));

    public static final OptionalParameter<DoubleRangeParameter> durationRange = new OptionalParameter<DoubleRangeParameter>(
            new DoubleRangeParameter("Duration range (sec)",
                    "Range of acceptable average feature duration.", "Filters",
                    ParameterValidator.createNonEmptyValidator(),
                    Range.closed(0.0, 60.0)));

    public static final OptionalParameter<IntegerParameter> minCount = new OptionalParameter<IntegerParameter>(
            new IntegerParameter("Minimum features in a row",
                    "Minimum number of feature detections required per row.",
                    "Filters", ParameterValidator.createNonEmptyValidator()));

    public static final OptionalParameter<IntegerParameter> minIsotopes = new OptionalParameter<IntegerParameter>(
            new IntegerParameter("Minimum peaks in isotope pattern",
                    "Minimum number of peaks required in an isotope pattern.",
                    "Filters", ParameterValidator.createNonEmptyValidator()));

    public static final OptionalParameter<StringParameter> ionAnnotation = new OptionalParameter<StringParameter>(
            new StringParameter("Text in ion annotation",
                    "Required text in feature annoation.", "Filters", ""));

    public static final BooleanParameter requireAnnotation = new BooleanParameter(
            "Remove non-identified features?",
            "If selected, non-identified features will be removed.", "Filters",
            false);

    public static final OptionalModuleParameter removeDuplicates = new OptionalModuleParameter(
            "Remove duplicate features?",
            "Remove duplicate features from the feature list?\nDuplicates are found based on m/z, RT and feature annotation.",
            "Filters", new DuplicateFilterParameters());

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
    public RowFilterParameters() {
        super(featureTables, mzRange, rtRange, durationRange, minCount,
                minIsotopes, ionAnnotation, requireAnnotation, removeDuplicates,
                nameSuffix, removeOldTable);
    }

}
