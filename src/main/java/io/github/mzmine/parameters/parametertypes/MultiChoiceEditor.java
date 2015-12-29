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

import java.util.List;

import javax.annotation.Nullable;

import org.controlsfx.control.CheckListView;
import org.controlsfx.control.PropertySheet;

import io.github.mzmine.parameters.ParameterEditor;
import javafx.scene.Node;
import javafx.scene.control.Control;

public class MultiChoiceEditor<ValueType> extends CheckListView<ValueType>
        implements ParameterEditor<List<ValueType>> {

    private final MultiChoiceParameter<ValueType> multiChoiceParameter;

    @SuppressWarnings("unchecked")
    public MultiChoiceEditor(PropertySheet.Item parameter) {

        if (!(parameter instanceof MultiChoiceParameter))
            throw new IllegalArgumentException();

        this.multiChoiceParameter = (MultiChoiceParameter<ValueType>) parameter;

        List<ValueType> choices = this.multiChoiceParameter.getChoices();
        getItems().addAll(choices);
        setMaxHeight(120);

    }

    @Override
    public Node getEditor() {
        return this;
    }

    @Override
    public List<ValueType> getValue() {
        return getCheckModel().getCheckedItems();
    }

    @Override
    public void setValue(List<ValueType> value) {
        getCheckModel().clearChecks();
        if (value == null)
            return;
        for (ValueType item : value)
            getCheckModel().check(item);
    }

    @Override
    @Nullable
    public Control getMainControl() {
        return this;
    }
}
