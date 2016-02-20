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
 * MZmine 3; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

package io.github.mzmine.modules.plots.chromatogram;

import java.util.Arrays;

import io.github.mzmine.parameters.ParameterSet;
import io.github.mzmine.parameters.parametertypes.ComboParameter;
import io.github.mzmine.parameters.parametertypes.ranges.MZRangeParameter;
import io.github.mzmine.parameters.parametertypes.selectors.RawDataFilesParameter;
import io.github.mzmine.parameters.parametertypes.selectors.ScanSelection;
import io.github.mzmine.parameters.parametertypes.selectors.ScanSelectionParameter;

public class ChromatogramPlotParameters extends ParameterSet {

    public static final RawDataFilesParameter inputFiles = new RawDataFilesParameter();

    public static final ScanSelectionParameter scanSelection = new ScanSelectionParameter(
            new ScanSelection(1));

    public static final ComboParameter<ChromatogramPlotType> plotType = new ComboParameter<>(
            "Plot type",
            "Type of Y value calculation (TIC = sum, base peak = max)",
            "Category", Arrays.asList(ChromatogramPlotType.values()),
            ChromatogramPlotType.BASEPEAK);

    public static final MZRangeParameter mzRange = new MZRangeParameter();

    /**
     * Create the parameter set.
     */
    public ChromatogramPlotParameters() {
        super(inputFiles, scanSelection, plotType, mzRange);
    }

}
