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

package io.github.mzmine.gui;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

import org.controlsfx.control.StatusBar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.msdk.datamodel.featuretables.FeatureTable;
import io.github.msdk.datamodel.rawdata.RawDataFile;
import io.github.mzmine.gui.mainwindow.FeatureTableTreeItem;
import io.github.mzmine.gui.mainwindow.MainWindowController;
import io.github.mzmine.gui.mainwindow.RawDataTreeItem;
import io.github.mzmine.main.MZmineCore;
import io.github.mzmine.main.NewVersionCheck;
import io.github.mzmine.main.NewVersionCheck.CheckType;
import io.github.mzmine.modules.MZmineRunnableModule;
import io.github.mzmine.parameters.ParameterSet;
import io.github.mzmine.project.MZmineGUIProject;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * MZmine JavaFX Application class
 */
public final class MZmineGUI extends Application {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final Image mzMineIcon = new Image(
            "file:icon/mzmine-icon.png");
    private static final String mzMineFXML = "file:conf/MainWindow.fxml";

    private static MainWindowController mainWindowController;

    private static Scene rootScene;

    public void start(Stage stage) {

        try {
            // Load the main window
            URL mainFXML = new URL(mzMineFXML);
            FXMLLoader loader = new FXMLLoader(mainFXML);

            rootScene = loader.load();
            mainWindowController = loader.getController();
            stage.setScene(rootScene);

        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Error loading MZmine GUI from FXML: " + e);
            Platform.exit();
        }

        stage.setTitle("MZmine " + MZmineCore.getMZmineVersion());
        stage.setMinWidth(300);
        stage.setMinHeight(300);

        // Set application icon
        stage.getIcons().setAll(mzMineIcon);

        stage.setOnCloseRequest(e -> {
            requestQuit();
            e.consume();
        });

        // Activate new GUI-supported project
        MZmineGUIProject project = new MZmineGUIProject();
        MZmineGUI.activateProject(project);

        stage.show();

        // Check for new version of MZmine
        NewVersionCheck NVC = new NewVersionCheck(CheckType.DESKTOP);
        Thread nvcThread = new Thread(NVC);
        nvcThread.setPriority(Thread.MIN_PRIORITY);
        nvcThread.start();
    }

    public static void requestQuit() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(mzMineIcon);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Exit MZmine");
        String s = "Are you sure you want to exit?";
        alert.setContentText(s);
        Optional<ButtonType> result = alert.showAndWait();

        if ((result.isPresent()) && (result.get() == ButtonType.OK)) {
            Platform.exit();
            System.exit(0);
        }
    }

    public static void closeProject() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(mzMineIcon);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Close project");
        String s = "Are you sure you want to close the current project?";
        alert.setContentText(s);
        Optional<ButtonType> result = alert.showAndWait();

        if ((result.isPresent()) && (result.get() == ButtonType.OK)) {
            MZmineGUIProject newProject = new MZmineGUIProject();
            activateProject(newProject);
            setStatusBarMessage("");
        }
    }

    public static void displayMessage(String msg) {
        Platform.runLater(() -> {
            Dialog<ButtonType> dialog = new Dialog<>();
            Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
            stage.getIcons().add(mzMineIcon);
            dialog.setTitle("Warning");
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
        mainWindowController.setSelectedTab(tabName);
    }

    public static void addWindow(Node node, String title,
            boolean openNewWindow) {
        if (openNewWindow) {

            BorderPane parent = new BorderPane();
            parent.setCenter(node);
            Scene newScene = new Scene(parent);

            // Copy CSS styles
            newScene.getStylesheets().addAll(rootScene.getStylesheets());

            Stage newStage = new Stage();
            newStage.setTitle(title);
            newStage.getIcons().add(mzMineIcon);
            newStage.setScene(newScene);
            newStage.show();

        } else {
            mainWindowController.addWindow(node, title);
        }
    }

    public static void activateProject(MZmineGUIProject project) {
        MZmineCore.setCurrentProject(project);

        TreeView<RawDataTreeItem> rawDataTree = mainWindowController
                .getRawDataTree();
        rawDataTree.setRoot(project.getRawDataRootItem());

        TreeView<FeatureTableTreeItem> featureTree = mainWindowController
                .getFeatureTree();
        featureTree.setRoot(project.getFeatureTableRootItem());

    }

    public static @Nonnull List<RawDataFile> getSelectedRawDataFiles() {

        final ArrayList<RawDataFile> list = new ArrayList<>();
        final TreeView<RawDataTreeItem> rawDataTree = mainWindowController
                .getRawDataTree();
        for (TreeItem<RawDataTreeItem> item : rawDataTree.getSelectionModel()
                .getSelectedItems()) {
            if (item == null)
                continue;
            RawDataTreeItem ritem = item.getValue();
            if (ritem == null)
                continue;
            RawDataFile file = ritem.getRawDataFile();
            list.add(file);
        }

        return list;

    }

    public static @Nonnull List<FeatureTable> getSelectedFeatureTables() {

        final ArrayList<FeatureTable> list = new ArrayList<>();
        final TreeView<FeatureTableTreeItem> featureTableTree = mainWindowController
                .getFeatureTree();
        for (TreeItem<FeatureTableTreeItem> item : featureTableTree
                .getSelectionModel().getSelectedItems()) {
            FeatureTable ft = item.getValue().getFeatureTable();
            list.add(ft);
        }

        return list;

    }

    public static <ModuleType extends MZmineRunnableModule> void setupAndRunModule(
            @Nonnull Class<ModuleType> moduleClass) {

        final ParameterSet moduleParameters = MZmineCore.getConfiguration()
                .getModuleParameters(moduleClass);
        ButtonType result = moduleParameters.showSetupDialog(null);
        if (result == ButtonType.OK) {
            MZmineCore.runModule(moduleClass, moduleParameters);
        }

    }

}
