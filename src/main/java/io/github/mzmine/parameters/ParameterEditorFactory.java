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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.controlsfx.control.PropertySheet;
import org.controlsfx.control.PropertySheet.Item;
import org.controlsfx.property.editor.DefaultPropertyEditorFactory;
import org.controlsfx.property.editor.PropertyEditor;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import javafx.scene.control.Control;

class ParameterEditorFactory extends DefaultPropertyEditorFactory {

    private final Map<Item, PropertyEditor<?>> editorsMap;
    private final ValidationSupport validationSupport;

    public ParameterEditorFactory(ValidationSupport validationSupport) {
        this.validationSupport = validationSupport;
        this.editorsMap = new HashMap<>();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public PropertyEditor<?> call(PropertySheet.Item item) {

        if (!(item instanceof Parameter))
            throw new IllegalArgumentException(
                    "This ParameterEditorFactory can be only used for Parameter instances");
        PropertyEditor<?> editor = super.call(item);

        editorsMap.put(item, editor);

        Parameter<?> p = (Parameter<?>) item;
        if ((p.getValidator() != null) && (editor instanceof ParameterEditor)) {
            ParameterValidator mainValidator = p.getValidator();
            ParameterEditor<?> ve = (ParameterEditor<?>) editor;
            Control c = ve.getMainControl();

            if (c != null && mainValidator != null) {

                List<String> errors = new ArrayList<>();
                Predicate myValidator = val -> {
                    Object currentVal = ve.getValue();
                    return mainValidator.checkValue(currentVal, errors);
                };
                Validator<?> v = Validator.createPredicateValidator(myValidator,
                        p.getName() + " is not set properly");
                validationSupport.registerValidator(c, v);

            }
        }

        return editor;
    }

    PropertyEditor<?> getEditorForItem(Item item) {
        return editorsMap.get(item);
    }

    ValidationSupport getValidationSupport() {
        return validationSupport;
    }

}
