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

import java.text.DecimalFormat;

import javax.annotation.Nullable;

import org.controlsfx.control.PropertySheet;

import io.github.mzmine.parameters.ParameterEditor;
import io.github.mzmine.parameters.parametertypes.SpinnerAutoCommit;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.control.Spinner;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class NumberFormatEditor extends HBox
        implements ParameterEditor<DecimalFormat> {

    private final NumberFormatParameter numFormatParameter;
    private final Spinner<Integer> decimalsSpinner;
    private final CheckBox exponentCheckbox;

    public NumberFormatEditor(PropertySheet.Item parameter) {

        if (!(parameter instanceof NumberFormatParameter))
            throw new IllegalArgumentException();

        // HBox properties
        setSpacing(10);
        setAlignment(Pos.CENTER_LEFT);

        this.numFormatParameter = (NumberFormatParameter) parameter;

        getChildren().add(new Text("Decimals"));

        decimalsSpinner = new SpinnerAutoCommit<>(1, 20, 3);
        decimalsSpinner.setPrefWidth(80.0);
        decimalsSpinner.setEditable(true);

        getChildren().add(decimalsSpinner);

        if (numFormatParameter.isShowExponentEnabled()) {
            exponentCheckbox = new CheckBox("Show exponent");
            getChildren().add(exponentCheckbox);
        } else {
            exponentCheckbox = null;
        }

    }

    public boolean getShowExponent() {
        if (exponentCheckbox == null)
            return false;
        else
            return exponentCheckbox.isSelected();
    }

    @Override
    public void setValue(DecimalFormat newValue) {
        if (newValue == null) return;
        final int decimals = newValue.getMinimumFractionDigits();
        boolean showExponent = newValue.toPattern().contains("E");
        decimalsSpinner.getValueFactory().setValue(decimals);
        if (exponentCheckbox != null)
            exponentCheckbox.setSelected(showExponent);
    }

    @Override
    public Node getEditor() {
        return this;
    }

    @Override
    public DecimalFormat getValue() {
        final int decimals = decimalsSpinner.getValue();
        boolean showExponent = false;
        if (exponentCheckbox != null)
            showExponent = exponentCheckbox.isSelected();
        String pattern = "0";

        if (decimals > 0) {
            pattern += ".";
            for (int i = 0; i < decimals; i++)
                pattern += "0";
        }
        if (showExponent) {
            pattern += "E0";
        }
        return new DecimalFormat(pattern);
    }

    @Override
    @Nullable
    public Control getMainControl() {
        return decimalsSpinner;
    }

}
