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

import java.text.NumberFormat;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.w3c.dom.Element;

import io.github.mzmine.parameters.ParameterValidator;

public class DoubleParameter extends AbstractParameter<Double> {

    public DoubleParameter(@Nonnull String name, @Nonnull String description,
            @Nonnull String category) {
        this(name, description, category, null, null, null);
    }

    public DoubleParameter(@Nonnull String name, @Nonnull String description,
            @Nonnull String category, Double defaultValue) {
        this(name, description, category, null, null, defaultValue);
    }

    public DoubleParameter(@Nonnull String name, @Nonnull String description,
            @Nonnull String category,
            @Nullable ParameterValidator<Double> validator) {
        this(name, description, category, null, validator, null);
    }

    public DoubleParameter(@Nonnull String name, @Nonnull String description,
            @Nonnull String category, @Nullable NumberFormat intensityFormat,
            @Nullable ParameterValidator<Double> validator,
            @Nullable Double defaultValue) {
        super(name, description, category, DoubleEditor.class, validator,
                intensityFormat);
        setValue(defaultValue);
    }

    @Override
    public @Nonnull DoubleParameter clone() {
        DoubleParameter copy = new DoubleParameter(getName(), getDescription(),
                getCategory(), getNumberFormat(), getValidator(), getValue());
        return copy;
    }

    @Override
    public void loadValueFromXML(@Nonnull Element xmlElement) {
        Double content = Double.parseDouble(xmlElement.getTextContent());
        setValue(content);
    }

    @Override
    public void saveValueToXML(@Nonnull Element xmlElement) {
        if (getValue() == null)
            return;
        xmlElement.setTextContent(getValue().toString());
    }

}
