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

package io.github.mzmine.parameters.parametertypes.tolerances;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import io.github.msdk.util.MZTolerance;
import io.github.mzmine.parameters.ParameterValidator;
import io.github.mzmine.parameters.parametertypes.AbstractParameter;

public class MZToleranceParameter extends AbstractParameter<MZTolerance> {

    public MZToleranceParameter(@Nonnull String name,
            @Nonnull String description, @Nonnull String category) {
        this(name, description, category, null, null);
    }

    public MZToleranceParameter(@Nonnull String name,
            @Nonnull String description, @Nonnull String category,
            MZTolerance defaultValue) {
        this(name, description, category, null, defaultValue);
    }

    public MZToleranceParameter(@Nonnull String name,
            @Nonnull String description, @Nonnull String category,
            @Nullable ParameterValidator<MZTolerance> validator) {
        this(name, description, category, validator, null);
    }

    public MZToleranceParameter(@Nonnull String name,
            @Nonnull String description, @Nonnull String category,
            @Nullable ParameterValidator<MZTolerance> validator,
            @Nullable MZTolerance defaultValue) {
        super(name, description, category, MZToleranceEditor.class, validator);
        setValue(defaultValue);
    }

    @Override
    public @Nonnull MZToleranceParameter clone() {
        MZToleranceParameter copy = new MZToleranceParameter(getName(),
                getDescription(), getCategory(), getValidator(), getValue());
        return copy;
    }

    @Override
    public void loadValueFromXML(@Nonnull Element xmlElement) {
        // Set some default values
        double mzTolerance = 0.005;
        double ppmTolerance = 5;
        NodeList items = xmlElement.getElementsByTagName("absolutetolerance");
        for (int i = 0; i < items.getLength(); i++) {
            String itemString = items.item(i).getTextContent();
            mzTolerance = Double.parseDouble(itemString);
        }
        items = xmlElement.getElementsByTagName("ppmtolerance");
        for (int i = 0; i < items.getLength(); i++) {
            String itemString = items.item(i).getTextContent();
            ppmTolerance = Double.parseDouble(itemString);
        }
        setValue(new MZTolerance(mzTolerance, ppmTolerance));
    }

    @Override
    public void saveValueToXML(@Nonnull Element xmlElement) {
        if (getValue() == null)
            return;
        Document parentDocument = xmlElement.getOwnerDocument();
        Element newElement = parentDocument.createElement("absolutetolerance");
        newElement.setTextContent(String.valueOf(getValue().getMzTolerance()));
        xmlElement.appendChild(newElement);
        newElement = parentDocument.createElement("ppmtolerance");
        newElement.setTextContent(String.valueOf(getValue().getPpmTolerance()));
        xmlElement.appendChild(newElement);
    }
}
