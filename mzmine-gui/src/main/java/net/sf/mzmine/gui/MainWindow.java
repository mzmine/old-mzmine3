/*
 * Copyright 2006-2014 The MZmine 2 Development Team
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
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import net.sf.mzmine.datamodel.PeakListRow;

import org.controlsfx.control.StatusBar;
import org.controlsfx.control.TaskProgressView;

/**
 * This class is the main window of application
 * 
 */
public class MainWindow extends VBox {

    private TreeTableView<PeakListRow> rawDataTree, peakListTree;
    private TaskProgressView<?> taskTable;
    private StatusBar statusBar;

    public MainWindow() {

	rawDataTree = new TreeTableView<PeakListRow>();
	ScrollPane rawDataTreeScroll = new ScrollPane(rawDataTree);
	rawDataTreeScroll.setFitToWidth(true);
	rawDataTreeScroll.setFitToHeight(true);
	
	peakListTree = new TreeTableView<PeakListRow>();
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

	//VBox vbox = new VBox(8);
	setFillWidth(true);
	getChildren().addAll(split, taskTable, statusBar);
	VBox.setVgrow(split, Priority.ALWAYS);
	BorderStroke stroke = new BorderStroke(Color.RED,
		BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.MEDIUM);
	//setBorder(new Border(stroke));
	

    }

    public TreeTableView<PeakListRow> getRawDataTree() {
	return rawDataTree;
    }

    public TreeTableView<PeakListRow> getPeakListTree() {
	return peakListTree;
    }

    public TaskProgressView<?> getTaskTable() {
	return taskTable;
    }

}
