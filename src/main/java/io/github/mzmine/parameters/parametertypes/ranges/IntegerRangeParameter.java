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

package io.github.mzmine.parameters.parametertypes.ranges;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.google.common.collect.Range;

import io.github.mzmine.parameters.ParameterValidator;
import io.github.mzmine.parameters.parametertypes.AbstractParameter;

public class IntegerRangeParameter extends AbstractParameter<Range<Integer>> {

  public IntegerRangeParameter(@Nonnull String name, @Nonnull String description,
      @Nonnull String category) {
    this(name, description, category, null, null);
  }

  public IntegerRangeParameter(@Nonnull String name, @Nonnull String description,
      @Nonnull String category, Range<Integer> defaultValue) {
    this(name, description, category, null, defaultValue);
  }

  public IntegerRangeParameter(@Nonnull String name, @Nonnull String description,
      @Nonnull String category, @Nullable ParameterValidator<Range<Integer>> validator) {
    this(name, description, category, validator, null);
  }

  public IntegerRangeParameter(@Nonnull String name, @Nonnull String description,
      @Nonnull String category, @Nullable ParameterValidator<Range<Integer>> validator,
      @Nullable Range<Integer> range) {
    super(name, description, category, IntegerRangeEditor.class, validator);
    setValue(range);
  }

  @Override
  public @Nonnull IntegerRangeParameter clone() {
    IntegerRangeParameter copy = new IntegerRangeParameter(getName(), getDescription(),
        getCategory(), getValidator(), getValue());
    return copy;
  }

  @Override
  public void loadValueFromXML(@Nonnull Element xmlElement) {
    NodeList minNodes = xmlElement.getElementsByTagName("min");
    if (minNodes.getLength() != 1)
      return;
    NodeList maxNodes = xmlElement.getElementsByTagName("max");
    if (maxNodes.getLength() != 1)
      return;
    String minText = minNodes.item(0).getTextContent();
    String maxText = maxNodes.item(0).getTextContent();
    Integer min = Integer.valueOf(minText);
    Integer max = Integer.valueOf(maxText);
    setValue(Range.closed(min, max));

  }

  @SuppressWarnings("null")
  @Override
  public void saveValueToXML(@Nonnull Element xmlElement) {
    if (getValue() == null)
      return;
    Document parentDocument = xmlElement.getOwnerDocument();
    Element newElement = parentDocument.createElement("min");
    newElement.setTextContent(String.valueOf(getValue().lowerEndpoint()));
    xmlElement.appendChild(newElement);
    newElement = parentDocument.createElement("max");
    newElement.setTextContent(String.valueOf(getValue().upperEndpoint()));
    xmlElement.appendChild(newElement);
  }

}
