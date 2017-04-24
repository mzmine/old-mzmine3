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

package io.github.mzmine.modules.plots.spectrumparser;

import java.util.Arrays;

import io.github.msdk.datamodel.msspectra.MsSpectrumType;
import io.github.mzmine.parameters.ParameterSet;
import io.github.mzmine.parameters.ParameterValidator;
import io.github.mzmine.parameters.parametertypes.ComboParameter;
import io.github.mzmine.parameters.parametertypes.TextAreaParameter;
import javafx.stage.FileChooser.ExtensionFilter;

public class SpectrumParserPlotParameters extends ParameterSet {

  private static final ExtensionFilter filters[] = {new ExtensionFilter("All files", "*")};

  public static final TextAreaParameter spectrumText = new TextAreaParameter("Spectrum data",
      "Spectrum m/z and intensity values. Each ion on a separate line, delimited by any non-numerical characters such as spaces or tabs.",
      "Input", ParameterValidator.createNonEmptyValidator(), Arrays.asList(filters));

  public static final ComboParameter<MsSpectrumType> spectrumType =
      new ComboParameter<>("Spectrum type", "Spectrum type, centroid or profile?", "Input",
          ParameterValidator.createNonEmptyValidator(),
          Arrays.asList(new MsSpectrumType[] {MsSpectrumType.CENTROIDED, MsSpectrumType.PROFILE}),
          MsSpectrumType.CENTROIDED);

  /**
   * Create the parameter set.
   */
  public SpectrumParserPlotParameters() {
    super(spectrumText, spectrumType);
  }

}
