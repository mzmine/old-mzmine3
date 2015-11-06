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
 * MZmine 3; if not, write to the Free Software Foundation, Inc., 51 Franklin
 * St, Fifth Floor, Boston, MA 02110-1301 USA
 */

package io.github.mzmine.parameters.parametertypes;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.w3c.dom.Element;

import io.github.mzmine.parameters.ParameterValidator;

public class IntegerParameter extends AbstractParameter<Integer> {

    public IntegerParameter(@Nonnull String name, @Nonnull String description,
            @Nonnull String category) {
        this(name, description, category, null, null);
    }

    public IntegerParameter(@Nonnull String name, @Nonnull String description,
            @Nonnull String category, Integer defaultValue) {
        this(name, description, category, null, defaultValue);
    }

    public IntegerParameter(@Nonnull String name, @Nonnull String description,
            @Nonnull String category, @Nullable ParameterValidator<Integer> validator) {
        this(name, description, category, validator, null);
    }

    public IntegerParameter(@Nonnull String name, @Nonnull String description,
            @Nonnull String category,
            @Nullable ParameterValidator<Integer> validator,
            @Nullable Integer defaultValue) {
        super(name, description, category, IntegerEditor.class, validator);
        setValue(defaultValue);
    }

    @Override
    public @Nonnull IntegerParameter clone() {
        IntegerParameter copy = new IntegerParameter(getName(),
                getDescription(), getCategory(), getValidator(), getValue());
        return copy;
    }

    @Override
    public void loadValueFromXML(@Nonnull Element xmlElement) {
        Integer content = Integer.parseInt(xmlElement.getTextContent());
        setValue(content);
    }

    @Override
    public void saveValueToXML(@Nonnull Element xmlElement) {
        if (getValue() == null)
            return;
        xmlElement.setTextContent(getValue().toString());
    }

}
