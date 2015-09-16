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

import org.controlsfx.control.PropertySheet;
import org.controlsfx.property.editor.PropertyEditor;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.layout.BorderPane;

public class NumOfThreadsEditor extends BorderPane
        implements PropertyEditor<NumOfThreadsValue> {

    private static final ObservableList<String> options = FXCollections
            .observableArrayList(
                    "Set to the number of CPU cores ("
                            + Runtime.getRuntime().availableProcessors() + ")",
                    "Set manually");

    private final ComboBox<String> optionCombo;
    private final Spinner<Integer> numField;

    public NumOfThreadsEditor(PropertySheet.Item parameter) {

        numField = new Spinner<>(1, 50, 4);
        numField.setDisable(true);
        setCenter(numField);

        optionCombo = new ComboBox<>(options);
        optionCombo.setOnAction(e -> {
            numField.setDisable(
                    optionCombo.getSelectionModel().getSelectedIndex() == 0);
        });
        setLeft(optionCombo);

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

}
