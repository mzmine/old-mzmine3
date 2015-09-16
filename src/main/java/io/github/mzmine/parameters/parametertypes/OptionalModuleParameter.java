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

package io.github.mzmine.parameters.parametertypes;

import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.controlsfx.property.editor.PropertyEditor;
import org.w3c.dom.Element;

import io.github.mzmine.parameters.Parameter;
import io.github.mzmine.parameters.ParameterSet;
import io.github.mzmine.parameters.ParameterValidator;

/**
 * Parameter represented by check box with additional sub-parameters
 * 
 */
public class OptionalModuleParameter implements Parameter<Boolean> {

    private final @Nonnull String name, description;
    private final ParameterSet embeddedParameters;
    private Boolean value;

    public OptionalModuleParameter(@Nonnull String name,
            @Nonnull String description, ParameterSet embeddedParameters) {
        this.name = name;
        this.description = description;
        this.embeddedParameters = embeddedParameters;
    }

    public ParameterSet getEmbeddedParameters() {
        return embeddedParameters;
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
    public Boolean getValue() {
        return value;
    }

    @Override
    public void setValue(@Nullable Object value) {
        this.value = (Boolean) value;
    }

    @Override
    public Class<?> getType() {
        return Boolean.class;
    }

    @Override
    public @Nonnull OptionalModuleParameter clone() {
        final ParameterSet embeddedParametersClone = embeddedParameters.clone();
        final OptionalModuleParameter copy = new OptionalModuleParameter(name,
                description, embeddedParametersClone);
        copy.setValue(this.getValue());
        return copy;
    }

    @Override
    public void loadValueFromXML(@Nonnull Element xmlElement) {
        embeddedParameters.loadValuesFromXML(xmlElement);
        String selectedAttr = xmlElement.getAttribute("selected");
        this.value = Boolean.valueOf(selectedAttr);
    }

    @Override
    public void saveValueToXML(@Nonnull Element xmlElement) {
        if (value != null)
            xmlElement.setAttribute("selected", value.toString());
        embeddedParameters.saveValuesToXML(xmlElement);
    }

    @Override
    public Optional<Class<? extends PropertyEditor<?>>> getPropertyEditorClass() {
        return Optional.of(OptionalModuleEditor.class);
    }

    @Override
    @Nullable
    public ParameterValidator<Boolean> getValidator() {

        return (val, msg) -> {
            // If we are selected, we have to check the validity of embedded
            // parameters
            if (val) {
                for (Parameter<?> par : embeddedParameters.getParameters()) {
                    ParameterValidator p = par.getValidator();
                    if (p == null)
                        continue;
                    boolean result = p.checkValue(par.getValue(), msg);
                    if (!result)
                        return false;
                }
            }
            return true;
        };
    }

}
