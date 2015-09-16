/*
 * Copyright 2006-2015 The MZmine 2 Development Team
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

package io.github.mzmine.gui.preferences;

import java.text.DecimalFormat;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.controlsfx.property.editor.PropertyEditor;
import org.w3c.dom.Element;

import io.github.mzmine.parameters.Parameter;

/**
 * Simple Parameter implementation
 * 
 * 
 */
public class NumberFormatParameter implements Parameter<DecimalFormat> {

    private final @Nonnull String name, description;
    private boolean showExponentOption;
    private DecimalFormat value;

    public NumberFormatParameter(@Nonnull String name,
            @Nonnull String description, boolean showExponentOption,
            DecimalFormat defaultValue) {

        assert defaultValue != null;

        this.name = name;
        this.description = description;
        this.showExponentOption = showExponentOption;
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
    public DecimalFormat getValue() {
        return value;
    }

    @Override
    public void setValue(@Nullable Object value) {
        this.value = (DecimalFormat) value;
    }

    @Override
    public @Nonnull NumberFormatParameter clone() {
        return this;
    }

    @Override
    public void loadValueFromXML(@Nonnull Element xmlElement) {
        String newPattern = xmlElement.getTextContent();
        value.applyPattern(newPattern);
    }

    @Override
    public void saveValueToXML(@Nonnull Element xmlElement) {
        xmlElement.setTextContent(value.toPattern());
    }

    @Override
    public Class<?> getType() {
        return DecimalFormat.class;
    }

    @Override
    public Optional<Class<? extends PropertyEditor<?>>> getPropertyEditorClass() {
        return Optional.of(NumberFormatEditor.class);
    }

    public boolean isShowExponentEnabled() {
        return showExponentOption;
    }

}
