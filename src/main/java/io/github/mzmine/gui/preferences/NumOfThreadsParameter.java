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

import javax.annotation.Nonnull;

import org.w3c.dom.Element;

import com.google.common.base.Strings;

import io.github.mzmine.parameters.parametertypes.AbstractParameter;

public class NumOfThreadsParameter extends AbstractParameter<NumOfThreadsValue> {

  private static final @Nonnull String name = "Number of parallel tasks";
  private static final @Nonnull String description =
      "Maximum number of tasks running simultaneously";
  private static final @Nonnull String category = "Parallel tasks";

  public NumOfThreadsParameter() {
    super(name, description, category, NumOfThreadsEditor.class, null);

    // Provide a default value
    setValue(new NumOfThreadsValue(true, 4));
  }

  @Override
  public @Nonnull NumOfThreadsParameter clone() {
    return this;
  }

  @Override
  public void loadValueFromXML(@Nonnull Element xmlElement) {
    String attrValue = xmlElement.getAttribute("isautomatic");
    boolean automatic = true;
    if (attrValue.length() > 0) {
      automatic = Boolean.valueOf(attrValue);
    }
    int manualValue = 4;
    String textContent = xmlElement.getTextContent();
    if (!Strings.isNullOrEmpty(textContent)) {
      manualValue = Integer.valueOf(textContent);
    }
    setValue(new NumOfThreadsValue(automatic, manualValue));
  }

  @Override
  public void saveValueToXML(@Nonnull Element xmlElement) {
    NumOfThreadsValue value = getValue();
    if (value == null)
      return;
    xmlElement.setAttribute("isautomatic", String.valueOf(value.isAutomatic()));
    xmlElement.setTextContent(value.getManualValue().toString());
  }

}
