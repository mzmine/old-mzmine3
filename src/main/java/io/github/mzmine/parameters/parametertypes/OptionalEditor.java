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

import javax.annotation.Nullable;

import org.controlsfx.control.PropertySheet;
import org.controlsfx.property.editor.PropertyEditor;

import io.github.mzmine.parameters.ParameterEditor;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.layout.FlowPane;

public class OptionalEditor extends FlowPane
        implements ParameterEditor<Boolean> {

    private final CheckBox checkBox;
    private AbstractParameter<?> embeddedParameter;

    public OptionalEditor(PropertySheet.Item parameter) {

        if (!(parameter instanceof OptionalParameter))
            throw new IllegalArgumentException();

        // The checkbox
        checkBox = new CheckBox();
        getChildren().add(checkBox);

        // FlowPane setting
        setHgap(10);

        // Add embedded editor
        try {
            OptionalParameter<?> optionalParameter = (OptionalParameter<?>) parameter;
            embeddedParameter = optionalParameter.getEmbeddedParameter();
            Class<? extends PropertyEditor<?>> embeddedEditorClass = embeddedParameter
                    .getPropertyEditorClass().get();
            PropertyEditor<?> editor;
            editor = embeddedEditorClass
                    .getDeclaredConstructor(PropertySheet.Item.class)
                    .newInstance(embeddedParameter);
            Node embeddedNode = editor.getEditor();
            getChildren().add(embeddedNode);
        } catch (Exception e) {
            e.printStackTrace();
        }

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
        if (value != null) {
            checkBox.setSelected(value);
        }
    }

    @Override
    @Nullable
    public Control getMainControl() {
        return checkBox;
    }

}
