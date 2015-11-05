/*
 * Copyright 2006-2015 The MZmine 3 Development Team
 * 
 * This file is part of MZmine 3.
 * 
 * MZmine 3 is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * MZmine 3 is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * MZmine 3; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

package io.github.mzmine.parameters.parametertypes;

import java.text.NumberFormat;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.controlsfx.property.editor.PropertyEditor;

import io.github.mzmine.parameters.Parameter;
import io.github.mzmine.parameters.ParameterEditor;
import io.github.mzmine.parameters.ParameterValidator;

/**
 * This parameter stores filenames
 */
public abstract class AbstractParameter<ValueType>
        implements Parameter<ValueType> {

    private ValueType value;

    private final @Nonnull String name, description, category;
    private final Optional<Class<? extends ParameterEditor<ValueType>>> editorClass;
    private @Nullable ParameterValidator<ValueType> validator;

    public AbstractParameter(@Nonnull String name, @Nonnull String description,
            @Nonnull String category,
            @Nonnull Class<? extends ParameterEditor<ValueType>> editorClass,
            @Nullable ParameterValidator<ValueType> validator) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.editorClass = Optional.of(editorClass);
        this.validator = validator;
    }

    @Override
    public @Nonnull String getName() {
        return name;
    }

    @Override
    public @Nonnull String getDescription() {
        return description;
    }

    @Override
    public @Nonnull String getCategory() {
        return category;
    }

    @Override
    public Class<?> getType() {
        // We can return Object class, because this is actually not used for
        // anything
        return Object.class;
    }

    @Override
    public @Nullable ValueType getValue() {
        return value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setValue(@Nullable Object newValue) {
        this.value = (ValueType) newValue;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public Optional<Class<? extends PropertyEditor<?>>> getPropertyEditorClass() {
        return (Optional) editorClass;
    }

    @Override
    @Nullable
    public ParameterValidator<ValueType> getValidator() {
        return validator;
    }

    public void setValidator(
            @Nullable ParameterValidator<ValueType> validator) {
        this.validator = validator;
    }

    abstract public @Nonnull Parameter<ValueType> clone();
}
