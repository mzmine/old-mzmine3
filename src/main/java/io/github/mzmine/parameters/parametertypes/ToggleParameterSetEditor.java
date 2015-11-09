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

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.controlsfx.control.PropertySheet;
import org.controlsfx.control.SegmentedButton;

import io.github.mzmine.parameters.ParameterEditor;
import io.github.mzmine.parameters.ParameterSet;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;

/**
 * This parameter stores double values
 */
public class ToggleParameterSetEditor<ValueType> extends BorderPane
        implements ParameterEditor<ValueType> {

    private final SegmentedButton segmentedButton;
    private final ToggleParameterSetParameter<ValueType> toggleParameterSetParameter;
    private final LinkedHashMap<String, ParameterSet> toggleValues;

    @SuppressWarnings("unchecked")
    public ToggleParameterSetEditor(PropertySheet.Item parameter) {
        if (!(parameter instanceof ToggleParameterSetParameter))
            throw new IllegalArgumentException();

        this.toggleParameterSetParameter = (ToggleParameterSetParameter<ValueType>) parameter;

        // The segmented button
        this.segmentedButton = new SegmentedButton();
        segmentedButton.getStyleClass().add(SegmentedButton.STYLE_CLASS_DARK);
        /*
         * TODO: Add event handler to handle showing different parameterSets
         */

        // The toggle buttons
        toggleValues = this.toggleParameterSetParameter.getToggleValues();
        for (HashMap.Entry<String, ParameterSet> entry : toggleValues
                .entrySet()) {
            segmentedButton.getButtons().add(new ToggleButton(entry.getKey()));
        }

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

                for (HashMap.Entry<String, ParameterSet> entry : toggleValues
                        .entrySet()) {
                    segmentedButton.getButtons()
                            .add(new ToggleButton(entry.getKey()));
                    if (entry.getKey().equals(buttonText)) {
                        return (ValueType) entry.getKey();
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
