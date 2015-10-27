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
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;

public class FeatureTablePopupMenu extends ContextMenu {

    public FeatureTablePopupMenu(FeatureTable featureTable, TreeTableView<FeatureTableRow> treeTable) {

        // Show menu
        Menu showMenu = new Menu("Show");
        MenuItem xicItem = new MenuItem("Extracted Ion Chromatogram (XIC)");
        showMenu.getItems().addAll(xicItem);
        getItems().addAll(showMenu);

        // Other items
        MenuItem deleteItem = new MenuItem("Delete selected row(s)");
        deleteItem.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent e)
            {
                ObservableList<TreeItem<FeatureTableRow>> rows = treeTable.getSelectionModel().getSelectedItems();
                if(rows!=null) {
                    for (TreeItem<FeatureTableRow> row : rows) {
                        FeatureTableRow featureTableRow = row.getValue();

                        //featureTable.removeRow(featureTableRow);
                        System.out.println("Delete row: " + row);
                        System.out.println("Delete feature table row: " + featureTableRow);
                    }
                }
            }
        });
        getItems().addAll(deleteItem);

    }

}
