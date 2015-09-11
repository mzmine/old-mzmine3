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
 * MZmine 2; if not, write to the Free Software Foundation, Inc., 51 Franklin
 * St, Fifth Floor, Boston, MA 02110-1301 USA
 */

package io.github.mzmine.parameters.parametertypes;

import javax.annotation.Nonnull;

import org.controlsfx.control.PropertySheet;

public abstract class AbstractParameter implements PropertySheet.Item {

    private final String name, category, description;
    private final Class<?> type;
    private Object value;

    public AbstractParameter(@Nonnull String name, @Nonnull String category,
            @Nonnull String description, @Nonnull Class<?> type) {
        this.name = name;
        this.category = category;
        this.description = description;
        this.type = type;
    }

    @Override
    public String getCategory() {
        return category;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Class<?> getType() {
        return type;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void setValue(Object value) {
        if (type.isInstance(value))
            throw new IllegalArgumentException(
                    "Invalid value class: " + value.getClass());
        this.value = value;
    }

}
