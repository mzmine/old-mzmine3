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

package io.github.mzmine.parameters;

import org.controlsfx.control.PropertySheet;
import org.controlsfx.property.editor.PropertyEditor;
import org.controlsfx.validation.ValidationSupport;

/**
 * Parameter sheet view
 */
public class ParameterSheetView extends PropertySheet {

    private final ParameterSet parameters;
    private final ParameterEditorFactory editorFactory;

    public ParameterSheetView(ParameterSet parameters,
            ValidationSupport validationSupport) {

        this.parameters = parameters;

        setModeSwitcherVisible(false);
        setSearchBoxVisible(false);
        setMode(PropertySheet.Mode.NAME);
        setPrefSize(600.0, 400.0);

        // Set editor factory to keep track of which editing component belongs
        // to which parameter
        this.editorFactory = new ParameterEditorFactory(validationSupport);
        setPropertyEditorFactory(editorFactory);

        for (Parameter<?> p : parameters) {
            getItems().add(p);
        }

        // TODO: initially the decorations are missing?
        validationSupport.redecorate();

    }

    public <ValueType> PropertyEditor<ValueType> getEditorForParameter(
            Parameter<ValueType> parameter) {

        // Let's lookup the parameter by name, because the actual instance may
        // differ due to the cloning of parameter sets
        Parameter<?> actualParameter = parameters.getParameter(parameter);
        if (actualParameter == null)
            throw new IllegalArgumentException("Parameter "
                    + parameter.getName() + " not found in this component");

        return editorFactory.getEditorForItem(actualParameter);

    }

}
