/*
 * Copyright 2006-2015 The MZmine 3 Development Team
 * 
 * This file is part of MZmine 3.
 * 
 * MZmine 3 is free software; you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * MZmine 3 is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with MZmine 3; if not,
 * write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301
 * USA
 */

package io.github.mzmine.parameters.parametertypes;

import javax.annotation.Nullable;

import org.controlsfx.control.PropertySheet;
import org.controlsfx.property.editor.PropertyEditor;

import io.github.mzmine.parameters.ParameterEditor;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.layout.BorderPane;

public class OptionalEditor extends BorderPane implements ParameterEditor<Boolean> {

  private final CheckBox checkBox;
  private final OptionalParameter<?> optionalParameter;
  private final AbstractParameter<?> embeddedParameter;
  private final PropertyEditor embeddedEditor;

  public OptionalEditor(PropertySheet.Item parameter) {

    if (!(parameter instanceof OptionalParameter))
      throw new IllegalArgumentException();

    optionalParameter = (OptionalParameter<?>) parameter;
    embeddedParameter = optionalParameter.getEmbeddedParameter();

    // The checkbox
    checkBox = new CheckBox();

    setLeft(checkBox);

    // Add embedded editor
    try {
      Class<? extends PropertyEditor<?>> embeddedEditorClass =
          embeddedParameter.getPropertyEditorClass().get();
      embeddedEditor = embeddedEditorClass.getDeclaredConstructor(PropertySheet.Item.class)
          .newInstance(embeddedParameter);
      Node embeddedNode = embeddedEditor.getEditor();
      Boolean value = optionalParameter.getValue();
      if (value == null)
        value = false;
      embeddedNode.setDisable(!value);
      checkBox.setOnAction(e -> {
        embeddedNode.setDisable(!checkBox.isSelected());
      });

      setCenter(embeddedNode);
    } catch (Exception e) {
      throw (new IllegalStateException(e));
    }

  }

  @Override
  public Node getEditor() {
    return this;
  }

  @Override
  public Boolean getValue() {
    embeddedParameter.setValue(embeddedEditor.getValue());
    return checkBox.isSelected();
  }

  @SuppressWarnings("unchecked")
  @Override
  public void setValue(Boolean value) {
    if (value != null) {
      checkBox.setSelected(value);
      embeddedEditor.setValue(embeddedParameter.getValue());
    }
  }

  @Override
  @Nullable
  public Control getMainControl() {
    return checkBox;
  }

}
