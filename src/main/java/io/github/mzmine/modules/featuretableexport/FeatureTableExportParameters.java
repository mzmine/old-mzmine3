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

package io.github.mzmine.modules.featuretableexport;

import java.util.Arrays;
import java.util.LinkedHashMap;

import io.github.mzmine.parameters.ParameterSet;
import io.github.mzmine.parameters.ParameterValidator;
import io.github.mzmine.parameters.parametertypes.StringParameter;
import io.github.mzmine.parameters.parametertypes.ToggleParameterSetParameter;
import io.github.mzmine.parameters.parametertypes.filenames.FileNameParameter;
import io.github.mzmine.parameters.parametertypes.selectors.FeatureTablesParameter;
import javafx.stage.FileChooser.ExtensionFilter;

public class FeatureTableExportParameters extends ParameterSet {

    /*
     * Common parameter
     */
    public static final FeatureTablesParameter featureTables = new FeatureTablesParameter();

    /*
     * CSV parameters
     */
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

    private static final ParameterSet csvParameters = new ParameterSet(
            featureTables, exportFileCSV, separator);

    /*
     * mzTab parameters
     */
    public static final FileNameParameter exportFileMzTab = new FileNameParameter(
            "Output file",
            "Path and name of the exported mzTab file. If the file already exists, it will be overwritten.",
            "Algorithm Parameters",
            ParameterValidator.createNonEmptyValidator(),
            FileNameParameter.Type.SAVE,
            Arrays.asList(new ExtensionFilter("mzTab file", "*.mzTab")));

    private static final ParameterSet mzTabParameters = new ParameterSet(
            featureTables, exportFileCSV, separator);

    private static final ParameterSet mzIdentMLParameters = new ParameterSet(
            featureTables);

    private static final ParameterSet mzQuantMLParameters = new ParameterSet(
            featureTables);

    private static final ParameterSet xmlParameters = new ParameterSet(
            featureTables);

    private static final ParameterSet sqlParameters = new ParameterSet(
            featureTables);

    /*
     * Toggle parameter
     */
    private static final LinkedHashMap<String, ParameterSet> exportFormats = new LinkedHashMap<String, ParameterSet>() {
        private static final long serialVersionUID = 1L;

        {
            put("CSV", csvParameters);
            put("mzTab", mzTabParameters);
            put("mzIdentML", mzIdentMLParameters);
            put("mzQuantML", mzQuantMLParameters);
            put("XML", xmlParameters);
            put("SQL", sqlParameters);
        }
    };
    public static final ToggleParameterSetParameter<String> exportFormat = new ToggleParameterSetParameter<String>(
            "Export format", "Format to export the feature table into.",
            "Algorithm Parameters", exportFormats, "CSV");

    public FeatureTableExportParameters() {
        super(exportFormat);
    }

}
