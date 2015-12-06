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

package io.github.mzmine.modules.plots.twod;

import io.github.mzmine.parameters.ParameterSet;
import io.github.mzmine.parameters.parametertypes.IntegerParameter;
import io.github.mzmine.parameters.parametertypes.ranges.DoubleRangeParameter;
import io.github.mzmine.parameters.parametertypes.selectors.RawDataFilesParameter;
import io.github.mzmine.parameters.parametertypes.selectors.ScanSelection;
import io.github.mzmine.parameters.parametertypes.selectors.ScanSelectionParameter;

public class TwoDPlotParameters extends ParameterSet {

    public static final RawDataFilesParameter inputFiles = new RawDataFilesParameter();

    public static final ScanSelectionParameter scanSelection = new ScanSelectionParameter(
            new ScanSelection(1));

    public static final DoubleRangeParameter mzRange = new DoubleRangeParameter(
            "m/z range", "m/z range", "Data range");

    public static final IntegerParameter rtResolution = new IntegerParameter(
            "Retention time resolution",
            "Number of data points on retention time axis", "Resolution", 500);

    public static final IntegerParameter mzResolution = new IntegerParameter(
            "m/z resolution", "Number of data points on m/z axis", "Resolution",
            500);

    /**
     * Create the parameter set.
     */
    public TwoDPlotParameters() {
        super(inputFiles, scanSelection, mzRange, rtResolution, mzResolution);
    }

}
