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

import javax.annotation.Nullable;

import org.controlsfx.control.PropertySheet;

import io.github.mzmine.parameters.ParameterEditor;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Control;

/**
 * Combo parameter editor
 */
public class ComboEditor<ValueType> extends ChoiceBox<ValueType>
        implements ParameterEditor<ValueType> {

    private final ComboParameter<ValueType> comboParameter;

    @SuppressWarnings("unchecked")
    public ComboEditor(PropertySheet.Item parameter) {

        if (!(parameter instanceof ComboParameter))
            throw new IllegalArgumentException();

        this.comboParameter = (ComboParameter<ValueType>) parameter;

        ObservableList<ValueType> optionsList = (ObservableList<ValueType>) comboParameter
                .getOptions();

        if (optionsList != null) {
            setItems(optionsList);
        }

        getSelectionModel().selectFirst();
        
    }

    @Override
    public Node getEditor() {
        return this;
    }

    @Override
    @Nullable
    public Control getMainControl() {
        return this;
    }

}
