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

package io.github.mzmine.modules.filtering.rowfilter;

import io.github.mzmine.gui.preferences.ProxySettings;
import io.github.mzmine.parameters.ParameterSet;
import io.github.mzmine.parameters.ParameterValidator;
import io.github.mzmine.parameters.parametertypes.BooleanParameter;
import io.github.mzmine.parameters.parametertypes.IntegerParameter;
import io.github.mzmine.parameters.parametertypes.OptionalModuleParameter;
import io.github.mzmine.parameters.parametertypes.StringParameter;
import io.github.mzmine.parameters.parametertypes.selectors.FeatureTablesParameter;

public class RowFilterParameters extends ParameterSet {

    public static final FeatureTablesParameter featureTables = new FeatureTablesParameter();

    public static final IntegerParameter minCount = new IntegerParameter(
            "Minimum features in a row",
            "Minimum number of feature detections required per row.", "Filters",
            ParameterValidator.createNonEmptyValidator());

    public static final IntegerParameter minIsotopes = new IntegerParameter(
            "Minimum peaks in isotope pattern",
            "Minimum number of peaks required in an isotope pattern.", "Filters",
            ParameterValidator.createNonEmptyValidator());

    /*
     * TODO: Add m/z range, RT range
     */

    public static final StringParameter featureAnnotation = new StringParameter(
            "Required feature annotation",
            "Required text in feature annoation.", "Filters", "");

    public static final BooleanParameter requireAnnotation = new BooleanParameter(
            "Remove non-identified features?",
            "If selected, non-identified features will be removed.", "Filters",
            false);

    /*
     * TODO: Change ParameterSet to duplicate feature remover
     */
    public static final OptionalModuleParameter removeDuplicates = new OptionalModuleParameter(
            "Remove duplicate features?",
            "Remove duplicate features from the feature list?\nDuplicates are found based on m/z, RT and feature annotation.",
            "Filters", new ProxySettings());

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
        super(featureTables, minCount, minIsotopes, featureAnnotation,
                requireAnnotation, removeDuplicates, nameSuffix,
                removeOldTable);
    }

}
