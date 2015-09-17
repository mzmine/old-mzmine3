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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.w3c.dom.Element;

import com.google.common.base.Strings;

import io.github.mzmine.parameters.ParameterValidator;

public class BooleanParameter extends AbstractParameter<Boolean> {

    public BooleanParameter(@Nonnull String name, @Nonnull String description,
            @Nonnull String category) {
        this(name, description, category, null, null);
    }

    public BooleanParameter(@Nonnull String name, @Nonnull String description,
            @Nonnull String category, @Nullable Boolean defaultValue) {
        this(name, description, category, null, defaultValue);
    }

    public BooleanParameter(@Nonnull String name, @Nonnull String description,
            @Nonnull String category,
            @Nullable ParameterValidator<Boolean> validator,
            @Nullable Boolean defaultValue) {
        super(name, description, category, BooleanEditor.class, validator);
        setValue(defaultValue);
    }

    @Override
    public @Nonnull BooleanParameter clone() {
        BooleanParameter copy = new BooleanParameter(getName(),
                getDescription(), getCategory(), getValidator(), getValue());
        return copy;
    }

    @Override
    public void loadValueFromXML(@Nonnull Element xmlElement) {
        String content = xmlElement.getTextContent();
        if (Strings.isNullOrEmpty(content))
            return;
        setValue(Boolean.valueOf(content));
    }

    @Override
    public void saveValueToXML(@Nonnull Element xmlElement) {
        if (getValue() == null)
            return;
        xmlElement.setTextContent(String.valueOf(getValue()));
    }

}
