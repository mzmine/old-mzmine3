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

package net.sf.mzmine.gui;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import net.sf.mzmine.main.MZmineCore;

/**
 * MZmine JavaFX Application class
 */
public final class MZmineGUI extends Application {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    private static Stage mainStage;

    public void start(Stage stage) {

	mainStage = stage;

	try {
	    Parent rootNode = FXMLLoader.load(getClass().getResource(
		    "MainWindow.fxml"));
	    Scene scene = new Scene(rootNode, 600, 700);
	    stage.setScene(scene);
	} catch (IOException e) {
	    e.printStackTrace();
	    logger.severe("Error loading MZmine GUI from FXML: " + e);
	    Platform.exit();
	}

	stage.setTitle("MZmine " + MZmineCore.getMZmineVersion());

	stage.setMinWidth(300);
	stage.setMinHeight(300);

	// Set application icon
	final Image mzMineIcon = new Image("file:lib" + File.separator
		+ "mzmine-icon.png");
	stage.getIcons().setAll(mzMineIcon);

	stage.setOnCloseRequest(e -> {

	    Dialog<ButtonType> dialog = new Dialog<>();
	    dialog.initOwner(stage);
	    dialog.setTitle("Confirmation");
	    dialog.setContentText("Are you sure you want to exit?");
	    dialog.getDialogPane().getButtonTypes()
		    .addAll(ButtonType.YES, ButtonType.NO);
	    dialog.showAndWait().ifPresent(response -> {
		if (response == ButtonType.YES) {
		    Platform.exit();
		    System.exit(0);
		}
	    });
	    e.consume();
	});

	stage.show();

    }

    public static Stage getMainWindow() {
	return mainStage;
    }

}
