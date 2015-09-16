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
import org.controlsfx.property.editor.PropertyEditor;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;

/**
 * This parameter stores filenames
 */
public class ComboEditor extends ChoiceBox<Object>
        implements PropertyEditor<Object> {

    private final ComboParameter<?> comboParameter;

    public ComboEditor(PropertySheet.Item parameter) {
        
        if (!(parameter instanceof ComboParameter))
            throw new IllegalArgumentException();

        this.comboParameter = (ComboParameter<?>) parameter;

        @SuppressWarnings("unchecked")
        ObservableList<Object> optionsList = (ObservableList<Object>) comboParameter
                .getOptions();
        
        if (optionsList != null)
            setItems(optionsList);

    }

    @Override
    public Node getEditor() {
        return this;
    }

}
