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

import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import javax.annotation.Nonnull;

import com.google.common.collect.Range;

import io.github.msdk.datamodel.chromatograms.Chromatogram;
import io.github.msdk.datamodel.chromatograms.ChromatogramType;
import io.github.msdk.datamodel.datastore.DataPointStore;
import io.github.msdk.datamodel.datastore.DataPointStoreFactory;
import io.github.msdk.datamodel.rawdata.MsScan;
import io.github.msdk.datamodel.rawdata.RawDataFile;
import io.github.msdk.rawdata.xic.MSDKXICMethod;
import io.github.mzmine.gui.MZmineGUI;
import io.github.mzmine.modules.MZmineRunnableModule;
import io.github.mzmine.parameters.ParameterSet;
import io.github.mzmine.parameters.parametertypes.selectors.ScanSelection;
import io.github.mzmine.project.MZmineProject;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

/**
 * Chromatogram plot
 */
public class ChromatogramPlotModule implements MZmineRunnableModule {

    private static final String PLOT_FXML = "ChromatogramPlotWindow.fxml";

    private static final @Nonnull String MODULE_NAME = "TIC/XIC visualizer";
    private static final @Nonnull String MODULE_DESCRIPTION = "TIC/XIC visualizer."; // TODO

    private static final ScheduledThreadPoolExecutor threadPool = new ScheduledThreadPoolExecutor(
            4);

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
        final ChromatogramPlotType plotType = parameters
                .getParameter(ChromatogramPlotParameters.plotType).getValue();
        final Range<Double> mzRange = parameters
                .getParameter(ChromatogramPlotParameters.mzRange).getValue();

        try {
            // Load the main window
            URL mainFXML = this.getClass().getResource(PLOT_FXML);
            FXMLLoader loader = new FXMLLoader(mainFXML);

            Parent node = loader.load();
            MZmineGUI.addWindow(node, "Chromatogram", false);
            ChromatogramPlotWindowController controller = loader
                    .getController();

            for (RawDataFile dataFile : dataFiles) {

                // Load the actual data in a separate thread to avoid blocking
                // the GUI
                threadPool.execute(() -> {
                    try {
                        DataPointStore store = DataPointStoreFactory
                                .getMemoryDataStore();
                        List<MsScan> scans = scanSelection
                                .getMatchingScans(dataFile);
                        ChromatogramType chromatogramType = ChromatogramType.TIC;
                        if (plotType == ChromatogramPlotType.BASEPEAK)
                            chromatogramType = ChromatogramType.BPC;
                        MSDKXICMethod xicExtractor = new MSDKXICMethod(dataFile,
                                scans, mzRange, chromatogramType, store);
                        Chromatogram chromatogram = xicExtractor.execute();
                        controller.addChromatogram(chromatogram);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public @Nonnull Class<? extends ParameterSet> getParameterSetClass() {
        return ChromatogramPlotParameters.class;
    }

}