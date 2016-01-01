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

package io.github.mzmine.parameters.parametertypes.tolerances;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.w3c.dom.Element;

import io.github.msdk.util.RTTolerance;
import io.github.mzmine.parameters.ParameterValidator;
import io.github.mzmine.parameters.parametertypes.AbstractParameter;

public class RTToleranceParameter extends AbstractParameter<RTTolerance> {

    public RTToleranceParameter(@Nonnull String name,
            @Nonnull String description, @Nonnull String category) {
        this(name, description, category, null, null);
    }

    public RTToleranceParameter(@Nonnull String name,
            @Nonnull String description, @Nonnull String category,
            RTTolerance defaultValue) {
        this(name, description, category, null, defaultValue);
    }

    public RTToleranceParameter(@Nonnull String name,
            @Nonnull String description, @Nonnull String category,
            @Nullable ParameterValidator<RTTolerance> validator) {
        this(name, description, category, validator, null);
    }

    public RTToleranceParameter(@Nonnull String name,
            @Nonnull String description, @Nonnull String category,
            @Nullable ParameterValidator<RTTolerance> validator,
            @Nullable RTTolerance defaultValue) {
        super(name, description, category, RTToleranceEditor.class, validator);
        setValue(defaultValue);
    }

    @Override
    public @Nonnull RTToleranceParameter clone() {
        RTToleranceParameter copy = new RTToleranceParameter(getName(),
                getDescription(), getCategory(), getValidator(), getValue());
        return copy;
    }

    @Override
    public void loadValueFromXML(@Nonnull Element xmlElement) {
        String typeAttr = xmlElement.getAttribute("type");
        Boolean isAbsolute = !typeAttr.equals("percent");
        Double content = Double.parseDouble(xmlElement.getTextContent());
        setValue(new RTTolerance(content, isAbsolute));
    }

    @Override
    public void saveValueToXML(@Nonnull Element xmlElement) {
        if (getValue() == null)
            return;
        if (getValue().isAbsolute()) {
            xmlElement.setAttribute("type", "absolute");
        } else {
            xmlElement.setAttribute("type", "percent");
        }
        Double value = getValue().getTolerance();
        xmlElement.setTextContent(value.toString());
    }

}
