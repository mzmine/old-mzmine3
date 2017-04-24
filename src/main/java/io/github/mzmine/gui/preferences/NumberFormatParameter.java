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

package io.github.mzmine.gui.preferences;

import java.text.DecimalFormat;

import javax.annotation.Nonnull;

import org.w3c.dom.Element;

import io.github.mzmine.parameters.parametertypes.AbstractParameter;

/**
 * Parameter for number formatting setting
 */
public class NumberFormatParameter extends AbstractParameter<DecimalFormat> {

  private boolean showExponentOption;

  public NumberFormatParameter(@Nonnull String name, @Nonnull String description,
      @Nonnull String category, boolean showExponentOption, @Nonnull DecimalFormat defaultValue) {

    super(name, description, category, NumberFormatEditor.class, null);

    assert defaultValue != null;

    this.showExponentOption = showExponentOption;
    setValue(defaultValue);
  }

  @Override
  public @Nonnull NumberFormatParameter clone() {
    return this;
  }

  @Override
  public void loadValueFromXML(@Nonnull Element xmlElement) {
    String newPattern = xmlElement.getTextContent();
    DecimalFormat format = getValue();
    if (format == null)
      return;
    format.applyPattern(newPattern);
  }

  @Override
  public void saveValueToXML(@Nonnull Element xmlElement) {
    DecimalFormat format = getValue();
    if (format == null)
      return;
    String pattern = format.toPattern();
    xmlElement.setTextContent(pattern);
  }

  public boolean isShowExponentEnabled() {
    return showExponentOption;
  }

}
