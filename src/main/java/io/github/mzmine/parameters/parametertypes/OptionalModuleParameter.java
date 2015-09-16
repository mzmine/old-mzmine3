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

import java.util.Collection;
import java.util.Optional;

import org.controlsfx.property.editor.PropertyEditor;
import org.w3c.dom.Element;

import io.github.mzmine.parameters.Parameter;
import io.github.mzmine.parameters.ParameterSet;

/**
 * Parameter represented by check box with additional sub-parameters
 * 
 */
public class OptionalModuleParameter implements
	Parameter<Boolean> {

    private final String name, description;
    private final ParameterSet embeddedParameters;
    private Boolean value;

    public OptionalModuleParameter(String name, String description,
	    ParameterSet embeddedParameters) {
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
    public String getName() {
	return name;
    }

    /**
     * @see net.sf.mzmine.data.Parameter#getDescription()
     */
    @Override
    public String getDescription() {
	return description;
    }

    @Override
    public Boolean getValue() {
	return value;
    }

    @Override
    public void setValue(Object value) {
	this.value = (Boolean) value;
    }

    @Override
    public Class<?> getType() {
        return Boolean.class;
    }

    @Override
    public OptionalModuleParameter clone() {
	final ParameterSet embeddedParametersClone = embeddedParameters
		.clone();
	final OptionalModuleParameter copy = new OptionalModuleParameter(name,
		description, embeddedParametersClone);
	copy.setValue(this.getValue());
	return copy;
    }



    @Override
    public void loadValueFromXML(Element xmlElement) {
	embeddedParameters.loadValuesFromXML(xmlElement);
	String selectedAttr = xmlElement.getAttribute("selected");
	this.value = Boolean.valueOf(selectedAttr);
    }

    @Override
    public void saveValueToXML(Element xmlElement) {
	if (value != null)
	    xmlElement.setAttribute("selected", value.toString());
	embeddedParameters.saveValuesToXML(xmlElement);
    }

    @Override
    public boolean checkValue(Collection<String> errorMessages) {
	if (value == null) {
	    errorMessages.add(name + " is not set properly");
	    return false;
	}
	if (value == true) {
	    return embeddedParameters.checkParameterValues(errorMessages);
	}
	return true;
    }
    
    @Override
    public Optional<Class<? extends PropertyEditor<?>>> getPropertyEditorClass() {
        return Optional.of(OptionalModuleEditor.class);
    }

}
