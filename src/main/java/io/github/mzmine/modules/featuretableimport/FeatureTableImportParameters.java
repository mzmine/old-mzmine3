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

package io.github.mzmine.modules.featuretableimport;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.controlsfx.control.PropertySheet;

import com.google.common.base.Strings;

import io.github.mzmine.parameters.ParameterSet;
import io.github.mzmine.parameters.ParameterValidator;
import io.github.mzmine.parameters.parametertypes.StringEditor;
import io.github.mzmine.parameters.parametertypes.StringParameter;
import io.github.mzmine.parameters.parametertypes.filenames.FileNamesParameter;
import io.github.mzmine.util.FileNameUtil;
import javafx.scene.Node;
import javafx.stage.FileChooser.ExtensionFilter;

public class FeatureTableImportParameters extends ParameterSet {

    private static final ExtensionFilter filters[] = new ExtensionFilter[] {
            new ExtensionFilter("All feature table files", "*.csv", "*.mzTab"),
            new ExtensionFilter("CSV files", "*.csv"),
            new ExtensionFilter("mzTab files", "*.mzTab"),
            new ExtensionFilter("All files", "*.*") };

    public static final FileNamesParameter fileNames = new FileNamesParameter(
            "File names", "Add feature table files", "Input files",
            ParameterValidator.createNonEmptyValidator(),
            Arrays.asList(filters));

    @SuppressWarnings({ "unchecked" })
    public static final StringParameter removePrefix = new StringParameter(
            "Remove prefix", "Prefix to be removed from file names", "Prefixes",
            null, null, e -> {
                StringEditor editor = null;
                PropertySheet sheet = null;
                Node src = (Node) e.getSource();
                while (src != null) {
                    if (src instanceof StringEditor)
                        editor = (StringEditor) src;
                    if (src instanceof PropertySheet) {
                        sheet = (PropertySheet) src;
                        break;
                    }
                    src = src.getParent();

                }
                if (sheet != null && editor != null) {
                    for (PropertySheet.Item item : sheet.getItems()) {
                        if (item instanceof FileNamesParameter) {
                            List<File> fileNames = (List<File>) item.getValue();
                            if (fileNames == null)
                                return;
                            String commonPrefix = FileNameUtil
                                    .findCommonPrefix(fileNames);
                            editor.setValue(commonPrefix);
                        }
                    }
                }
            });

    @SuppressWarnings({ "unchecked" })
    public static final StringParameter removeSuffix = new StringParameter(
            "Remove suffix", "Suffix to be removed from file names", "Prefixes",
            null, null, e -> {
                StringEditor editor = null;
                PropertySheet sheet = null;
                Node src = (Node) e.getSource();
                while (src != null) {
                    if (src instanceof StringEditor)
                        editor = (StringEditor) src;
                    if (src instanceof PropertySheet) {
                        sheet = (PropertySheet) src;
                        break;
                    }
                    src = src.getParent();

                }
                if (sheet != null && editor != null) {
                    for (PropertySheet.Item item : sheet.getItems()) {
                        if (item instanceof FileNamesParameter) {
                            List<File> fileNames = (List<File>) item.getValue();
                            if (fileNames == null)
                                continue;
                            String commonSuffix = FileNameUtil
                                    .findCommonSuffix(fileNames);
                            // If no common suffix then remove file extension
                            if (Strings.isNullOrEmpty(commonSuffix))
                                commonSuffix = ".*";
                            editor.setValue(commonSuffix);
                        }
                    }
                }
            });

    public FeatureTableImportParameters() {
        super(fileNames, removePrefix, removeSuffix);
    }

}
