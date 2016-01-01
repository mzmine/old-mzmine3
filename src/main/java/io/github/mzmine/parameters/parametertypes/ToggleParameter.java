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
 * MZmine 3; if not, write to the Free Software Foundation, Inc., 51 Franklin
 * St, Fifth Floor, Boston, MA 02110-1301 USA
 */

package io.github.mzmine.parameters.parametertypes;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.w3c.dom.Element;

import io.github.mzmine.parameters.ParameterValidator;

public class ToggleParameter<ValueType> extends AbstractParameter<ValueType> {

    private final @Nonnull List<ValueType> toggleValues;

    public ToggleParameter(@Nonnull String name, @Nonnull String description,
            @Nonnull String category, List<ValueType> toggleValues) {
        this(name, description, category, null, toggleValues, null);
    }

    public ToggleParameter(@Nonnull String name, @Nonnull String description,
            @Nonnull String category, List<ValueType> toggleValues,
            String defaultValue) {
        this(name, description, category, null, toggleValues, defaultValue);
    }

    public ToggleParameter(@Nonnull String name, @Nonnull String description,
            @Nonnull String category,
            @Nullable ParameterValidator<ValueType> validator,
            List<ValueType> toggleValues) {
        this(name, description, category, validator, toggleValues, null);
    }

    @SuppressWarnings("unchecked")
    public ToggleParameter(@Nonnull String name, @Nonnull String description,
            @Nonnull String category,
            @Nullable ParameterValidator<ValueType> validator,
            List<ValueType> toggleValues, @Nullable String defaultValue) {
        super(name, description, category, (Class) ToggleEditor.class,
                validator);
        this.toggleValues = toggleValues;
        setValue(defaultValue);
    }

    @Override
    public @Nonnull ToggleParameter<ValueType> clone() {
        ToggleParameter<ValueType> copy = new ToggleParameter<ValueType>(
                getName(), getDescription(), getCategory(), getValidator(),
                toggleValues);
        copy.setValue(this.getValue());
        return copy;
    }

    public List<ValueType> getToggleValues() {
        return toggleValues;
    }

    @Override
    public void loadValueFromXML(@Nonnull Element xmlElement) {
        String stringValue = xmlElement.getTextContent();
        for (ValueType value : toggleValues) {
            if (value.toString().equals(stringValue)) {
                setValue(value);
                break;
            }
        }
    }

    @Override
    public void saveValueToXML(@Nonnull Element xmlElement) {
        Object value = getValue();
        if (value == null)
            return;
        xmlElement.setTextContent(value.toString());
    }

}
