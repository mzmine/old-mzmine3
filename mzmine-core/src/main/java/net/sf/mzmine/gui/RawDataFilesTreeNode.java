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

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * 
 */
class RawDataFilesTreeNode extends TreeView<Object> {

    private static final Image rootIcon = new Image(
	    "projecticon.png");
    private static final Image fileIcon = new Image(
	    "fileicon.png");
    private static final Image peakListIcon = new Image(
	    "peaklisticon_single.png");

    private final TreeItem<Object> rootItem;

    RawDataFilesTreeNode() {

	rootItem = new TreeItem<>("Raw data files");
	rootItem.setGraphic(new ImageView(rootIcon));
	rootItem.setExpanded(true);
	setRoot(rootItem);

	TreeItem<Object> item1 = new TreeItem<>("File 1");
	item1.setGraphic(new ImageView(fileIcon));

	TreeItem<Object> item2 = new TreeItem<>("File 2");
	item2.setGraphic(new ImageView(fileIcon));

	TreeItem<Object> item3 = new TreeItem<>("File 3");
	item3.setGraphic(new ImageView(fileIcon));

	rootItem.getChildren().addAll(item1, item2, item3);

    }

}
