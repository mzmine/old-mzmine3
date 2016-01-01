/*
 * Copyright 2006-2016 The MZmine 3 Development Team
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.controlsfx.control.PropertySheet;
import org.controlsfx.control.PropertySheet.Item;
import org.controlsfx.property.editor.DefaultPropertyEditorFactory;
import org.controlsfx.property.editor.PropertyEditor;
import org.controlsfx.validation.ValidationMessage;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import javafx.scene.control.Control;

class ParameterEditorFactory extends DefaultPropertyEditorFactory {

    private final Map<Item, PropertyEditor<?>> editorsMap;
    private final ValidationSupport validationSupport;

    public ParameterEditorFactory(ValidationSupport validationSupport) {
        this.editorsMap = new HashMap<>();
        this.validationSupport = validationSupport;
    }

    @Override
    public PropertyEditor<?> call(PropertySheet.Item item) {

        if (!(item instanceof Parameter))
            throw new IllegalArgumentException(
                    "This ParameterEditorFactory can be only used for Parameter instances");

        PropertyEditor<?> editor = super.call(item);

        // Save the reference for the editor
        editorsMap.put(item, editor);

        if (editor instanceof ParameterEditor) {
            addValidator(validationSupport, (Parameter<?>) item,
                    (ParameterEditor<?>) editor);
        }

        return editor;
    }

    @SuppressWarnings("unchecked")
    <ValueType> PropertyEditor<ValueType> getEditorForItem(Item item) {
        return (PropertyEditor<ValueType>) editorsMap.get(item);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void addValidator(ValidationSupport validationSupport,
            Parameter<?> p, ParameterEditor<?> pe) {

        ParameterValidator pv = p.getValidator();
        if (pv == null)
            return;

        Control mainControl = pe.getMainControl();
        if (mainControl == null)
            return;

        if (mainControl != null && pv != null) {

            // Create the official validator
            Validator<?> validator = (control, value) -> {
                ValidationResult result = new ValidationResult();
                Object currentVal = pe.getValue();
                List<String> errors = new ArrayList<>();
                if (pv.checkValue(currentVal, errors))
                    return result;
                // IF no message was produced, add our own message
                if (errors.isEmpty())
                    errors.add(p.getName() + " is not set properly");
                // Copy the messages to the result
                for (String er : errors) {
                    String m = p.getName() + ": " + er;
                    ValidationMessage msg = ValidationMessage.error(control, m);
                    result.add(msg);
                }
                return result;
            };

            // Register the validator
            validationSupport.registerValidator(mainControl, false, validator);

        }
    }
}
