/*
 * Copyright 2006-2015 The MZmine 3 Development Team
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
 * MZmine 2; if not, write to the Free Software Foundation, Inc., 51 Franklin
 * St, Fifth Floor, Boston, MA 02110-1301 USA
 */

package io.github.mzmine.parameters.parametertypes;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.w3c.dom.Element;

import io.github.mzmine.parameters.ParameterValidator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ComboParameter<ValueType> extends AbstractParameter<ValueType> {

    private @Nonnull ObservableList<ValueType> options;

    public ComboParameter(@Nonnull String name, @Nonnull String description,
            @Nonnull String category, @Nonnull List<ValueType> options) {
        this(name, description, category, null, options, null);
    }

    public ComboParameter(@Nonnull String name, @Nonnull String description,
            @Nonnull String category, @Nonnull List<ValueType> options,
            @Nullable ValueType defaultValue) {
        this(name, description, category, null, options, defaultValue);
    }

    @SuppressWarnings({ "unchecked", "rawtypes", "null" })
    public ComboParameter(@Nonnull String name, @Nonnull String description,
            @Nonnull String category,
            @Nullable ParameterValidator<ValueType> validator,
            @Nonnull List<ValueType> options,
            @Nullable ValueType defaultValue) {
        super(name, description, category, (Class) ComboEditor.class,
                validator, null);
        this.options = FXCollections.observableList(options);
        setValue(defaultValue);
    }

    @Override
    public @Nonnull ComboParameter<ValueType> clone() {
        ComboParameter<ValueType> copy = new ComboParameter<>(getName(),
                getDescription(), getCategory(), getValidator(), options,
                getValue());
        return copy;
    }

    @Override
    public void loadValueFromXML(@Nonnull Element xmlElement) {
        // value = xmlElement.getTextContent();
    }

    @Override
    public void saveValueToXML(@Nonnull Element xmlElement) {
        Object value = getValue();
        if (value == null)
            return;
        xmlElement.setTextContent(value.toString());
    }

    ObservableList<ValueType> getOptions() {
        return options;
    }

}
