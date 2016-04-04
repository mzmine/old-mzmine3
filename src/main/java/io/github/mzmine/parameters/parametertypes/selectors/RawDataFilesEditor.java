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

package io.github.mzmine.parameters.parametertypes.selectors;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.controlsfx.control.PropertySheet;

import io.github.msdk.datamodel.rawdata.RawDataFile;
import io.github.mzmine.main.MZmineCore;
import io.github.mzmine.parameters.ParameterEditor;
import io.github.mzmine.parameters.ParameterSet;
import io.github.mzmine.parameters.parametertypes.MultiChoiceParameter;
import io.github.mzmine.parameters.parametertypes.StringParameter;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class RawDataFilesEditor extends HBox
        implements ParameterEditor<RawDataFilesSelection> {

    private final ComboBox<RawDataFilesSelectionType> typeCombo;
    private final Button detailsButton;
    private final Text numFilesLabel;

    private List<RawDataFile> specificFiles;
    private String namePattern;

    public RawDataFilesEditor(PropertySheet.Item parameter) {

        // HBox properties
        setSpacing(10);
        setAlignment(Pos.CENTER_LEFT);

        numFilesLabel = new Text();

        typeCombo = new ComboBox<>(FXCollections.observableList(
                Arrays.asList(RawDataFilesSelectionType.values())));

        detailsButton = new Button("...");
        detailsButton.setDisable(true);

        typeCombo.setOnAction(e -> {
            RawDataFilesSelectionType type = typeCombo.getSelectionModel()
                    .getSelectedItem();
            detailsButton
                    .setDisable((type != RawDataFilesSelectionType.NAME_PATTERN)
                            && (type != RawDataFilesSelectionType.SPECIFIC_FILES));
            updateNumFiles();
        });

        detailsButton.setOnAction(e -> {
            RawDataFilesSelectionType type = typeCombo.getSelectionModel()
                    .getSelectedItem();

            if (type == RawDataFilesSelectionType.SPECIFIC_FILES) {

                final @Nonnull List<RawDataFile> allFiles = MZmineCore
                        .getCurrentProject().getRawDataFiles();
                final MultiChoiceParameter<RawDataFile> filesParameter = new MultiChoiceParameter<RawDataFile>(
                        "Select files", "Select files", "Files", allFiles,
                        specificFiles);
                final ParameterSet paramSet = new ParameterSet(filesParameter);
                final ButtonType exitCode = paramSet.showSetupDialog(null);
                if (exitCode == ButtonType.OK) {
                    specificFiles = paramSet.getParameter(filesParameter)
                            .getValue();
                }

            }

            if (type == RawDataFilesSelectionType.NAME_PATTERN) {
                final StringParameter nameParameter = new StringParameter(
                        "Name pattern",
                        "Set name pattern that may include wildcards (*), e.g. *mouse* matches any name that contains mouse",
                        "Name", namePattern);
                final ParameterSet paramSet = new ParameterSet(nameParameter);
                final ButtonType exitCode = paramSet.showSetupDialog(null);
                if (exitCode == ButtonType.OK) {
                    namePattern = paramSet.getParameter(nameParameter)
                            .getValue();
                }

            }

            updateNumFiles();

        });

        getChildren().addAll(numFilesLabel, typeCombo, detailsButton);
    }

    private void updateNumFiles() {
        RawDataFilesSelection currentValue = getValue();
        List<RawDataFile> files = currentValue.getMatchingRawDataFiles();
        if (files.size() == 1) {
            if (files.get(0) == null) return;
            String fileName = files.get(0).getName();
            if (fileName.length() > 22)
                fileName = fileName.substring(0, 20) + "...";
            numFilesLabel.setText(fileName);
        } else {
            numFilesLabel.setText(files.size() + " selected");
        }
        // numFilesLabel.setToolTipText(currentValue.toString());
    }

    @Override
    public Node getEditor() {
        return this;
    }

    @Override
    public RawDataFilesSelection getValue() {
        RawDataFilesSelectionType selectionType = typeCombo.getSelectionModel()
                .getSelectedItem();
        if (selectionType == null)
            selectionType = RawDataFilesSelectionType.ALL_FILES;
        return new RawDataFilesSelection(selectionType, specificFiles,
                namePattern);
    }

    @Override
    public void setValue(RawDataFilesSelection value) {
        if (value == null)
            return;

        typeCombo.getSelectionModel().select(value.getSelectionType());
        specificFiles = value.getSpecificFiles();
        namePattern = value.getNamePattern();
        updateNumFiles();
    }

    @Override
    @Nullable
    public Control getMainControl() {
        return typeCombo;
    }
}
