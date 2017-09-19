/*
 * Copyright 2006-2016 The MZmine 3 Development Team
 * 
 * This file is part of MZmine 3.
 * 
 * MZmine 3 is free software; you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * MZmine 3 is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with MZmine 3; if not,
 * write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301
 * USA
 */

package io.github.mzmine.project;

import io.github.msdk.datamodel.FeatureTable;
import io.github.msdk.datamodel.RawDataFile;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Simple implementation of the MZmineProject interface.
 */
public class MZmineGUIProject extends MZmineProject {

  private static final Image rawDataFilesIcon = new Image("icons/xicicon.png");
  private static final Image featureTablesIcon = new Image("icons/peaklistsicon.png");
  // private static final Image groupIcon = new Image("icons/groupicon.png");
  private static final Image fileIcon = new Image("icons/fileicon.png");
  private static final Image peakListIcon = new Image("icons/peaklisticon_single.png");

  private final TreeItem<Object> rawDataRootItem;
  private final TreeItem<Object> featureTableRootItem;

  public MZmineGUIProject() {
    rawDataRootItem = new TreeItem<>("Raw data files");
    rawDataRootItem.setGraphic(new ImageView(rawDataFilesIcon));
    rawDataRootItem.setExpanded(true);

    featureTableRootItem = new TreeItem<>("Feature tables");
    featureTableRootItem.setGraphic(new ImageView(featureTablesIcon));
    featureTableRootItem.setExpanded(true);
  }

  public void addFile(final RawDataFile rawDataFile) {
    super.addFile(rawDataFile);
    TreeItem<Object> df1 = new TreeItem<>(rawDataFile);
    df1.setGraphic(new ImageView(fileIcon));
    rawDataRootItem.getChildren().add(df1);
  }

  public void removeFile(final RawDataFile rawDataFile) {
    super.removeFile(rawDataFile);
    for (TreeItem<?> df1 : rawDataRootItem.getChildren()) {
      if (df1.getValue() == rawDataFile) {
        rawDataRootItem.getChildren().remove(df1);
        break;
      }
    }
  }

  public void addFeatureTable(final FeatureTable featureTable) {
    super.addFeatureTable(featureTable);
    TreeItem<Object> df1 = new TreeItem<>(featureTable);
    df1.setGraphic(new ImageView(peakListIcon));
    featureTableRootItem.getChildren().add(df1);
  }

  public void removeFeatureTable(final FeatureTable featureTable) {
    super.removeFeatureTable(featureTable);
    for (TreeItem<?> df1 : featureTableRootItem.getChildren()) {
      if (df1.getValue() == featureTable) {
        featureTableRootItem.getChildren().remove(df1);
        break;
      }
    }
  }

  public TreeItem<Object> getRawDataRootItem() {
    return rawDataRootItem;
  }

  public TreeItem<Object> getFeatureTableRootItem() {
    return featureTableRootItem;
  }

}
