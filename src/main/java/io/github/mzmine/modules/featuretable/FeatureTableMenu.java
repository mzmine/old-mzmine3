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
 * MZmine 3; if not, write to the Free Software Foundation, Inc., 51 Franklin
 * St, Fifth Floor, Boston, MA 02110-1301 USA
 */

package io.github.mzmine.modules.featuretable;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import com.sun.javafx.scene.control.skin.TreeTableViewSkin;

import io.github.msdk.datamodel.featuretables.FeatureTableRow;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.MouseEvent;

public class FeatureTableMenu {

    /**
     * Make a custom table menu for the feature table.
     * 
     * @param treeTableView
     */
    public static void addCustomTableMenu(
            TreeTableView<FeatureTableRow> treeTableView) {

        // Enable table menu
        treeTableView.setTableMenuButtonVisible(true);

        // Check that table has been populated
        TreeTableViewSkin<?> tableSkin = (TreeTableViewSkin<?>) treeTableView
                .getSkin();

        if (tableSkin == null) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    setCustomContextMenu(treeTableView);
                }
            });

        } else {
            // Replace internal mouse listener with custom listener
            setCustomContextMenu(treeTableView);
        }
    }

    private static void setCustomContextMenu(
            TreeTableView<FeatureTableRow> table) {

        TreeTableViewSkin<?> tableSkin = (TreeTableViewSkin<?>) table.getSkin();

        // Get all children of the skin
        ObservableList<Node> children = tableSkin.getChildren();

        // Find the TableHeaderRow child
        for (int i = 0; i < children.size(); i++) {
            Node node = children.get(i);

            if (node instanceof TableHeaderRow) {
                TableHeaderRow tableHeaderRow = (TableHeaderRow) node;

                // Make sure that the header row always has a height and thus is
                // visible
                double defaultHeight = tableHeaderRow.getHeight();
                tableHeaderRow.setPrefHeight(defaultHeight);

                for (Node child : tableHeaderRow.getChildren()) {

                    if (child.getStyleClass()
                            .contains("show-hide-columns-button")) {

                        // Create a context menu
                        ContextMenu columnPopupMenu = createContextMenu(table);

                        // Replace the mouse listener
                        child.setOnMousePressed(me -> {
                            columnPopupMenu.show(child, Side.BOTTOM, 0, 0);
                            me.consume();
                        });
                    }
                }

            }
        }
    }

    /**
     * Create a custom menu.
     * 
     * @param table
     * @return contextMenu
     */
    private static ContextMenu createContextMenu(
            TreeTableView<FeatureTableRow> table) {

        // New context menu
        ContextMenu cm = new ContextMenu();
        CustomMenuItem cmi;

        // Select all item
        Label selectAll = new Label("Select all");
        selectAll.addEventHandler(MouseEvent.MOUSE_CLICKED,
                new EventHandler<MouseEvent>() {

                    @Override
                    public void handle(MouseEvent event) {
                        for (Object obj : table.getColumns()) {
                            ((TreeTableColumn<?, ?>) obj).setVisible(true);
                        }
                    }

                });

        cmi = new CustomMenuItem(selectAll);
        cmi.setHideOnClick(false);
        cm.getItems().add(cmi);

        // Deselect all item
        Label deselectAll = new Label("Deselect all");
        deselectAll.addEventHandler(MouseEvent.MOUSE_CLICKED,
                new EventHandler<MouseEvent>() {

                    @Override
                    public void handle(MouseEvent event) {
                        for (Object obj : table.getColumns()) {
                            ((TreeTableColumn<?, ?>) obj).setVisible(false);
                        }
                    }

                });

        cmi = new CustomMenuItem(deselectAll);
        cmi.setHideOnClick(false);
        cm.getItems().add(cmi);

        // Separator
        cm.getItems().add(new SeparatorMenuItem());

        // Variables
        Boolean sampleColumns = false;

        // menu item for each of the available columns
        for (Object obj : table.getColumns()) {

            TreeTableColumn<?, ?> tableColumn = (TreeTableColumn<?, ?>) obj;
            int childrenColumns = tableColumn.getColumns().size();
            CheckBox cb;

            if (childrenColumns != 0 && !sampleColumns) {
                sampleColumns = true;

                // Separator
                cm.getItems().add(new SeparatorMenuItem());

                // Sample specific columns
                for (TreeTableColumn col : tableColumn.getColumns()) {
                    cb = new CheckBox("Sample: " + col.getText());

                    // Bind to all equal sample specific columns
                    for (Object obj2 : table.getColumns()) {
                        TreeTableColumn<?, ?> tableColumn2 = (TreeTableColumn<?, ?>) obj2;
                        int childrenColumns2 = tableColumn2.getColumns().size();
                        if (childrenColumns2 != 0) {

                            for (TreeTableColumn col2 : tableColumn2
                                    .getColumns()) {
                                if (col.getText().equals(col2.getText())) {
                                    cb.selectedProperty().bindBidirectional(
                                            col2.visibleProperty());

                                    // cb.setSelected(true);
                                    // cb.selectedProperty().addListener(
                                    // new ChangeListener<Boolean>() {
                                    // public void changed(
                                    // ObservableValue<? extends Boolean> ov,
                                    // Boolean old_val,
                                    // Boolean new_val) {
                                    // col2.setVisible(new_val);
                                    // }
                                    // });

                                }
                            }
                        }
                    }

                    cmi = new CustomMenuItem(cb);
                    cmi.setHideOnClick(false);

                    cm.getItems().add(cmi);
                }

                // Separator
                cm.getItems().add(new SeparatorMenuItem());
            }

            if (childrenColumns == 0) {
                cb = new CheckBox(tableColumn.getText());
                cb.selectedProperty()
                        .bindBidirectional(tableColumn.visibleProperty());

                cmi = new CustomMenuItem(cb);
                cmi.setHideOnClick(false);

                cm.getItems().add(cmi);
            }

        }

        return cm;
    }

}
