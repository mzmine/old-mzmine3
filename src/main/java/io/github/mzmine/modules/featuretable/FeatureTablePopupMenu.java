/*
 * Copyright 2006-2015 The MZmine 3 Development Team
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

package io.github.mzmine.modules.featuretable;

import io.github.msdk.datamodel.featuretables.FeatureTable;
import io.github.msdk.datamodel.featuretables.FeatureTableRow;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;

public class FeatureTablePopupMenu extends ContextMenu {

    FeatureTable featureTable;
    TreeTableView<FeatureTableRow> treeTable;

    public FeatureTablePopupMenu(FeatureTable featureTable,
            TreeTableView<FeatureTableRow> treeTable) {

        this.featureTable = featureTable;
        this.treeTable = treeTable;

        /* ***********
         * Show menu
         *************/

        Menu showMenu = new Menu("Show");

        // XIC
        MenuItem xicItem = new MenuItem("Extracted Ion Chromatogram (XIC)");
        xicItem.setOnAction(handleClick("XIC"));
        showMenu.getItems().addAll(xicItem);
        getItems().addAll(showMenu);
        getItems().addAll(new SeparatorMenuItem());


        /* ************
         * Other items
         **************/

        // Expand
        MenuItem expandItem = new MenuItem("Expand all groups");
        expandItem.setOnAction(handleClick("Expand"));
        getItems().addAll(expandItem);

        // Collapse
        MenuItem collapsItem = new MenuItem("Collapse all groups");
        collapsItem.setOnAction(handleClick("Collapse"));
        getItems().addAll(collapsItem);
        getItems().addAll(new SeparatorMenuItem());

        // Delete
        MenuItem deleteItem = new MenuItem("Delete selected row(s)");
        deleteItem.setOnAction(handleClick("Delete"));
        getItems().addAll(deleteItem);

    }

    private EventHandler<ActionEvent> handleClick(String item) {
        EventHandler<ActionEvent> eventHandler = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                ObservableList<TreeItem<FeatureTableRow>> rows = null;
                if (treeTable.getSelectionModel() != null) {
                    rows = treeTable.getSelectionModel().getSelectedItems();
                }

                switch (item) {

                case "XIC":
                    /*
                     * TODO
                     */
                    System.out.println("Show XIC");
                    break;

                case "Expand":
                    /*
                     * TODO
                     */
                    System.out.println("Expand all groups");
                    treeTable.refresh();
                    break;

                case "Collapse":
                    /*
                     * TODO
                     */
                    System.out.println("Collapse all groups");
                    treeTable.refresh();
                    break;

                case "Delete":
                    if (rows != null) {
                        for (int i = rows.size() - 1; i >= 0; i--) {
                            TreeItem<FeatureTableRow> row = rows.get(i);

                            // Remove from featureTable
                            FeatureTableRow featureTableRow = row.getValue();
                            featureTable.removeRow(featureTableRow);

                            // Remove from tree table view
                            TreeItem<?> parent = row.getParent();
                            parent.getChildren().remove(row);
                        }
                        treeTable.refresh();
                        treeTable.getSelectionModel().clearSelection();
                    }
                    break;

                }
            }
        };
        return eventHandler;
    }
}
