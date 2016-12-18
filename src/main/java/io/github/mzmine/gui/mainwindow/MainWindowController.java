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
 * MZmine 3; if not, write to the Free Software Foundation, Inc., 51 Franklin
 * St, Fifth Floor, Boston, MA 02110-1301 USA
 */

package io.github.mzmine.gui.mainwindow;

import java.util.Collection;

import org.controlsfx.control.StatusBar;
import org.controlsfx.control.TaskProgressView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.msdk.datamodel.featuretables.FeatureTable;
import io.github.msdk.datamodel.rawdata.RawDataFile;
import io.github.mzmine.gui.MZmineGUI;
import io.github.mzmine.main.MZmineCore;
import io.github.mzmine.modules.featuretable.FeatureTableModule;
import io.github.mzmine.modules.featuretable.FeatureTableModuleParameters;
import io.github.mzmine.modules.plots.chromatogram.ChromatogramPlotModule;
import io.github.mzmine.modules.plots.chromatogram.ChromatogramPlotParameters;
import io.github.mzmine.modules.plots.msspectrum.MsSpectrumPlotModule;
import io.github.mzmine.modules.plots.msspectrum.MsSpectrumPlotParameters;
import io.github.mzmine.parameters.ParameterSet;
import io.github.mzmine.parameters.parametertypes.selectors.FeatureTablesParameter;
import io.github.mzmine.parameters.parametertypes.selectors.FeatureTablesSelectionType;
import io.github.mzmine.parameters.parametertypes.selectors.RawDataFilesParameter;
import io.github.mzmine.parameters.parametertypes.selectors.RawDataFilesSelectionType;
import io.github.mzmine.taskcontrol.MZmineTask;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;

/**
 * This class controls the main window of the application
 * 
 */
