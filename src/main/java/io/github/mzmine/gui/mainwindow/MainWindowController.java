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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.controlsfx.control.HiddenSidesPane;
import org.controlsfx.control.StatusBar;
import org.controlsfx.control.TaskProgressView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import io.github.mzmine.project.MZmineProject;
import io.github.mzmine.taskcontrol.MZmineTask;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * This class controls the main window of the application
 * 
 */
public class MainWindowController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final Image mzMineIcon = new Image(
            "file:icon/mzmine-icon.png");

    @FXML
    private Scene mainScene;
    
    @FXML
    private BorderPane mainWindowPane;

    @FXML
    private HiddenSidesPane mainContentPane;

    @FXML
    private TabPane mainTabPane;

    @FXML
    private TreeView<RawDataTreeItem> rawDataTree;

    @FXML
    private TreeView<FeatureTableTreeItem> featureTree;

    @FXML
    private TaskProgressView<Task<?>> tasksView;

    @FXML
    private StatusBar statusBar;

    @FXML
    private ProgressBar memoryBar;

    @FXML
    private Label memoryBarLabel;

    @FXML
    private Button detachButton;

    private String currentNodeTitle;

    @FXML
    public void initialize() {

        rawDataTree.getSelectionModel()
                .setSelectionMode(SelectionMode.MULTIPLE);
        rawDataTree.setShowRoot(false);

        // Add mouse clicked event handler
        rawDataTree.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    handleShowTIC(null);
                }
            }
        });

        featureTree.getSelectionModel()
                .setSelectionMode(SelectionMode.MULTIPLE);
        featureTree.setShowRoot(false);

        // Add mouse clicked event handler
        featureTree.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    // Show feature table for selected row
                    ParameterSet moduleParameters = MZmineCore
                            .getConfiguration()
                            .getModuleParameters(FeatureTableModule.class);
                    FeatureTablesParameter inputTablesParam = moduleParameters
                            .getParameter(
                                    FeatureTableModuleParameters.featureTables);
                    inputTablesParam.switchType(
                            FeatureTablesSelectionType.GUI_SELECTED_FEATURE_TABLES);
                    FeatureTableModule moduleInstance = MZmineCore
                            .getModuleInstance(FeatureTableModule.class);
                    MZmineProject currentProject = MZmineCore
                            .getCurrentProject();
                    List<Task<?>> newTasks = new ArrayList<>();
                    moduleInstance.runModule(currentProject, moduleParameters,
                            newTasks);
                    MZmineCore.submitTasks(newTasks);
                }
            }
        });

        statusBar.setText("Welcome to MZmine " + MZmineCore.getMZmineVersion());

        // Setup the Timeline to update the memory indicator periodically
        final Timeline memoryUpdater = new Timeline();
        int UPDATE_FREQUENCY = 500; // ms
        memoryUpdater.setCycleCount(Animation.INDEFINITE);
        memoryUpdater.getKeyFrames().add(new KeyFrame(
                Duration.millis(UPDATE_FREQUENCY), (ActionEvent e) -> {

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

        // Setup the Timeline to update the MSDK tasks periodically
        final Timeline msdkTaskUpdater = new Timeline();
        UPDATE_FREQUENCY = 50; // ms
        msdkTaskUpdater.setCycleCount(Animation.INDEFINITE);
        msdkTaskUpdater.getKeyFrames().add(new KeyFrame(
                Duration.millis(UPDATE_FREQUENCY), (ActionEvent e) -> {

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

    public TreeView<RawDataTreeItem> getRawDataTree() {
        return rawDataTree;
    }

    public TreeView<FeatureTableTreeItem> getFeatureTree() {
        return featureTree;
    }

    public TaskProgressView<Task<?>> getTaskTable() {
        return tasksView;
    }

    public StatusBar getStatusBar() {
        return statusBar;
    }

    @FXML
    protected void handleShowTIC(ActionEvent event) {
        logger.debug("Activated Show chromatogram menu item");
        ParameterSet chromPlotParams = MZmineCore.getConfiguration()
                .getModuleParameters(ChromatogramPlotModule.class);
        RawDataFilesParameter inputFilesParam = chromPlotParams
                .getParameter(ChromatogramPlotParameters.inputFiles);
        inputFilesParam
                .switchType(RawDataFilesSelectionType.GUI_SELECTED_FILES);
        MZmineGUI.setupAndRunModule(ChromatogramPlotModule.class);
    }

    @FXML
    protected void handleShowMsSpectrum(ActionEvent event) {
        logger.debug("Activated Show MS spectrum menu item");
        ParameterSet specPlotParams = MZmineCore.getConfiguration()
                .getModuleParameters(MsSpectrumPlotModule.class);
        RawDataFilesParameter inputFilesParam = specPlotParams
                .getParameter(MsSpectrumPlotParameters.inputFiles);
        inputFilesParam
                .switchType(RawDataFilesSelectionType.GUI_SELECTED_FILES);
        MZmineGUI.setupAndRunModule(MsSpectrumPlotModule.class);
    }

    @FXML
    protected void removeRawData(ActionEvent event) {
        // Get selected tree items
        ObservableList<TreeItem<RawDataTreeItem>> rows = null;
        if (rawDataTree.getSelectionModel() != null) {
            rows = rawDataTree.getSelectionModel().getSelectedItems();
        }

        // Loop through all selected tree items
        if (rows != null) {
            for (int i = rows.size() - 1; i >= 0; i--) {
                TreeItem<RawDataTreeItem> row = rows.get(i);

                // Remove feature table from current project
                RawDataTreeItem rawDataTreeItem = row.getValue();
                MZmineCore.getCurrentProject()
                        .removeFile(rawDataTreeItem.getRawDataFile());

                // Remove raw data file from tree table view
                TreeItem<?> parent = row.getParent();
                parent.getChildren().remove(row);
            }
            rawDataTree.getSelectionModel().clearSelection();
        }
    }

    @FXML
    protected void removeFeatureTable(ActionEvent event) {
        // Get selected tree items
        ObservableList<TreeItem<FeatureTableTreeItem>> rows = null;
        if (featureTree.getSelectionModel() != null) {
            rows = featureTree.getSelectionModel().getSelectedItems();
        }

        // Loop through all selected tree items
        if (rows != null) {
            for (int i = rows.size() - 1; i >= 0; i--) {
                TreeItem<FeatureTableTreeItem> row = rows.get(i);

                // Remove feature table from current project
                FeatureTableTreeItem featureTableTreeItem = row.getValue();
                MZmineCore.getCurrentProject().removeFeatureTable(
                        featureTableTreeItem.getFeatureTable());

                // Remove feature table from tree table view
                TreeItem<?> parent = row.getParent();
                parent.getChildren().remove(row);
            }
            featureTree.getSelectionModel().clearSelection();
        }
    }

    @FXML
    protected void detachCurrentNode(ActionEvent event) {

        Node currentNode = mainContentPane.getContent();
        if (currentNode == null)
            return;

        mainContentPane.setContent(new Pane());

        BorderPane parent = new BorderPane();
        parent.setCenter(currentNode);
        Scene newScene = new Scene(parent);

        // Copy CSS styles
        newScene.getStylesheets().addAll(mainScene.getStylesheets());

        Stage newStage = new Stage();
        newStage.setTitle(currentNodeTitle);
        newStage.getIcons().add(mzMineIcon);
        newStage.setScene(newScene);
        newStage.show();

        detachButton.setDisable(true);

    }

    public void addWindow(Node node, String title) {
        mainContentPane.setContent(node);
        this.currentNodeTitle = title;
        detachButton.setDisable(false);
    }

    public void setSelectedTab(String tabName) {
        switch (tabName) {
        default:
        case "RawData":
            mainTabPane.getSelectionModel().select(0);
            break;
        case "FeatureTable":
            mainTabPane.getSelectionModel().select(1);
            break;
        }
    }

}
