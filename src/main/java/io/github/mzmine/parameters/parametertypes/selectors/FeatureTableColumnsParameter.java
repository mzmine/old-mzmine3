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

package io.github.mzmine.parameters.parametertypes.selectors;

import javax.annotation.Nonnull;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import io.github.mzmine.parameters.parametertypes.AbstractParameter;
import javafx.collections.ObservableList;

public class FeatureTableColumnsParameter
        extends AbstractParameter<FeatureTableColumnsSelection> {

    private static final @Nonnull String NAME = "Column names";
    private static final @Nonnull String DESCRIPTION = "Feature table column names to include. You can use wildcards (*), for example *area.";
    private static final @Nonnull String CATEGORY = "Input";

    public FeatureTableColumnsParameter() {
        super(NAME, DESCRIPTION, CATEGORY, FeatureTableColumnsEditor.class,
                null);
    }

    @Override
    public @Nonnull FeatureTableColumnsParameter clone() {
        FeatureTableColumnsParameter copy = new FeatureTableColumnsParameter();
        FeatureTableColumnsSelection value = getValue();
        if (value != null) {
            FeatureTableColumnsSelection valueCopy = value.clone();
            copy.setValue(valueCopy);
        }
        return copy;
    }

    @Override
    public void loadValueFromXML(@Nonnull Element xmlElement) {

        FeatureTableColumnsSelection selection = new FeatureTableColumnsSelection();
        ObservableList<String> namePatterns = selection.getNamePatterns();

        NodeList items = xmlElement.getElementsByTagName("name_pattern");
        for (int i = 0; i < items.getLength(); i++) {
            String itemString = items.item(i).getTextContent();
            namePatterns.add(itemString);
        }

        setValue(selection);
    }

    @Override
    public void saveValueToXML(@Nonnull Element xmlElement) {
        FeatureTableColumnsSelection value = getValue();
        if (value == null)
            return;
        Document parentDocument = xmlElement.getOwnerDocument();

        for (String item : value.getNamePatterns()) {
            Element newElement = parentDocument.createElement("name_pattern");
            newElement.setTextContent(item);
            xmlElement.appendChild(newElement);
        }

    }

}
