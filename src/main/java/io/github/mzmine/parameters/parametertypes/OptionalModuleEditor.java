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

package io.github.mzmine.parameters.parametertypes;

import org.controlsfx.control.PropertySheet;
import org.controlsfx.property.editor.PropertyEditor;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.FlowPane;

public class OptionalModuleEditor extends FlowPane
        implements PropertyEditor<Boolean> {

    private final CheckBox checkBox;
    private final Button setButton;

    private final OptionalModuleParameter optionalModuleParameter;

    public OptionalModuleEditor(PropertySheet.Item parameter) {

        if (!(parameter instanceof OptionalModuleParameter))
            throw new IllegalArgumentException();

        this.optionalModuleParameter = (OptionalModuleParameter) parameter;

        setButton = new Button("Setup..");
        setButton.setDisable(true);
        setButton.setOnAction(e -> {
            optionalModuleParameter.getEmbeddedParameters().showSetupDialog();
        });

        checkBox = new CheckBox();
        checkBox.setOnAction(e -> {
            setButton.setDisable(!checkBox.isSelected());
        });

        // FLowPane setting
        setHgap(10);

        getChildren().addAll(checkBox, setButton);

    }

    @Override
    public Node getEditor() {
        return this;
    }

    @Override
    public Boolean getValue() {
        return checkBox.isSelected();
    }

    @Override
    public void setValue(Boolean value) {
        if (value != null)
            checkBox.setSelected(value);
    }

}
