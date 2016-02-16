/*
 * Copyright 2006-2016 The MZmine 3 Development Team
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

package io.github.mzmine.parameters;

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ParameterValidator<ValueType> {

    /**
     * If value is not valid, add a message to the collection and return false.
     */
    @Nonnull
    Boolean checkValue(@Nullable ValueType value,
            @Nonnull Collection<String> messages);

    /**
     * Simple validator that checks whether value is present
     */
    static <T> ParameterValidator<T> createNonEmptyValidator() {
        return (value, messages) -> {
            if (value == null) {
                messages.add("Value is not set");
                return false;
            }
            if (value instanceof String) {
                String strValue = (String) value;
                if (strValue.trim().length() == 0) {
                    messages.add("Value is empty");
                    return false;
                }
            }
            if (value instanceof Collection) {
                Collection<?> collectionValue = (Collection<?>) value;
                if (collectionValue.size() == 0) {
                    messages.add("Value is empty");
                    return false;
                }
            }
            return true;
        };
    };

    /**
     * Simple validator that checks the value against a regular expression
     */
    static <T> ParameterValidator<T> createRegexValidator(String regex) {
        return (value, messages) -> {
            if (value == null) {
                messages.add("Value is not set");
                return false;
            }
            String strValue = value.toString();
            if (strValue.matches(regex)) {
                return true;
            } else {
                messages.add("Invalid format");
                return false;
            }
        };
    };

}
