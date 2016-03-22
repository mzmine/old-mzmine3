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

package io.github.mzmine.modules.rawdata.rawdataimport;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.controlsfx.property.editor.PropertyEditor;

import com.google.common.base.Strings;

import io.github.mzmine.parameters.ParameterSet;
import io.github.mzmine.parameters.ParameterSheetView;
import io.github.mzmine.parameters.ParameterValidator;
import io.github.mzmine.parameters.parametertypes.ComboParameter;
import io.github.mzmine.parameters.parametertypes.StringParameter;
import io.github.mzmine.parameters.parametertypes.filenames.FileNamesParameter;
import io.github.mzmine.util.FileNameUtil;
import io.github.mzmine.util.JavaFXUtil;
import javafx.scene.Node;
import javafx.stage.FileChooser.ExtensionFilter;

public class RawDataImportParameters extends ParameterSet {

    private static final ExtensionFilter filters[] = new ExtensionFilter[] {
            new ExtensionFilter("All raw data files", "*.csv", "*.cdf", "*.nc",
                    "*.mzData", "*.mzML", "*.mzXML", "*.raw", "*.xml"),
            new ExtensionFilter("Agilent CSV files", "*.csv"),
            new ExtensionFilter("mzData files", "*.mzData"),
            new ExtensionFilter("mzML files", "*.mzML"),
            new ExtensionFilter("mzXML files", "*.mzXML"),
            new ExtensionFilter("NetCDF files", "*.cdf", "*.nc"),
            new ExtensionFilter("Waters RAW folders", "*.raw"),
            new ExtensionFilter("XCalibur RAW files", "*.raw"),
            new ExtensionFilter("XML files", "*.xml"),
            new ExtensionFilter("All files", "*.*") };

    public static final FileNamesParameter fileNames = new FileNamesParameter(
            "File names", "Add raw data files", "Input files",
            ParameterValidator.createNonEmptyValidator(),
            Arrays.asList(filters));

    public static final ComboParameter<RawDataImportMode> importMode = new ComboParameter<>(
            "Import mode", "Select how the raw data points will be handled",
            "Mode", Arrays.asList(RawDataImportMode.values()),
            RawDataImportMode.TRANSPARENT);

    public static final StringParameter removePrefix = new StringParameter(
            "Remove prefix", "Prefix to be removed from file names",
            "Prefixes");

    public static final StringParameter removeSuffix = new StringParameter(
            "Remove suffix", "Suffix to be removed from file names",
            "Prefixes");

    public RawDataImportParameters() {

        super(fileNames, importMode, removePrefix, removeSuffix);

        removePrefix.setAutoSetAction(e -> {

            ParameterSheetView sheet = JavaFXUtil.getAncestorOfClass(
                    ParameterSheetView.class, (Node) e.getSource());
            PropertyEditor<List<File>> fileNamesEditor = sheet
                    .getEditorForParameter(fileNames);
            PropertyEditor<String> prefixEditor = sheet
                    .getEditorForParameter(removePrefix);

            List<File> fileNames = fileNamesEditor.getValue();
            if (fileNames == null) {
                prefixEditor.setValue("");
            } else {
                String commonPrefix = FileNameUtil.findCommonPrefix(fileNames);
                prefixEditor.setValue(commonPrefix);
            }

        });

        removeSuffix.setAutoSetAction(e -> {

            ParameterSheetView sheet = JavaFXUtil.getAncestorOfClass(
                    ParameterSheetView.class, (Node) e.getSource());
            PropertyEditor<List<File>> fileNamesEditor = sheet
                    .getEditorForParameter(fileNames);
            PropertyEditor<String> suffixEditor = sheet
                    .getEditorForParameter(removeSuffix);

            List<File> fileNames = fileNamesEditor.getValue();
            if (fileNames == null) {
                suffixEditor.setValue("");
            } else {
                String commonSuffix = FileNameUtil.findCommonSuffix(fileNames);
                // If no common suffix then remove file extension
                if (Strings.isNullOrEmpty(commonSuffix))
                    commonSuffix = ".*";
                suffixEditor.setValue(commonSuffix);
            }

        });
    }

}
