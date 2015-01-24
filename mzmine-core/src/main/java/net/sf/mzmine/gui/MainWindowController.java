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
 * MZmine 2; if not, write to the Free Software Foundation, Inc., 51 Franklin
 * St, Fifth Floor, Boston, MA 02110-1301 USA
 */

package net.sf.mzmine.gui;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import net.sf.mzmine.main.MZmineCore;

import org.controlsfx.control.StatusBar;
import org.controlsfx.control.TaskProgressView;

/**
 * This class is the main window of application
 * 
 */
public class MainWindowController implements Initializable {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    private static final Image rawDataFilesIcon = new Image("xicicon.png");
    private static final Image peakListsIcon = new Image("peaklistsicon.png");
    private static final Image groupIcon = new Image("groupicon.png");
    private static final Image fileIcon = new Image("fileicon.png");
    private static final Image peakListIcon = new Image(
	    "peaklisticon_single.png");

    @FXML
    private TreeView<Object> rawDataTree;

    @FXML
    private TreeView<Object> peakListTree;

    @FXML
    private TaskProgressView<?> tasksView;

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

	TreeItem<Object> rawDataRootItem = new TreeItem<>("Raw data files");
	rawDataRootItem.setGraphic(new ImageView(rawDataFilesIcon));
	rawDataRootItem.setExpanded(true);
	rawDataTree.setRoot(rawDataRootItem);

	peakListTree.getSelectionModel().setSelectionMode(
		SelectionMode.MULTIPLE);
	TreeItem<Object> peakListsRootItem = new TreeItem<>("Peak lists");
	peakListsRootItem.setGraphic(new ImageView(peakListsIcon));
	peakListsRootItem.setExpanded(true);
	peakListTree.setRoot(peakListsRootItem);

	// peakListTree.setCellFactory(TextFieldTreeCell.forTreeView());

	TreeItem<Object> df1 = new TreeItem<>("File 1");
	df1.setGraphic(new ImageView(fileIcon));

	TreeItem<Object> group1 = new TreeItem<>("Group 1");
	group1.setGraphic(new ImageView(groupIcon));
	TreeItem<Object> df2 = new TreeItem<>("File 2");
	df2.setGraphic(new ImageView(fileIcon));
	TreeItem<Object> df3 = new TreeItem<>("File 3");
	df3.setGraphic(new ImageView(fileIcon));
	TreeItem<Object> df4 = new TreeItem<>("File 4");
	df4.setGraphic(new ImageView(fileIcon));
	TreeItem<Object> df5 = new TreeItem<>("File 5");
	df5.setGraphic(new ImageView(fileIcon));
	TreeItem<Object> df6 = new TreeItem<>("File 6");
	df6.setGraphic(new ImageView(fileIcon));
	group1.getChildren().addAll(df4, df5, df6);
	rawDataTree.getRoot().getChildren().addAll(df1, group1, df2, df3);

	TreeItem<Object> item1 = new TreeItem<>("Peak list 1");
	item1.setGraphic(new ImageView(peakListIcon));
	TreeItem<Object> item2 = new TreeItem<>("Peak list 2");
	item2.setGraphic(new ImageView(peakListIcon));
	TreeItem<Object> item3 = new TreeItem<>("Peak list 3");
	item3.setGraphic(new ImageView(peakListIcon));
	peakListTree.getRoot().getChildren().addAll(item1, item2, item3);

	statusBar.setText("Welcome to MZmine " + MZmineCore.getMZmineVersion());

	// Setup the Timeline to update the memory indicator periodically
	final Timeline memoryUpdater = new Timeline();
	final int UPDATE_FREQUENCY = 500; // ms
	memoryUpdater.setCycleCount(Animation.INDEFINITE);
	memoryUpdater.setAutoReverse(true);
	memoryUpdater.getKeyFrames()
		.add(new KeyFrame(Duration.millis(UPDATE_FREQUENCY), (
			ActionEvent e) -> {

		    final long freeMemMB = Runtime.getRuntime().freeMemory()
			    / (1024 * 1024);
		    final long totalMemMB = Runtime.getRuntime().totalMemory()
			    / (1024 * 1024);
		    final double memory = ((double) (totalMemMB - freeMemMB))
			    / totalMemMB;

		    memoryBar.setProgress(memory);
		    memoryBarLabel.setText(freeMemMB + "/" + totalMemMB
			    + " MB free");
		}));
	memoryUpdater.play();

    }

    @FXML
    public void memoryBarClicked(MouseEvent e) {
	// Run garbage collector on a new thread, so it does not block the GUI
	new Thread(() -> {
	    logger.info("Running garbage collector");
	    System.gc();
	}).start();

    }

    public TreeView<?> getRawDataTree() {
	return rawDataTree;
    }

    public TreeView<?> getPeakListTree() {
	return peakListTree;
    }

    public TaskProgressView<?> getTaskTable() {
	return tasksView;
    }

}
