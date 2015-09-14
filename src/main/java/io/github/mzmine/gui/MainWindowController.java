/*
 * Copyright 2006-2015 The MZmine 3 Development Team
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
 * MZmine 2; if not, write to the Free Software Foundation, Inc., 51 Franklin
 * St, Fifth Floor, Boston, MA 02110-1301 USA
 */

package io.github.mzmine.gui;

import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.logging.Logger;

import javax.annotation.Nonnull;

import org.controlsfx.control.StatusBar;
import org.controlsfx.control.TaskProgressView;
import org.dockfx.DockNode;
import org.dockfx.DockPane;
import org.dockfx.DockPos;

import io.github.mzmine.main.MZmineCore;
import io.github.mzmine.taskcontrol.MSDKTask;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

/**
 * This class is the main window of application
 * 
 */
public class MainWindowController implements Initializable {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    private final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(
            2);

    @FXML
    private DockPane mainDockPane;
    private DockNode visualizerDock;

    @FXML
    private TreeView<RawDataTreeItem> rawDataTree;

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        rawDataTree.getSelectionModel()
                .setSelectionMode(SelectionMode.MULTIPLE);
        rawDataTree.setShowRoot(false);
        rawDataTree.setRoot(MZmineGUI.getCurrentProject().getRawDataRootItem());

        // Add mouse clicked event handler
        rawDataTree.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    // Sample Line Chart
                    NumberAxis xAxis = new NumberAxis();
                    NumberAxis yAxis = new NumberAxis();
                    xAxis.setLabel("Label X");
                    final LineChart<Number, Number> lineChart = new LineChart<Number, Number>(
                            xAxis, yAxis);
                    lineChart.setTitle("Line Chart Example 2");
                    XYChart.Series series = new XYChart.Series();
                    series.setName("Sample X");
                    series.getData().add(new XYChart.Data(1, 23));
                    series.getData().add(new XYChart.Data(2, 14));
                    series.getData().add(new XYChart.Data(3, 15));
                    series.getData().add(new XYChart.Data(4, 24));
                    series.getData().add(new XYChart.Data(5, 34));
                    series.getData().add(new XYChart.Data(6, 36));
                    series.getData().add(new XYChart.Data(7, 22));
                    series.getData().add(new XYChart.Data(8, 45));
                    series.getData().add(new XYChart.Data(9, 43));
                    series.getData().add(new XYChart.Data(10, 17));
                    series.getData().add(new XYChart.Data(11, 29));
                    series.getData().add(new XYChart.Data(12, 25));
                    lineChart.getData().addAll(series);

                    // New test dock
                    final DockNode testDock = new DockNode(lineChart,
                            "Example Line Chart 2");

                    // Add default button
                    ToggleButton DefaultButton;
                    DefaultButton = new ToggleButton();
                    DefaultButton.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            System.out.println("Selected property: "
                                    + DefaultButton.selectedProperty());
                        }
                    });
                    DefaultButton.setMinSize(16, 20);
                    DefaultButton.setMaxSize(16, 20);
                    testDock.getDockTitleBar().getChildren().add(DefaultButton);

                    testDock.dock(mainDockPane, DockPos.RIGHT);
                    // testDock.setFloating(true);
                }
            }
        });

        featureTree.getSelectionModel()
                .setSelectionMode(SelectionMode.MULTIPLE);
        TreeItem<Object> peakListsRootItem = new TreeItem<>("Peak lists");
        featureTree.setRoot(peakListsRootItem);

        // featureTree.setCellFactory(TextFieldTreeCell.forTreeView());

        /*
         * TreeItem<Object> df1 = new TreeItem<>("File 1"); df1.setGraphic(new
         * ImageView(fileIcon));
         * 
         * TreeItem<Object> group1 = new TreeItem<>("Group 1");
         * group1.setGraphic(new ImageView(groupIcon)); TreeItem<Object> df2 =
         * new TreeItem<>("File 2"); df2.setGraphic(new ImageView(fileIcon));
         * TreeItem<Object> df3 = new TreeItem<>("File 3"); df3.setGraphic(new
         * ImageView(fileIcon)); TreeItem<Object> df4 = new TreeItem<>("File 4"
         * ); df4.setGraphic(new ImageView(fileIcon)); TreeItem<Object> df5 =
         * new TreeItem<>("File 5"); df5.setGraphic(new ImageView(fileIcon));
         * TreeItem<Object> df6 = new TreeItem<>("File 6"); df6.setGraphic(new
         * ImageView(fileIcon)); group1.getChildren().addAll(df4, df5, df6);
         * rawDataTree.getRoot().getChildren().addAll(df1, group1, df2, df3);
         */

        TreeItem<Object> item1 = new TreeItem<>("Peak list 1");
        TreeItem<Object> item2 = new TreeItem<>("Peak list 2");
        TreeItem<Object> item3 = new TreeItem<>("Peak list 3");
        featureTree.getRoot().getChildren().addAll(item1, item2, item3);

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
                        if (task instanceof MSDKTask) {
                            MSDKTask msdkTask = (MSDKTask) task;
                            msdkTask.refreshStatus();
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

    public DockPane getMainDockPane() {
        return mainDockPane;
    }

    public DockNode getVisualizerDock() {
        return visualizerDock;
    }

    public void setVisualizerDock(@Nonnull DockNode visualizerDock) {
        this.visualizerDock = visualizerDock;
    }

    public TreeView<RawDataTreeItem> getRawDataTree() {
        return rawDataTree;
    }

    public TreeView<?> getFeatureTree() {
        return featureTree;
    }

    public TaskProgressView<?> getTaskTable() {
        return tasksView;
    }

    void addTask(Task<?> task) {
        tasksView.getTasks().add(task);
        executor.execute(task);
    }

    @FXML
    protected void handleShowTIC(ActionEvent event) {
        System.out.println("show tic event " + event);
        List<TreeItem<RawDataTreeItem>> selectedItems = rawDataTree
                .getSelectionModel().getSelectedItems();

    }

}
