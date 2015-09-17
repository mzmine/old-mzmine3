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
 * MZmine 2; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

package io.github.mzmine.gui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

import org.controlsfx.control.StatusBar;
import org.controlsfx.control.TaskProgressView;
import org.dockfx.DockNode;
import org.dockfx.DockPane;
import org.dockfx.DockPos;

import io.github.mzmine.gui.mainwindow.FeatureTableTreeItem;
import io.github.mzmine.gui.mainwindow.MainWindowController;
import io.github.mzmine.gui.mainwindow.RawDataTreeItem;
import io.github.mzmine.main.MZmineCore;
import io.github.mzmine.project.MZmineGUIProject;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * MZmine JavaFX Application class
 */
public final class MZmineGUI extends Application {

    private static final File MENU_FILE = new File("conf/MainMenu.fxml");

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    private static MainWindowController mainWindowController;
    private static TabPane tabs = new TabPane();

    public void start(Stage stage) {

        try {
            // Load the main window
            URL mainFXML = getClass().getResource("mainwindow/MainWindow.fxml");
            FXMLLoader loader = new FXMLLoader(mainFXML);
            BorderPane rootPane = (BorderPane) loader.load();
            mainWindowController = loader.getController();
            Scene scene = new Scene(rootPane, 1000, 600, Color.WHITE);
            stage.setScene(scene);

            // Load menu
            MenuBar menu = (MenuBar) FXMLLoader.load(MENU_FILE.toURI().toURL());
            rootPane.setTop(menu);

        } catch (IOException e) {
            e.printStackTrace();
            logger.severe("Error loading MZmine GUI from FXML: " + e);
            Platform.exit();
        }

        // Configure DockFX - currently does not support FXML
        DockPane dockPane = mainWindowController.getMainDockPane();
        DockNode visualizerDock = mainWindowController.getVisualizerDock();
        TreeView<?> rawDataTree = mainWindowController.getRawDataTree();
        TreeView<?> featureTableTree = mainWindowController.getFeatureTree();
        TaskProgressView<?> tasksView = mainWindowController.getTaskTable();

        // Add raw data file and feature table trees to tabs
        Tab fileTab = new Tab("Raw Data", rawDataTree);
        fileTab.setClosable(false);
        Tab featureTab = new Tab("Feature Tables", featureTableTree);
        featureTab.setClosable(false);
        tabs.getTabs().addAll(fileTab, featureTab);
        
        
        
        DockNode tabsDock = new DockNode(tabs);
        tabsDock.setDockTitleBar(null); // Disable undocking
        tabsDock.setPrefSize(200, 400);
        tabsDock.setMinHeight(400);
        tabsDock.setVisible(true);
        tabsDock.dock(dockPane, DockPos.LEFT);

        // Sample Line Chart
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Label X");
        final LineChart<Number, Number> lineChart = new LineChart<Number, Number>(
                xAxis, yAxis);
        lineChart.setTitle("Line Chart Example");
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

        // Initial dock for visualizers
        visualizerDock = new DockNode(lineChart, "Example Line Chart");
        visualizerDock.setPrefWidth(650);
        visualizerDock.dock(dockPane, DockPos.RIGHT);
        mainWindowController.setVisualizerDock(visualizerDock);

        // Add task table
        DockNode taskDock = new DockNode(tasksView);
        taskDock.setPrefHeight(100);
        taskDock.setVisible(true);
        taskDock.setClosable(false);
        taskDock.dock(dockPane, DockPos.BOTTOM);

        stage.setTitle("MZmine " + MZmineCore.getMZmineVersion());
        stage.setMinWidth(300);
        stage.setMinHeight(300);

        // Set application icon
        final Image mzMineIcon = new Image(
                "file:icon" + File.separator + "mzmine-icon.png");
        stage.getIcons().setAll(mzMineIcon);

        stage.setOnCloseRequest(e -> {
            requestQuit();
            e.consume();
        });
        
        // Activate new GUI-supported project
        MZmineGUIProject project = new MZmineGUIProject();
        MZmineGUI.activateProject(project);

        stage.show();
        DockPane.initializeDefaultUserAgentStylesheet();
        

    }

    public static void requestQuit() {
        Dialog<ButtonType> dialog = new Dialog<>();
        // dialog.initOwner(stage);
        dialog.setTitle("Confirmation");
        dialog.setContentText("Are you sure you want to exit?");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.YES,
                ButtonType.NO);
        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                Platform.exit();
                System.exit(0);
            }
        });

    }

    public static void displayMessage(String msg) {
        Platform.runLater(() -> {
            Dialog<ButtonType> dialog = new Dialog<>();
            // dialog.initOwner(stage);
            dialog.setTitle("Message");
            dialog.setContentText(msg);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
            dialog.showAndWait();
        });
    }

    public static void setStatusBarMessage(String message) {
        Platform.runLater(() -> {
            StatusBar statusBar = mainWindowController.getStatusBar();
            statusBar.setText(message);
        });
    }

    public static MainWindowController getMainWindowController() {
        return mainWindowController;
    }

    public static void setSelectedTab(String tabName) {
        switch (tabName) {
        case "RawData":
            tabs.getSelectionModel().select(0);
            break;
        case "FeatureTable":
            tabs.getSelectionModel().select(1);
            break;
        default:
            tabs.getSelectionModel().select(0);
            break;
        }
    }
    
    public static void activateProject(MZmineGUIProject project) {
        MZmineCore.setCurrentProject(project);

        TreeView<RawDataTreeItem> rawDataTree = mainWindowController.getRawDataTree();
        rawDataTree.setRoot(project.getRawDataRootItem());
        
        TreeView<FeatureTableTreeItem> featureTree=mainWindowController.getFeatureTree();
        featureTree.setRoot(project.getFeatureTableRootItem());
        
    }
    
}
