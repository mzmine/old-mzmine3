/*
 * Copyright 2006-2015 The MZmine 3 Development Team
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
 * MZmine 2; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

package io.github.mzmine.parameters;

import java.text.NumberFormat;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.controlsfx.control.PropertySheet.Item;
import org.w3c.dom.Element;

/**
 * Parameter interface, represents parameters or variables used in the project
 */
public interface Parameter<ValueType> extends Item, Cloneable {

    @Override
    @Nonnull
    String getName();

    @Override
    @Nonnull
    String getDescription();

    @Override
    @Nonnull
    String getCategory();

    @Override
    @Nullable
    ValueType getValue();

    @Override
    void setValue(@Nullable Object newValue);

    void loadValueFromXML(@Nonnull Element xmlElement);

    void saveValueToXML(@Nonnull Element xmlElement);

    @Nonnull
    Parameter<ValueType> clone();

    @Nullable
    ParameterValidator<ValueType> getValidator();

    @Nullable
    NumberFormat getNumberFormat();

}
