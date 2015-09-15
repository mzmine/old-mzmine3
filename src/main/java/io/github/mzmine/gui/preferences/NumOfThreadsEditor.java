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
 * MZmine 2; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

package io.github.mzmine.gui.preferences;

import org.controlsfx.control.PropertySheet;
import org.controlsfx.property.editor.PropertyEditor;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

public class NumOfThreadsEditor extends BorderPane
        implements PropertyEditor<Integer> {

    private final NumOfThreadsParameter numThrParameter;

    private static final ObservableList<String> options = FXCollections
            .observableArrayList(
                    "Set to the number of CPU cores ("
                            + Runtime.getRuntime().availableProcessors() + ")",
                    "Set manually");

    private ComboBox<String> optionCombo;
    private TextField numField;

    public NumOfThreadsEditor(PropertySheet.Item parameter) {

        this.numThrParameter = (NumOfThreadsParameter) parameter;

        optionCombo = new ComboBox<>(options);
        setLeft(optionCombo);

        numField = new TextField();
        setCenter(numField);

    }

    @Override
    public Node getEditor() {
        return this;
    }

    @Override
    public Integer getValue() {
        if (numThrParameter.isAutomatic())
            return Runtime.getRuntime().availableProcessors();
        else
            return Integer.parseInt(numField.getText());
    }

    @Override
    public void setValue(Integer value) {
        if (value != null)
            numField.setText(String.valueOf(value));

    }

}
