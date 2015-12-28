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

import javafx.collections.ObservableList;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.util.StringConverter;

public class SpinnerAutoCommit<T> extends Spinner<T> {

    public SpinnerAutoCommit() {
        super();
        addListenerKeyChange();
    }

    public SpinnerAutoCommit(int min, int max, int initialValue) {
        super(min, max, initialValue);
        addListenerKeyChange();
    }

    public SpinnerAutoCommit(int min, int max, int initialValue,
            int amountToStepBy) {
        super(min, max, initialValue, amountToStepBy);
        addListenerKeyChange();
    }

    public SpinnerAutoCommit(double min, double max, double initialValue) {
        super(min, max, initialValue);
        addListenerKeyChange();
    }

    public SpinnerAutoCommit(double min, double max, double initialValue,
            double amountToStepBy) {
        super(min, max, initialValue, amountToStepBy);
        addListenerKeyChange();
    }

    public SpinnerAutoCommit(ObservableList<T> items) {
        super(items);
        addListenerKeyChange();
    }

    public SpinnerAutoCommit(SpinnerValueFactory<T> valueFactory) {
        super(valueFactory);
        addListenerKeyChange();
    }

    private void addListenerKeyChange() {
        getEditor().textProperty()
                .addListener((observable, oldValue, newValue) -> {
                    commitEditorText();
                });
    }

    private void commitEditorText() {
        if (!isEditable())
            return;
        String text = getEditor().getText();
        SpinnerValueFactory<T> valueFactory = getValueFactory();
        if (valueFactory != null) {
            StringConverter<T> converter = valueFactory.getConverter();
            if (converter != null) {
                T value = converter.fromString(text);
                valueFactory.setValue(value);
            }
        }
    }
}
