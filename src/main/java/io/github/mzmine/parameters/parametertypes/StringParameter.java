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
import javax.annotation.Nullable;

import org.w3c.dom.Element;

import io.github.mzmine.parameters.ParameterValidator;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class StringParameter extends AbstractParameter<String> {

    private EventHandler<ActionEvent> autoSetAction;

    public StringParameter(@Nonnull String name, @Nonnull String description,
            @Nonnull String category) {
        this(name, description, category, null, null, null);
    }

    public StringParameter(@Nonnull String name, @Nonnull String description,
            @Nonnull String category, String defaultValue) {
        this(name, description, category, null, defaultValue, null);
    }

    public StringParameter(@Nonnull String name, @Nonnull String description,
            @Nonnull String category,
            @Nullable ParameterValidator<String> validator) {
        this(name, description, category, validator, null, null);
    }

    public StringParameter(@Nonnull String name, @Nonnull String description,
            @Nonnull String category,
            @Nullable ParameterValidator<String> validator, String defaultValue) {
        this(name, description, category, validator, defaultValue, null);
    }

    public StringParameter(@Nonnull String name, @Nonnull String description,
            @Nonnull String category,
            @Nullable ParameterValidator<String> validator,
            @Nullable String defaultValue,
            @Nullable EventHandler<ActionEvent> autoSetAction) {
        super(name, description, category, StringEditor.class, validator, null);
        setValue(defaultValue);
        this.autoSetAction = autoSetAction;
    }

    @Override
    public @Nonnull StringParameter clone() {
        StringParameter copy = new StringParameter(getName(), getDescription(),
                getCategory(), getValidator(), getValue(), autoSetAction);
        return copy;
    }

    @Override
    public void loadValueFromXML(@Nonnull Element xmlElement) {
        String content = xmlElement.getTextContent();
        setValue(content);
    }

    @Override
    public void saveValueToXML(@Nonnull Element xmlElement) {
        if (getValue() == null)
            return;
        xmlElement.setTextContent(getValue());
    }

    public EventHandler<ActionEvent> getAutoSetAction() {
        return autoSetAction;
    }

    public void setAutoSetAction(EventHandler<ActionEvent> autoSetAction) {
        this.autoSetAction = autoSetAction;
    }

}