public class MainWindowController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @FXML
    private Scene mainScene;

    @FXML
    private BorderPane mainWindowPane;

    @FXML
    private TreeView<Object> rawDataTree;

    @FXML
    private TreeView<Object> featureTree;

    @FXML
    private TaskProgressView<Task<?>> tasksView;

    @FXML
    private StatusBar statusBar;

    @FXML
    private ProgressBar memoryBar;

    @FXML
    private Label memoryBarLabel;

    @FXML
    public void initialize() {

        rawDataTree.getSelectionModel()
                .setSelectionMode(SelectionMode.MULTIPLE);
        rawDataTree.setShowRoot(true);

        // Add mouse clicked event handler
        rawDataTree.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                handleShowTIC(null);
            }
        });

        featureTree.getSelectionModel()
                .setSelectionMode(SelectionMode.MULTIPLE);
        featureTree.setShowRoot(true);

        // Add mouse clicked event handler
        featureTree.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                // Show feature table for selected row
                ParameterSet moduleParameters = MZmineCore.getConfiguration()
                        .getModuleParameters(FeatureTableModule.class);
                FeatureTablesParameter inputTablesParam = moduleParameters
                        .getParameter(
                                FeatureTableModuleParameters.featureTables);
                inputTablesParam.switchType(
                        FeatureTablesSelectionType.GUI_SELECTED_FEATURE_TABLES);
                MZmineCore.runMZmineModule(FeatureTableModule.class,
                        moduleParameters);
            }
        });

        statusBar.setText("Welcome to MZmine " + MZmineCore.getMZmineVersion());

        /*
         * tasksView.setGraphicFactory(task -> { return new Glyph("FontAwesome",
         * FontAwesome.Glyph.COG).size(24.0) .color(Color.BLUE); });
         */

        // Setup the Timeline to update the memory indicator periodically
        final Timeline memoryUpdater = new Timeline();
        int UPDATE_FREQUENCY = 500; // ms
        memoryUpdater.setCycleCount(Animation.INDEFINITE);
        memoryUpdater.getKeyFrames()
                .add(new KeyFrame(Duration.millis(UPDATE_FREQUENCY), e -> {

                    final long freeMemMB = Runtime.getRuntime().freeMemory()
                            / (1024 * 1024);
                    final long totalMemMB = Runtime.getRuntime().totalMemory()
                            / (1024 * 1024);
                    final double memory = ((double) (totalMemMB - freeMemMB))
                            / totalMemMB;

                    memoryBar.setProgress(memory);
                    memoryBarLabel
                            .setText(freeMemMB + "/" + totalMemMB + " MB free");
                }));
        memoryUpdater.play();

        // Setup the Timeline to update the MZmine tasks periodically
        final Timeline msdkTaskUpdater = new Timeline();
        UPDATE_FREQUENCY = 50; // ms
        msdkTaskUpdater.setCycleCount(Animation.INDEFINITE);
        msdkTaskUpdater.getKeyFrames()
                .add(new KeyFrame(Duration.millis(UPDATE_FREQUENCY), e -> {

                    Collection<Task<?>> tasks = tasksView.getTasks();
                    for (Task<?> task : tasks) {
                        if (task instanceof MZmineTask) {
                            MZmineTask mzmineTask = (MZmineTask) task;
                            mzmineTask.refreshStatus();
                        }
                    }
                }));
        msdkTaskUpdater.play();
    }

    @FXML
    public void memoryBarClicked(MouseEvent e) {
        // Run garbage collector on a new thread, so it does not block the GUI
        new Thread(() -> {
            logger.info("Running garbage collector");
            System.gc();
        }).start();

    }

    public TreeView<Object> getRawDataTree() {
        return rawDataTree;
    }

    public TreeView<Object> getFeatureTree() {
        return featureTree;
    }

    public TaskProgressView<Task<?>> getTaskTable() {
        return tasksView;
    }

    public StatusBar getStatusBar() {
        return statusBar;
    }

    public void handleShowTIC(ActionEvent event) {
        logger.debug("Activated Show chromatogram menu item");
        ParameterSet chromPlotParams = MZmineCore.getConfiguration()
                .getModuleParameters(ChromatogramPlotModule.class);
        RawDataFilesParameter inputFilesParam = chromPlotParams
                .getParameter(ChromatogramPlotParameters.inputFiles);
        inputFilesParam
                .switchType(RawDataFilesSelectionType.GUI_SELECTED_FILES);
        MZmineGUI.setupAndRunModule(ChromatogramPlotModule.class);
    }

    public void handleShowMsSpectrum(ActionEvent event) {
        logger.debug("Activated Show MS spectrum menu item");
        ParameterSet specPlotParams = MZmineCore.getConfiguration()
                .getModuleParameters(MsSpectrumPlotModule.class);
        RawDataFilesParameter inputFilesParam = specPlotParams
                .getParameter(MsSpectrumPlotParameters.inputFiles);
        inputFilesParam
                .switchType(RawDataFilesSelectionType.GUI_SELECTED_FILES);
        MZmineGUI.setupAndRunModule(MsSpectrumPlotModule.class);
    }

    public void removeRawData(ActionEvent event) {
        // Get selected tree items
        ObservableList<TreeItem<Object>> rows = null;
        if (rawDataTree.getSelectionModel() != null) {
            rows = rawDataTree.getSelectionModel().getSelectedItems();
        }

        // Loop through all selected tree items
        if (rows != null) {
            for (int i = rows.size() - 1; i >= 0; i--) {
                TreeItem<Object> row = rows.get(i);

                if (!(row.getValue() instanceof RawDataFile))
                    continue;

                // Remove feature table from current project
                RawDataFile rawDataFile = (RawDataFile) row.getValue();
                MZmineCore.getCurrentProject().removeFile(rawDataFile);

                // Remove raw data file from tree table view
                TreeItem<?> parent = row.getParent();
                if (parent == null)
                    continue;
                parent.getChildren().remove(row);
            }
            rawDataTree.getSelectionModel().clearSelection();
        }
    }

    public void removeFeatureTable(ActionEvent event) {
        // Get selected tree items
        ObservableList<TreeItem<Object>> rows = null;
        if (featureTree.getSelectionModel() != null) {
            rows = featureTree.getSelectionModel().getSelectedItems();
        }

        // Loop through all selected tree items
        if (rows != null) {
            for (int i = rows.size() - 1; i >= 0; i--) {
                TreeItem<Object> row = rows.get(i);

                if (!(row.getValue() instanceof FeatureTable))
                    continue;

                // Remove feature table from current project
                FeatureTable featureTable = (FeatureTable) row.getValue();
                MZmineCore.getCurrentProject().removeFeatureTable(featureTable);

                // Remove feature table from tree table view
                TreeItem<?> parent = row.getParent();
                if (parent == null)
                    continue;
                parent.getChildren().remove(row);
            }
            featureTree.getSelectionModel().clearSelection();
        }
    }

    public void updateTabName(Tab tab) {
        /*
         * String title = ""; if (tab.equals(rawDataFilesTab)) { title =
         * "Raw Data"; int rawDataFiles =
         * MZmineCore.getCurrentProject().getRawDataFiles() .size(); if
         * (rawDataFiles > 0) title += " (" + rawDataFiles + ")";
         * rawDataFilesTab.setText(title); return; } if
         * (tab.equals(featureTablesTab)) { title = "Feature Tables"; int
         * featureTables = MZmineCore.getCurrentProject()
         * .getFeatureTables().size(); if (featureTables > 0) title += " (" +
         * featureTables + ")"; featureTablesTab.setText(title); return; }
         */
    }
}
