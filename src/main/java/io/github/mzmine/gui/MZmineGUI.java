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
import java.util.Collection;
import java.util.logging.Logger;

import io.github.mzmine.datamodel.MZmineProject;
import io.github.mzmine.datamodel.impl.MZmineObjectBuilder;
import io.github.mzmine.main.MZmineCore;
import io.github.mzmine.taskcontrol.Task;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.MenuBar;
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
    private static MZmineProject currentProject;

    public void start(Stage stage) {

        // Create an empty project
        currentProject = MZmineObjectBuilder.getMZmineProject();

        try {
            // Load the main window
            URL mainFXML = getClass().getResource("MainWindow.fxml");
            FXMLLoader loader = new FXMLLoader(mainFXML);
            BorderPane rootPane = (BorderPane) loader.load();
            mainWindowController = loader.getController();
            Scene scene = new Scene(rootPane, 600, 700, Color.RED);
            stage.setScene(scene);

            // Load menu
            MenuBar menu = (MenuBar) FXMLLoader.load(MENU_FILE.toURI().toURL());
            rootPane.setTop(menu);

        } catch (IOException e) {
            e.printStackTrace();
            logger.severe("Error loading MZmine GUI from FXML: " + e);
            Platform.exit();
        }

        stage.setTitle("MZmine " + MZmineCore.getMZmineVersion());

        stage.setMinWidth(300);
        stage.setMinHeight(300);

        // Set application icon
        final Image mzMineIcon = new Image(
                "file:lib" + File.separator + "mzmine-icon.png");
        stage.getIcons().setAll(mzMineIcon);

        stage.setOnCloseRequest(e -> {
            requestQuit();
            e.consume();
        });

        stage.show();

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
        Dialog<ButtonType> dialog = new Dialog<>();
        // dialog.initOwner(stage);
        dialog.setTitle("Message");
        dialog.setContentText(msg);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.showAndWait();
    }

    static MZmineProject getCurrentProject() {
        return currentProject;
    }

    static void submitTasks(Collection<Task> tasks) {
        for (Task task : tasks) {
            TaskJavaFXWrapper wrappedTask = new TaskJavaFXWrapper(task);
            mainWindowController.addTask(wrappedTask);
        }

    }

}
