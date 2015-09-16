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
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

/**
 * This parameter stores filenames
 */
public class StringEditor extends BorderPane
        implements ParameterEditor<String> {

    private final StringParameter stringParameter;
    private final TextField textField;
    private final Button autoButton;

    public StringEditor(PropertySheet.Item parameter) {
        if (!(parameter instanceof StringParameter))
            throw new IllegalArgumentException();

        this.stringParameter = (StringParameter) parameter;

        // The text field
        this.textField = new TextField();
        setCenter(textField);

        // The Add button
        EventHandler<ActionEvent> autoSetAction = stringParameter
                .getAutoSetAction();
        if (autoSetAction != null) {
            autoButton = new Button("Auto");
            setRight(autoButton);
            setMargin(autoButton, new Insets(0, 0, 0, 10.0));
            autoButton.setOnAction(autoSetAction);
        } else {
            autoButton = null;
        }

    }

    @Override
    public Node getEditor() {
        return this;
    }

    @Override
    public String getValue() {
        return textField.getText();
    }

    @Override
    public void setValue(String value) {
        textField.setText(value);
    }

   
    @Override
    public Control getMainControl() {
        // TODO Auto-generated method stub
        return textField;
    }

}
