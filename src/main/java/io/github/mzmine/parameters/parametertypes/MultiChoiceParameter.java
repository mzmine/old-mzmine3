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

package io.github.mzmine.parameters.parametertypes;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class MultiChoiceParameter<ValueType> extends AbstractParameter<List<ValueType>> {

  private final @Nonnull List<ValueType> choices;

  /**
   * We need the choices parameter non-null even when the length may be 0. We need it to determine
   * the class of the ValueType.
   */
  public MultiChoiceParameter(@Nonnull String name, @Nonnull String description,
      @Nonnull String category, @Nonnull List<ValueType> choices) {
    this(name, description, category, choices, null);
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  public MultiChoiceParameter(@Nonnull String name, @Nonnull String description,
      @Nonnull String category, @Nonnull List<ValueType> choices, List<ValueType> defaultValue) {
    super(name, description, category, (Class) MultiChoiceEditor.class, null);
    this.choices = choices;
    setValue(defaultValue);
  }

  @Override
  public @Nonnull MultiChoiceParameter<ValueType> clone() {
    MultiChoiceParameter<ValueType> copy =
        new MultiChoiceParameter<ValueType>(getName(), getDescription(), getCategory(), choices);
    copy.setValue(this.getValue());
    return copy;
  }

  @Override
  public void loadValueFromXML(@Nonnull Element xmlElement) {
    NodeList items = xmlElement.getElementsByTagName("item");
    ArrayList<ValueType> newValues = new ArrayList<ValueType>();
    for (int i = 0; i < items.getLength(); i++) {
      final String itemString = items.item(i).getTextContent();
      for (ValueType choice : choices) {
        if (choice.toString().equals(itemString)) {
          newValues.add(choice);
        }
      }
    }
    setValue(newValues);
  }

  @Override
  public void saveValueToXML(@Nonnull Element xmlElement) {
    List<ValueType> values = getValue();
    if (values == null)
      return;
    Document parentDocument = xmlElement.getOwnerDocument();
    for (ValueType item : values) {
      Element newElement = parentDocument.createElement("item");
      newElement.setTextContent(item.toString());
      xmlElement.appendChild(newElement);
    }
  }

  public List<ValueType> getChoices() {
    return choices;
  }

}
