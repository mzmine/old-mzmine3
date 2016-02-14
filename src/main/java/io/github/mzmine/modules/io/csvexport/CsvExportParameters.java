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

package io.github.mzmine.modules.io.csvexport;

import java.util.Arrays;

import io.github.mzmine.parameters.ParameterSet;
import io.github.mzmine.parameters.ParameterValidator;
import io.github.mzmine.parameters.parametertypes.BooleanParameter;
import io.github.mzmine.parameters.parametertypes.StringParameter;
import io.github.mzmine.parameters.parametertypes.filenames.FileNameParameter;
import io.github.mzmine.parameters.parametertypes.selectors.FeatureTableColumnsParameter;
import io.github.mzmine.parameters.parametertypes.selectors.FeatureTablesParameter;
import javafx.stage.FileChooser.ExtensionFilter;

public class CsvExportParameters extends ParameterSet {

    public static final FeatureTablesParameter featureTables = new FeatureTablesParameter();

    public static final FileNameParameter exportFileCSV = new FileNameParameter(
            "Output file",
            "Path and name of the exported CSV file. If the file already exists, it will be overwritten.",
            "Algorithm Parameters",
            ParameterValidator.createNonEmptyValidator(),
            FileNameParameter.Type.SAVE,
            Arrays.asList(new ExtensionFilter("CSV file", "*.csv")));

    public static final StringParameter separator = new StringParameter(
            "Field separator",
            "Character(s) used to separate fields in the exported file.",
            "Algorithm Parameters",
            ParameterValidator.createNonEmptyValidator(), ",");

    public static final StringParameter separatorIDs = new StringParameter(
            "Identification separator",
            "Character(s) used to separate identification results in the exported file.",
            "Algorithm Parameters",
            ParameterValidator.createNonEmptyValidator(), ";");

    public static final BooleanParameter exportAllIds = new BooleanParameter(
            "Export all feature identifications?",
            "If checked, all possible identifications of a feature will be exported.",
            "Algorithm Parameters", false);

    public static final FeatureTableColumnsParameter tableColumns = new FeatureTableColumnsParameter();

    public CsvExportParameters() {
        super(featureTables, exportFileCSV, separator, separatorIDs,
                exportAllIds, tableColumns);
    }

}
