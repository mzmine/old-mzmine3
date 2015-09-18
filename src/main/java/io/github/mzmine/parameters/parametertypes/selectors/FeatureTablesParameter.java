/*
 * Copyright 2006-2015 The MZmine 2 Development Team
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

package io.github.mzmine.parameters.parametertypes.selectors;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.google.common.base.Strings;

import io.github.msdk.datamodel.featuretables.FeatureTable;
import io.github.mzmine.main.MZmineCore;
import io.github.mzmine.parameters.parametertypes.AbstractParameter;

public class FeatureTablesParameter
        extends AbstractParameter<FeatureTablesSelection> {

    private static final @Nonnull String NAME = "Feature tables";
    private static final @Nonnull String DESCRIPTION = "Feature tables that this module will take as its input.";
    private static final @Nonnull String CATEGORY = "Input";

    public FeatureTablesParameter() {
        super(NAME, DESCRIPTION, CATEGORY, FeatureTablesEditor.class, null);
    }

    @Override
    public @Nonnull FeatureTablesParameter clone() {
        FeatureTablesParameter copy = new FeatureTablesParameter();
        copy.setValue(getValue());
        return copy;
    }

    @Override
    public void loadValueFromXML(@Nonnull Element xmlElement) {

        List<FeatureTable> currentDataFiles = MZmineCore.getCurrentProject()
                .getFeatureTables();

        FeatureTablesSelectionType selectionType;
        final String attrValue = xmlElement.getAttribute("type");

        if (Strings.isNullOrEmpty(attrValue))
            selectionType = FeatureTablesSelectionType.GUI_SELECTED_FEATURE_TABLES;
        else
            selectionType = FeatureTablesSelectionType
                    .valueOf(xmlElement.getAttribute("type"));

        List<FeatureTable> specificFiles = new ArrayList<>();

        NodeList items = xmlElement.getElementsByTagName("specific_name");
        for (int i = 0; i < items.getLength(); i++) {
            String itemString = items.item(i).getTextContent();
            for (FeatureTable df : currentDataFiles) {
                if (df.getName().equals(itemString))
                    specificFiles.add(df);
            }
        }

        String namePattern = null;
        items = xmlElement.getElementsByTagName("name_pattern");
        for (int i = 0; i < items.getLength(); i++) {
            namePattern = items.item(i).getTextContent();
        }

        setValue(new FeatureTablesSelection(selectionType, specificFiles,
                namePattern));
    }

    @Override
    public void saveValueToXML(@Nonnull Element xmlElement) {
        FeatureTablesSelection value = getValue();
        if (value == null)
            return;
        Document parentDocument = xmlElement.getOwnerDocument();
        xmlElement.setAttribute("type", value.getSelectionType().name());

        if (value.getSpecificFeatureTables() != null) {
            for (FeatureTable item : value.getSpecificFeatureTables()) {
                Element newElement = parentDocument
                        .createElement("specific_name");
                newElement.setTextContent(item.getName());
                xmlElement.appendChild(newElement);
            }
        }

        if (value.getNamePattern() != null) {
            Element newElement = parentDocument.createElement("name_pattern");
            newElement.setTextContent(value.getNamePattern());
            xmlElement.appendChild(newElement);
        }

    }

    public void switchType(FeatureTablesSelectionType newType) {
        FeatureTablesSelection curValue = getValue();
        FeatureTablesSelection newValue;
        if (curValue == null)
            newValue = new FeatureTablesSelection(newType, null, null);
        else
            newValue = new FeatureTablesSelection(newType,
                    curValue.getSpecificFeatureTables(),
                    curValue.getNamePattern());
        setValue(newValue);
    }
}
