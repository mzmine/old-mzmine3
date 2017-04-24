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

package io.github.mzmine.modules.plots.isotopepattern;

import io.github.mzmine.main.MZmineCore;
import io.github.mzmine.parameters.ParameterSet;
import io.github.mzmine.parameters.ParameterValidator;
import io.github.mzmine.parameters.parametertypes.ChemicalFormulaParameter;
import io.github.mzmine.parameters.parametertypes.DoubleParameter;
import io.github.mzmine.parameters.parametertypes.PercentParameter;

public class IsotopePatternPlotParameters extends ParameterSet {

  public static final ChemicalFormulaParameter formula = new ChemicalFormulaParameter();

  public static final DoubleParameter mzTolerance = new DoubleParameter("m/z tolerance",
      "If the m/z difference between the isotopes is smaller than m/z tolerance, their intensity is summed and a new m/z value is calculated as a weighted average.",
      "Input", MZmineCore.getConfiguration().getMZFormat(),
      ParameterValidator.createNonEmptyValidator(), 0.0001);

  public static final PercentParameter minAbundance =
      new PercentParameter("Minimum abundance", "Minimum abundance of the predicted isotopes",
          "Input", ParameterValidator.createNonEmptyValidator(), 0.001);

  public static final DoubleParameter normalizedIntensity = new DoubleParameter(
      "Normalize intensity", "The intensity value the highest isotope will be normalized to",
      "Input", MZmineCore.getConfiguration().getIntensityFormat(),
      ParameterValidator.createNonEmptyValidator(), 100.0);

  /**
   * Create the parameter set.
   */
  public IsotopePatternPlotParameters() {
    super(formula, mzTolerance, minAbundance, normalizedIntensity);
  }

}
