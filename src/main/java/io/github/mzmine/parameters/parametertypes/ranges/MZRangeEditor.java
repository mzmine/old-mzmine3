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

package io.github.mzmine.parameters.parametertypes.ranges;

import java.text.NumberFormat;

import org.controlsfx.control.PropertySheet;

import com.google.common.collect.Range;

import io.github.mzmine.main.MZmineCore;
import io.github.mzmine.parameters.ParameterEditor;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

/**
 * Parameter editor for double ranges
 */
public class MZRangeEditor extends HBox
        implements ParameterEditor<Range<Double>> {

    private final TextField minTxtField;
    private final TextField maxTxtField;

    public MZRangeEditor(PropertySheet.Item parameter) {
        if (!(parameter instanceof MZRangeParameter))
            throw new IllegalArgumentException();

        // The minimum value field
        minTxtField = new TextField();

        // The separator label
        Label separatorLabel = new Label("-");

        // The maximum value field
        maxTxtField = new TextField();

        // Spacing setting
        setSpacing(10);

        // Add the elements
        getChildren().addAll(minTxtField, separatorLabel, maxTxtField);
    }

    @Override
    public Node getEditor() {
        return this;
    }

    @Override
    public Range<Double> getValue() {
        String minValueS = minTxtField.getText();
        String maxValueS = maxTxtField.getText();
        try {
            double minValue = Double.parseDouble(minValueS);
            double maxValue = Double.parseDouble(maxValueS);
            return Range.closed(minValue, maxValue);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public void setValue(Range<Double> value) {
        if (value == null)
            return;
        String minValue;
        String maxValue;

        NumberFormat numberFormat = MZmineCore.getConfiguration().getMZFormat();
        minValue = numberFormat.format(value.lowerEndpoint());
        maxValue = numberFormat.format(value.upperEndpoint());

        minTxtField.setText(minValue);
        maxTxtField.setText(maxValue);
    }

    @Override
    public Control getMainControl() {
        return minTxtField;
    }

}
