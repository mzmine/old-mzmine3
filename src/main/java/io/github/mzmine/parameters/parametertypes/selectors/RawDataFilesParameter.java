/*
 * Copyright 2006-2015 The MZmine 3 Development Team
 * 
 * This file is part of MZmine 3.
 * 
 * MZmine 3 is free software; you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * MZmine 3 is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with MZmine 3; if not,
 * write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301
 * USA
 */

package io.github.mzmine.parameters.parametertypes.selectors;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.google.common.base.Strings;

import io.github.msdk.datamodel.rawdata.RawDataFile;
import io.github.mzmine.main.MZmineCore;
import io.github.mzmine.parameters.parametertypes.AbstractParameter;

public class RawDataFilesParameter extends AbstractParameter<RawDataFilesSelection> {

  private static final @Nonnull String NAME = "Raw data files";
  private static final @Nonnull String DESCRIPTION =
      "Raw data files that this module will take as its input.";
  private static final @Nonnull String CATEGORY = "Input";

  public RawDataFilesParameter() {
    super(NAME, DESCRIPTION, CATEGORY, RawDataFilesEditor.class, null);
  }

  @Override
  public @Nonnull RawDataFilesParameter clone() {
    RawDataFilesParameter copy = new RawDataFilesParameter();
    copy.setValue(getValue());
    return copy;
  }

  @Override
  public void loadValueFromXML(@Nonnull Element xmlElement) {

    List<RawDataFile> currentDataFiles = MZmineCore.getCurrentProject().getRawDataFiles();

    RawDataFilesSelectionType selectionType;
    final String attrValue = xmlElement.getAttribute("type");

    if (Strings.isNullOrEmpty(attrValue))
      selectionType = RawDataFilesSelectionType.GUI_SELECTED_FILES;
    else
      selectionType = RawDataFilesSelectionType.valueOf(xmlElement.getAttribute("type"));

    List<RawDataFile> specificFiles = new ArrayList<>();

    NodeList items = xmlElement.getElementsByTagName("specific_name");
    for (int i = 0; i < items.getLength(); i++) {
      String itemString = items.item(i).getTextContent();
      for (RawDataFile df : currentDataFiles) {
        if (df.getName().equals(itemString))
          specificFiles.add(df);
      }
    }

    String namePattern = null;
    items = xmlElement.getElementsByTagName("name_pattern");
    for (int i = 0; i < items.getLength(); i++) {
      namePattern = items.item(i).getTextContent();
    }

    setValue(new RawDataFilesSelection(selectionType, specificFiles, namePattern));
  }

  @Override
  public void saveValueToXML(@Nonnull Element xmlElement) {
    RawDataFilesSelection value = getValue();
    if (value == null)
      return;
    Document parentDocument = xmlElement.getOwnerDocument();
    xmlElement.setAttribute("type", value.getSelectionType().name());

    if (value.getSpecificFiles() != null) {
      for (RawDataFile item : value.getSpecificFiles()) {
        Element newElement = parentDocument.createElement("specific_name");
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

  public void switchType(RawDataFilesSelectionType newType) {
    RawDataFilesSelection curValue = getValue();
    RawDataFilesSelection newValue;
    if (curValue == null)
      newValue = new RawDataFilesSelection(newType, null, null);
    else
      newValue = new RawDataFilesSelection(newType, curValue.getSpecificFiles(),
          curValue.getNamePattern());
    setValue(newValue);
  }

}
