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

import java.text.NumberFormat;

import javax.annotation.Nullable;

import org.controlsfx.control.PropertySheet;

import io.github.mzmine.parameters.ParameterEditor;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

/**
 * This parameter stores double values
 */
public class DoubleEditor extends HBox implements ParameterEditor<Double> {

    private final TextField doubleField;
    private @Nullable NumberFormat numberFormat;

    public DoubleEditor(PropertySheet.Item parameter) {
        if (!(parameter instanceof DoubleParameter))
            throw new IllegalArgumentException();

        DoubleParameter dp = (DoubleParameter) parameter;
        this.numberFormat = dp.getNumberFormat();
        this.doubleField = new TextField();
        getChildren().addAll(doubleField);
    }

    @Override
    public Node getEditor() {
        return this;
    }

    @Override
    public Double getValue() {
        String stringValue = doubleField.getText();
        try {
            double doubleValue = Double.parseDouble(stringValue);
            return doubleValue;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public void setValue(Double value) {
        String stringValue;
        if (numberFormat != null) {
            stringValue = numberFormat.format(value);
        } else {
            stringValue = String.valueOf(value);
        }
        doubleField.setText(stringValue);
    }

    @Override
    public Control getMainControl() {
        return doubleField;
    }

}
