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
 * MZmine 3; if not, write to the Free Software Foundation, Inc., 51 Franklin
 * St, Fifth Floor, Boston, MA 02110-1301 USA
 */

package io.github.mzmine.parameters.parametertypes;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.w3c.dom.Element;

import io.github.mzmine.parameters.Parameter;
import io.github.mzmine.parameters.ParameterSet;
import io.github.mzmine.parameters.ParameterValidator;

/**
 * Parameter represented by check box with additional sub-parameters
 * 
 */
public class OptionalModuleParameter extends AbstractParameter<Boolean> {

    private final ParameterSet embeddedParameters;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public OptionalModuleParameter(@Nonnull String name,
            @Nonnull String description, @Nonnull String category,
            ParameterSet embeddedParameters) {
        super(name, description, category, OptionalModuleEditor.class, null);

        this.embeddedParameters = embeddedParameters;

        setValidator((currentValue, messages) -> {
            // If we are selected, we have to check the validity of embedded
            // parameters
            if (currentValue) {
                List<String> voidMsgs = new ArrayList<>();
                for (Parameter<?> embeddedParam : embeddedParameters
                        .getParameters()) {
                    ParameterValidator embeddedValidator = embeddedParam
                            .getValidator();
                    if (embeddedValidator == null)
                        continue;
                    Object value = embeddedParam.getValue();
                    boolean result = embeddedValidator.checkValue(value,
                            voidMsgs);
                    if (!result) {
                        messages.add("Parameters not set properly");
                        return false;
                    }

                }
            }
            return true;
        });
    }

    public ParameterSet getEmbeddedParameters() {
        return embeddedParameters;
    }

    @Override
    public @Nonnull OptionalModuleParameter clone() {
        final ParameterSet embeddedParametersClone = embeddedParameters.clone();
        final OptionalModuleParameter copy = new OptionalModuleParameter(
                getName(), getDescription(), getCategory(),
                embeddedParametersClone);
        copy.setValue(this.getValue());
        return copy;
    }

    @Override
    public void loadValueFromXML(@Nonnull Element xmlElement) {
        embeddedParameters.loadValuesFromXML(xmlElement);
        String selectedAttr = xmlElement.getAttribute("selected");
        setValue(Boolean.valueOf(selectedAttr));
    }

    @Override
    public void saveValueToXML(@Nonnull Element xmlElement) {
        Boolean value = getValue();
        if (value != null)
            xmlElement.setAttribute("selected", value.toString());
        embeddedParameters.saveValuesToXML(xmlElement);
    }

}
