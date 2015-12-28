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
 * MZmine 3; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

package io.github.mzmine.modules.plots.chromatogram;

import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.collect.Range;

import io.github.msdk.datamodel.rawdata.RawDataFile;
import io.github.mzmine.gui.MZmineGUI;
import io.github.mzmine.modules.MZmineRunnableModule;
import io.github.mzmine.parameters.ParameterSet;
import io.github.mzmine.parameters.parametertypes.selectors.ScanSelection;
import io.github.mzmine.project.MZmineProject;
import javafx.concurrent.Task;

/**
 * Chromatogram plot
 */
public class ChromatogramPlotModule implements MZmineRunnableModule {

    private static final @Nonnull String MODULE_NAME = "TIC/XIC visualizer";
    private static final @Nonnull String MODULE_DESCRIPTION = "TIC/XIC visualizer."; // TODO

    @Override
    public @Nonnull String getName() {
        return MODULE_NAME;
    }

    @Override
    public @Nonnull String getDescription() {
        return MODULE_DESCRIPTION;
    }

    @Override
    public void runModule(@Nonnull MZmineProject project,
            @Nonnull ParameterSet parameters,
            @Nonnull Collection<Task<?>> tasks) {

        final List<RawDataFile> dataFiles = parameters
                .getParameter(ChromatogramPlotParameters.inputFiles).getValue()
                .getMatchingRawDataFiles();
        final ScanSelection scanSelection = parameters
                .getParameter(ChromatogramPlotParameters.scanSelection)
                .getValue();
        final Range<Double> mzRange = parameters
                .getParameter(ChromatogramPlotParameters.mzRange).getValue();

        ChromatogramPlotWindow newWindow = new ChromatogramPlotWindow();
        MZmineGUI.addWindow(newWindow, "Chromatogram");

    }

    @Override
    public @Nonnull Class<? extends ParameterSet> getParameterSetClass() {
        return ChromatogramPlotParameters.class;
    }

}