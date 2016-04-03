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

package io.github.mzmine.modules.plots.msspectrum;

import java.net.URL;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.base.Preconditions;
import com.google.common.collect.Range;

import io.github.msdk.datamodel.msspectra.MsSpectrum;
import io.github.msdk.datamodel.rawdata.MsScan;
import io.github.msdk.datamodel.rawdata.RawDataFile;
import io.github.mzmine.gui.MZmineGUI;
import io.github.mzmine.modules.MZmineRunnableModule;
import io.github.mzmine.parameters.ParameterSet;
import io.github.mzmine.parameters.parametertypes.selectors.RawDataFilesSelection;
import io.github.mzmine.parameters.parametertypes.selectors.ScanSelection;
import io.github.mzmine.project.MZmineProject;
import io.github.mzmine.util.MsScanUtils;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

/**
 * MS spectrum plot
 */
public class MsSpectrumPlotModule implements MZmineRunnableModule {

    private static final String PLOT_FXML = "MsSpectrumPlotWindow.fxml";

    private static final @Nonnull String MODULE_NAME = "MS spectrum plot";
    private static final @Nonnull String MODULE_DESCRIPTION = "MS spectrum plot";

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

        final RawDataFilesSelection fileSelection = parameters
                .getParameter(MsSpectrumPlotParameters.inputFiles).getValue();

        final Integer scanNumber = parameters
                .getParameter(MsSpectrumPlotParameters.scanNumber).getValue();

        Preconditions.checkNotNull(fileSelection);
        Preconditions.checkNotNull(scanNumber);

        final ScanSelection scanSelection = new ScanSelection(
                Range.singleton(scanNumber), null, null, null, null, null);

        final List<RawDataFile> dataFiles = fileSelection
                .getMatchingRawDataFiles();

        // Add the window to the desktop only if we actually have any raw
        // data to show.
        boolean weHaveData = false;
        for (RawDataFile dataFile : dataFiles) {
            if (!scanSelection.getMatchingScans(dataFile).isEmpty()) {
                weHaveData = true;
                break;
            }
        }
        if (!weHaveData) {
            MZmineGUI.displayMessage("Scan not found");
            return;
        }

        try {
            // Load the main window
            URL mainFXML = this.getClass().getResource(PLOT_FXML);
            FXMLLoader loader = new FXMLLoader(mainFXML);

            Parent node = loader.load();
            MZmineGUI.addWindow(node, "MS spectrum", false);
            MsSpectrumPlotWindowController controller = loader.getController();

            Platform.runLater(() -> {
                for (RawDataFile dataFile : dataFiles) {
                    for (MsScan scan : scanSelection
                            .getMatchingScans(dataFile)) {
                        String title = MsScanUtils
                                .createSingleLineMsScanDescription(scan);
                        controller.addSpectrum(scan, title);
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public @Nonnull Class<? extends ParameterSet> getParameterSetClass() {
        return MsSpectrumPlotParameters.class;
    }

    public static void showNewSpectrumWindow(@Nonnull MsScan scan,
            @Nonnull Boolean openNewWindow) {
        try {
            URL mainFXML = MsSpectrumPlotModule.class.getResource(PLOT_FXML);
            FXMLLoader loader = new FXMLLoader(mainFXML);
            Parent node = loader.load();
            String title = MsScanUtils.createSingleLineMsScanDescription(scan);
            MZmineGUI.addWindow(node, title, openNewWindow);
            MsSpectrumPlotWindowController controller = loader.getController();
            controller.addSpectrum(scan, title);

        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    public static void showNewSpectrumWindow(@Nonnull MsSpectrum spectrum,
            @Nonnull String name, @Nonnull Boolean openNewWindow) {
        try {
            URL mainFXML = MsSpectrumPlotModule.class.getResource(PLOT_FXML);
            FXMLLoader loader = new FXMLLoader(mainFXML);

            Parent node = loader.load();
            MZmineGUI.addWindow(node, name, openNewWindow);
            MsSpectrumPlotWindowController controller = loader.getController();
            controller.addSpectrum(spectrum, name);

        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

}