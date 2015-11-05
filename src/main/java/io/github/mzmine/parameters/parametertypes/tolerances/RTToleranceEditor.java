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

package io.github.mzmine.parameters.parametertypes.tolerances;

import org.controlsfx.control.PropertySheet;

import io.github.msdk.util.RTTolerance;
import io.github.mzmine.parameters.ParameterEditor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

/**
 * Parameter editor for RT tolerances
 */
public class RTToleranceEditor extends BorderPane
        implements ParameterEditor<RTTolerance> {

    private final TextField valueField;
    private final ComboBox<String> comboBox;

    public RTToleranceEditor(PropertySheet.Item parameter) {
        if (!(parameter instanceof RTToleranceParameter))
            throw new IllegalArgumentException();

        // Make a box for the fields and labels
        HBox hBox = new HBox();
        hBox.setSpacing(5);

        // The value field
        valueField = new TextField();
        hBox.getChildren().add(valueField);

        // The combo box
        ObservableList<String> options = FXCollections
                .observableArrayList("Absolute (sec)", "Relative (%)");
        comboBox = new ComboBox<String>(options);
        hBox.getChildren().add(comboBox);

        setLeft(hBox);
    }

    @Override
    public Node getEditor() {
        return this;
    }

    @Override
    public RTTolerance getValue() {
        String comboValue = comboBox.getSelectionModel().getSelectedItem();
        Boolean isAbsolute = true;
        if (comboValue.equals("Relative (%)"))
            isAbsolute = false;

        String stringValue = valueField.getText();
        try {
            double doubleValue = Double.parseDouble(stringValue);
            return new RTTolerance(doubleValue, isAbsolute);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public void setValue(RTTolerance value) {
        String stringValue = String.valueOf(value.getTolerance());
        valueField.setText(stringValue);
        if (value.isAbsolute()) {
            comboBox.getSelectionModel().select(0);
        } else {
            comboBox.getSelectionModel().select(1);
        }
    }

    @Override
    public Control getMainControl() {
        // TODO Auto-generated method stub
        return valueField;
    }

}
