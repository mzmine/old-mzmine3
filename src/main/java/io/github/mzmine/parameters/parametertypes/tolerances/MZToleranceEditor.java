/*
 * Copyright 2006-2016 The MZmine 3 Development Team
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

package io.github.mzmine.parameters.parametertypes.tolerances;

import org.controlsfx.control.PropertySheet;

import io.github.msdk.util.tolerances.MaximumMzTolerance;
import io.github.mzmine.parameters.ParameterEditor;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;

/**
 * Parameter editor for RT tolerances
 */
public class MZToleranceEditor extends FlowPane implements ParameterEditor<MaximumMzTolerance> {

  private final TextField fieldMZ;
  private final TextField fieldPPM;

  public MZToleranceEditor(PropertySheet.Item parameter) {
    if (!(parameter instanceof MZToleranceParameter))
      throw new IllegalArgumentException();

    // The m/z value field
    fieldMZ = new TextField();

    // The m/z label
    Label mzLabel = new Label("m/z");

    // The 'or' label
    Label orLabel = new Label(" or ");

    // The ppm value field
    fieldPPM = new TextField();

    // The m/z label
    Label ppmLabel = new Label("ppm");

    // FlowPane setting
    setHgap(7);

    // Add the elements
    getChildren().addAll(fieldMZ, mzLabel, orLabel, fieldPPM, ppmLabel);
  }

  @Override
  public Node getEditor() {
    return this;
  }

  @Override
  public MaximumMzTolerance getValue() {
    String stringMZ = fieldMZ.getText();
    String stringPPM = fieldPPM.getText();
    try {
      double doubleMZ = Double.parseDouble(stringMZ);
      double doublePPM = Double.parseDouble(stringPPM);
      return new MaximumMzTolerance(doubleMZ, doublePPM);
    } catch (NumberFormatException e) {
      return null;
    }
  }

  @Override
  public void setValue(MaximumMzTolerance value) {
    String stringMZ = String.valueOf(value.getMzTolerance());
    String stringPPM = String.valueOf(value.getPpmTolerance());
    fieldMZ.setText(stringMZ);
    fieldPPM.setText(stringPPM);
  }

  @Override
  public Control getMainControl() {
    return fieldMZ;
  }

}
