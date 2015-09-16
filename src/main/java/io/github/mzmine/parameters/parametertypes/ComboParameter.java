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

import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.controlsfx.property.editor.PropertyEditor;
import org.w3c.dom.Element;

import io.github.mzmine.parameters.Parameter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ComboParameter<Type> implements Parameter<Type> {

    private final @Nonnull String name, description;
    private ObservableList<Type> options;
    private Type value;

    public ComboParameter(@Nonnull String name, @Nonnull String description) {
        this(name, description, null, null);
    }

    public ComboParameter(@Nonnull String name, @Nonnull String description,
            List<Type> options, Type defaultValue) {
        this.name = name;
        this.description = description;
        this.options = FXCollections.observableList(options);
        this.value = defaultValue;
    }

    /**
     * @see net.sf.mzmine.data.Parameter#getName()
     */
    @Override
    public @Nonnull String getName() {
        return name;
    }

    /**
     * @see net.sf.mzmine.data.Parameter#getDescription()
     */
    @Override
    public @Nonnull String getDescription() {
        return description;
    }

    @Override
    public Class<?> getType() {
        return String.class;
    }

    public Type getValue() {
        return value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setValue(@Nullable Object value) {
        this.value = (Type) value;
    }

    @Override
    public @Nonnull ComboParameter<Type> clone() {
        ComboParameter<Type> copy = new ComboParameter<Type>(name, description,
                options, value);
        return copy;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public void loadValueFromXML(@Nonnull Element xmlElement) {
        // value = xmlElement.getTextContent();
    }

    @Override
    public void saveValueToXML(@Nonnull Element xmlElement) {
        if (value == null)
            return;
        // xmlElement.setTextContent(value);
    }

    @Override
    public Optional<Class<? extends PropertyEditor<?>>> getPropertyEditorClass() {
        return Optional.of(ComboEditor.class);
    }

    ObservableList<Type> getOptions() {
        return options;
    }

}
