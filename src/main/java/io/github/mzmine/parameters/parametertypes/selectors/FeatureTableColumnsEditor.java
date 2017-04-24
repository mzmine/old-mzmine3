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

import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import org.controlsfx.control.PropertySheet;

import io.github.mzmine.parameters.ParameterEditor;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class FeatureTableColumnsEditor extends HBox
    implements ParameterEditor<FeatureTableColumnsSelection> {

  private final ListView<String> namePatternList;
  private FeatureTableColumnsSelection value;

  public FeatureTableColumnsEditor(PropertySheet.Item parameter) {

    // HBox properties
    setSpacing(10);
    setAlignment(Pos.CENTER_LEFT);

    namePatternList = new ListView<>();
    namePatternList.setEditable(true);
    namePatternList.setPrefHeight(150);
    namePatternList.setCellFactory(TextFieldListCell.forListView());

    Button addButton = new Button("Add");
    addButton.setOnAction(e -> {
      TextInputDialog dialog = new TextInputDialog();
      dialog.setTitle("Add name pattern");
      dialog.setHeaderText("New name pattern");
      Optional<String> result = dialog.showAndWait();
      result.ifPresent(value -> namePatternList.getItems().add(value));
    });

    Button removeButton = new Button("Remove");
    removeButton.setOnAction(e -> {
      List<String> selectedItems = namePatternList.getSelectionModel().getSelectedItems();
      namePatternList.getItems().removeAll(selectedItems);
    });

    VBox buttons = new VBox(addButton, removeButton);
    buttons.setSpacing(10);

    getChildren().addAll(namePatternList, buttons);
  }

  @Override
  public Node getEditor() {
    return this;
  }

  @Override
  public FeatureTableColumnsSelection getValue() {
    return this.value;
  }

  @Override
  public void setValue(FeatureTableColumnsSelection value) {

    this.value = value;

    if (this.value == null) {
      this.value = new FeatureTableColumnsSelection();
    }

    namePatternList.setItems(this.value.getNamePatterns());

  }

  @Override
  @Nullable
  public Control getMainControl() {
    return namePatternList;
  }
}
