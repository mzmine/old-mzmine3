/*
 * Copyright 2006-2015 The MZmine 3 Development Team
 * 
 * This file is part of MZmine 3.
 * 
 * MZmine 3 is free software; you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * MZmine 3 is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with MZmine 3; if not,
 * write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301
 * USA
 */

package io.github.mzmine.parameters.parametertypes.selectors;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.controlsfx.control.PropertySheet;

import io.github.msdk.datamodel.featuretables.FeatureTable;
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

public class FeatureTablesEditor extends HBox implements ParameterEditor<FeatureTablesSelection> {

  private final ComboBox<FeatureTablesSelectionType> typeCombo;
  private final Button detailsButton;
  private final Text numFeatureTablesLabel;

  private List<FeatureTable> specificFeatureTables;
  private String namePattern;

  public FeatureTablesEditor(PropertySheet.Item parameter) {

    // HBox properties
    setSpacing(10);
    setAlignment(Pos.CENTER_LEFT);

    numFeatureTablesLabel = new Text();

    typeCombo = new ComboBox<>(
        FXCollections.observableList(Arrays.asList(FeatureTablesSelectionType.values())));

    detailsButton = new Button("...");
    detailsButton.setDisable(true);

    typeCombo.setOnAction(e -> {
      FeatureTablesSelectionType type = typeCombo.getSelectionModel().getSelectedItem();
      detailsButton.setDisable((type != FeatureTablesSelectionType.NAME_PATTERN)
          && (type != FeatureTablesSelectionType.SPECIFIC_FEATURE_TABLES));
      updateNumFeatureTables();
    });

    detailsButton.setOnAction(e -> {
      FeatureTablesSelectionType type = typeCombo.getSelectionModel().getSelectedItem();

      if (type == FeatureTablesSelectionType.SPECIFIC_FEATURE_TABLES) {

        final @Nonnull List<FeatureTable> allFeatureTables =
            MZmineCore.getCurrentProject().getFeatureTables();
        final MultiChoiceParameter<FeatureTable> filesParameter =
            new MultiChoiceParameter<FeatureTable>("Select files", "Select files", "FeatureTables",
                allFeatureTables, specificFeatureTables);
        final ParameterSet paramSet = new ParameterSet(filesParameter);
        final ButtonType exitCode = paramSet.showSetupDialog(null);
        if (exitCode == ButtonType.OK) {
          specificFeatureTables = paramSet.getParameter(filesParameter).getValue();
        }

      }

      if (type == FeatureTablesSelectionType.NAME_PATTERN) {
        final StringParameter nameParameter = new StringParameter("Name pattern",
            "Set name pattern that may include wildcards (*), e.g. *mouse* matches any name that contains mouse",
            "Name", namePattern);
        final ParameterSet paramSet = new ParameterSet(nameParameter);
        final ButtonType exitCode = paramSet.showSetupDialog(null);
        if (exitCode == ButtonType.OK) {
          namePattern = paramSet.getParameter(nameParameter).getValue();
        }

      }

      updateNumFeatureTables();

    });

    getChildren().addAll(numFeatureTablesLabel, typeCombo, detailsButton);
  }

  private void updateNumFeatureTables() {
    FeatureTablesSelection currentValue = getValue();
    List<FeatureTable> files = currentValue.getMatchingFeatureTables();
    if (files.size() == 1) {
      String fileName = files.get(0).getName();
      if (fileName.length() > 22)
        fileName = fileName.substring(0, 20) + "...";
      numFeatureTablesLabel.setText(fileName);
    } else {
      numFeatureTablesLabel.setText(files.size() + " selected");
    }
    // numFeatureTablesLabel.setToolTipText(currentValue.toString());
  }

  @Override
  public Node getEditor() {
    return this;
  }

  @Override
  public FeatureTablesSelection getValue() {
    FeatureTablesSelectionType selectionType = typeCombo.getSelectionModel().getSelectedItem();
    if (selectionType == null)
      selectionType = FeatureTablesSelectionType.ALL_FEATURE_TABLES;
    return new FeatureTablesSelection(selectionType, specificFeatureTables, namePattern);
  }

  @Override
  public void setValue(FeatureTablesSelection value) {
    if (value == null) {
      typeCombo.getSelectionModel().select(0);
      return;
    }
    typeCombo.getSelectionModel().select(value.getSelectionType());
    specificFeatureTables = value.getSpecificFeatureTables();
    namePattern = value.getNamePattern();
    updateNumFeatureTables();
  }

  @Override
  @Nullable
  public Control getMainControl() {
    return typeCombo;
  }
}
