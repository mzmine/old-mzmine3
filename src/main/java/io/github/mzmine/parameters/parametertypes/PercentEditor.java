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

package io.github.mzmine.parameters.parametertypes;

import org.controlsfx.control.PropertySheet;

import io.github.mzmine.parameters.ParameterEditor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

/**
 * This parameter stores filenames
 */
public class PercentEditor extends BorderPane
        implements ParameterEditor<Double> {

    private final TextField percentField;

    public PercentEditor(PropertySheet.Item parameter) {
        if (!(parameter instanceof PercentParameter))
            throw new IllegalArgumentException();

        // Make a box for the fields and labels
        HBox hBox = new HBox();
        hBox.setSpacing(5);

        // The percent field
        this.percentField = new TextField();
        hBox.getChildren().add(percentField);

        // The percent sign
        Label signLabel = new Label("%");
        signLabel.setPrefHeight(24);
        hBox.getChildren().add(signLabel);

        setLeft(hBox);
    }

    @Override
    public Node getEditor() {
        return this;
    }

    @Override
    public Double getValue() {

        String stringValue = percentField.getText();
        try {
            double doubleValue = Double.parseDouble(stringValue) / 100;
            return doubleValue;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public void setValue(Double value) {
        String stringValue = String.valueOf(value * 100);
        percentField.setText(stringValue);
    }

   
    @Override
    public Control getMainControl() {
        // TODO Auto-generated method stub
        return percentField;
    }

}
