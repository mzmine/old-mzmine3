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

package io.github.mzmine.parameters.parametertypes;

import java.util.List;

import org.controlsfx.control.PropertySheet;
import org.controlsfx.control.SegmentedButton;

import io.github.mzmine.parameters.ParameterEditor;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;

/**
 * This parameter stores double values
 */
public class ToggleEditor<ValueType> extends BorderPane
        implements ParameterEditor<ValueType> {

    private final SegmentedButton segmentedButton;
    private final ToggleParameter<ValueType> toggleParameter;
    private final List<ValueType> toggleValues;

    public ToggleEditor(PropertySheet.Item parameter) {
        if (!(parameter instanceof ToggleParameter))
            throw new IllegalArgumentException();

        this.toggleParameter = (ToggleParameter<ValueType>) parameter;

        // The segmented button
        this.segmentedButton = new SegmentedButton();
        segmentedButton.getStyleClass().add(SegmentedButton.STYLE_CLASS_DARK);

        // The toggle buttons
        toggleValues = this.toggleParameter.getToggleValues();
        for (ValueType toggleValue : toggleValues) {
            segmentedButton.getButtons()
                    .add(new ToggleButton(toggleValue.toString()));
        }

        // Default set to first choice
        setValue(toggleValues.get(0));

        setLeft(segmentedButton);
    }

    @Override
    public Node getEditor() {
        return this;
    }

    @Override
    public ValueType getValue() {
        ObservableList<ToggleButton> buttons = segmentedButton.getButtons();
        for (ToggleButton button : buttons) {
            if (button.isSelected()) {
                String buttonText = button.getText();

                for (ValueType toggleValue : toggleValues) {
                    if (toggleValue.toString().equals(buttonText)) {
                        return toggleValue;
                    }
                }

            }
        }
        return null;
    }

    @Override
    public void setValue(ValueType value) {
        if (value != null) {
            String stringValue = value.toString();
            ObservableList<ToggleButton> buttons = segmentedButton.getButtons();
            for (ToggleButton button : buttons) {
                if (button.getText().equals(stringValue)) {
                    button.setSelected(true);
                    break;
                }
            }
        }
    }

    @Override
    public Control getMainControl() {
        return segmentedButton;
    }

}
