/*
 * Copyright 2006-2016 The MZmine 3 Development Team
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

import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.w3c.dom.Element;

import io.github.mzmine.parameters.ParameterSet;
import io.github.mzmine.parameters.ParameterValidator;

public class ToggleParameterSetParameter<ValueType> extends AbstractParameter<ValueType> {

  private final @Nonnull LinkedHashMap<String, ParameterSet> toggleValues;

  public ToggleParameterSetParameter(@Nonnull String name, @Nonnull String description,
      @Nonnull String category, LinkedHashMap<String, ParameterSet> toggleValues) {
    this(name, description, category, null, toggleValues, null);
  }

  public ToggleParameterSetParameter(@Nonnull String name, @Nonnull String description,
      @Nonnull String category, LinkedHashMap<String, ParameterSet> toggleValues,
      String defaultValue) {
    this(name, description, category, null, toggleValues, defaultValue);
  }

  public ToggleParameterSetParameter(@Nonnull String name, @Nonnull String description,
      @Nonnull String category, @Nullable ParameterValidator<ValueType> validator,
      LinkedHashMap<String, ParameterSet> toggleValues) {
    this(name, description, category, validator, toggleValues, null);
  }

  @SuppressWarnings("unchecked")
  public ToggleParameterSetParameter(@Nonnull String name, @Nonnull String description,
      @Nonnull String category, @Nullable ParameterValidator<ValueType> validator,
      LinkedHashMap<String, ParameterSet> toggleValues, @Nullable String defaultValue) {
    super(name, description, category, (Class) ToggleParameterSetEditor.class, validator);
    this.toggleValues = toggleValues;
    setValue(defaultValue);
  }

  @Override
  public @Nonnull ToggleParameterSetParameter<ValueType> clone() {
    ToggleParameterSetParameter<ValueType> copy = new ToggleParameterSetParameter<ValueType>(
        getName(), getDescription(), getCategory(), getValidator(), toggleValues);
    copy.setValue(this.getValue());
    return copy;
  }

  public LinkedHashMap<String, ParameterSet> getToggleValues() {
    return toggleValues;
  }

  @Override
  public void loadValueFromXML(@Nonnull Element xmlElement) {
    String stringValue = xmlElement.getTextContent();
    for (HashMap.Entry<String, ParameterSet> entry : toggleValues.entrySet()) {
      if (entry.getKey().toString().equals(stringValue)) {
        setValue(entry.getKey());
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
