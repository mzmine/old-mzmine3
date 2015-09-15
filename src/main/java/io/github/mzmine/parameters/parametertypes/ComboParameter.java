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

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.controlsfx.property.editor.PropertyEditor;
import org.w3c.dom.Element;

import io.github.mzmine.parameters.Parameter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ComboParameter<Type> implements Parameter<Type> {

    private final String name, description, category;
    private ObservableList<Type> options; 
    private Type value;

    public ComboParameter(String name, String description, String category) {
        this(name, description, category, null, null);
    }

    public ComboParameter(String name, String description, String category,
            List<Type> options, Type defaultValue) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.options = FXCollections.observableList(options);
        this.value = defaultValue;
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
    public String getCategory() {
        return category;
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
    public void setValue(Object value) {
        this.value = (Type) value;
    }

    @Override
    public ComboParameter<Type> clone() {
        ComboParameter<Type> copy = new ComboParameter<Type>(name, description, category, options, value);
        return copy;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public void loadValueFromXML(Element xmlElement) {
        //value = xmlElement.getTextContent();
    }

    @Override
    public void saveValueToXML(Element xmlElement) {
        if (value == null)
            return;
        //xmlElement.setTextContent(value);
    }

    @Override
    public boolean checkValue(Collection<String> errorMessages) {
        if (value == null) {
            errorMessages.add(name + " is not set properly");
            return false;
        }
        return true;
    }
    
    @Override
    public Optional<Class<? extends PropertyEditor<?>>> getPropertyEditorClass() {
        return Optional.of(ComboEditor.class);
    }
    
    ObservableList<Type> getOptions() {
        return options;
    }



}
