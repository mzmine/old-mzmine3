/*
 * Copyright 2006-2015 The MZmine 2 Development Team
 * 
 * This file is part of MZmine 2.
 * 
 * MZmine 2 is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * MZmine 2 is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * MZmine 2; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

package io.github.mzmine.gui.preferences;

import javax.annotation.Nullable;

import org.controlsfx.control.PropertySheet;

import io.github.mzmine.parameters.ParameterEditor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Spinner;
import javafx.scene.layout.HBox;

public class NumOfThreadsEditor extends HBox
        implements ParameterEditor<NumOfThreadsValue> {

    private static final ObservableList<String> options = FXCollections
            .observableArrayList(
                    "Set to the number of CPU cores ("
                            + Runtime.getRuntime().availableProcessors() + ")",
                    "Set manually");

    private final ComboBox<String> optionCombo;
    private final Spinner<Integer> numField;

    public NumOfThreadsEditor(PropertySheet.Item parameter) {

        // HBox properties
        setSpacing(10);
        setAlignment(Pos.CENTER_LEFT);

        numField = new Spinner<>(1, 50, 4);
        numField.setVisible(false);

        optionCombo = new ComboBox<>(options);
        optionCombo.setOnAction(e -> {
            numField.setVisible(
                    optionCombo.getSelectionModel().getSelectedIndex() == 1);
        });

        getChildren().addAll(optionCombo, numField);

    }

    @Override
    public Node getEditor() {
        return this;
    }

    @SuppressWarnings("null")
    @Override
    public NumOfThreadsValue getValue() {
        Boolean automatic = (optionCombo.getSelectionModel()
                .getSelectedIndex() == 0);
        Integer manualValue = numField.getValue();
        return new NumOfThreadsValue(automatic, manualValue);
    }

    @Override
    public void setValue(NumOfThreadsValue value) {
        if (value != null) {
            if (value.isAutomatic())
                optionCombo.getSelectionModel().select(0);
            else
                optionCombo.getSelectionModel().select(1);
            numField.getValueFactory().setValue(value.getManualValue());
        } else {
            optionCombo.getSelectionModel().select(0);
        }
    }

    @Override
    @Nullable
    public Control getMainControl() {
        return null;
    }

}
