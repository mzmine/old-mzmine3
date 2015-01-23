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

import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import net.sf.mzmine.main.MZmineCore;

import org.controlsfx.control.StatusBar;
import org.controlsfx.control.TaskProgressView;

/**
 * This class is the main window of application
 * 
 */
public class MZmineMainWindowNode extends VBox {

    private MZmineMenuBar menuBar;
    private RawDataFilesTreeNode rawDataTree;
    private PeakListsTreeNode peakListTree;
    private TaskProgressView<?> taskTable;
    private StatusBar statusBar;

    public MZmineMainWindowNode() {

	menuBar = new MZmineMenuBar();

	rawDataTree = new RawDataFilesTreeNode();
	ScrollPane rawDataTreeScroll = new ScrollPane(rawDataTree);
	rawDataTreeScroll.setFitToWidth(true);
	rawDataTreeScroll.setFitToHeight(true);

	peakListTree = new PeakListsTreeNode();
	ScrollPane peakListTreeScroll = new ScrollPane(peakListTree);
	peakListTreeScroll.setFitToWidth(true);
	peakListTreeScroll.setFitToHeight(true);

	SplitPane split = new SplitPane();
	split.setMinHeight(60);

	split.getItems().addAll(rawDataTreeScroll, peakListTreeScroll);
	split.setDividerPositions(0.5f);

	taskTable = new TaskProgressView();
	taskTable.setPrefHeight(100);
	taskTable.setMinHeight(60);

	statusBar = new StatusBar();
	statusBar.setMinHeight(statusBar.getPrefHeight());
	statusBar.setText("Welcome to MZmine " + MZmineCore.getMZmineVersion());
	
	setFillWidth(true);

	getChildren().addAll(menuBar, split, taskTable, statusBar);
	VBox.setVgrow(split, Priority.ALWAYS);

	/*
	 * BorderStroke stroke = new BorderStroke(Color.RED,
	 * BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.MEDIUM);
	 * setBorder(new Border(stroke));
	 */

    }

    public TreeView getRawDataTree() {
	return rawDataTree;
    }

    public TreeView getPeakListTree() {
	return peakListTree;
    }

    public TaskProgressView<?> getTaskTable() {
	return taskTable;
    }

}
