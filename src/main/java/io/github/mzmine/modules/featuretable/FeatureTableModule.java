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
 * MZmine 3; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

package io.github.mzmine.modules.featuretable;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import io.github.msdk.datamodel.featuretables.ColumnName;
import io.github.msdk.datamodel.featuretables.FeatureTable;
import io.github.msdk.datamodel.featuretables.FeatureTableColumn;
import io.github.msdk.datamodel.featuretables.FeatureTableRow;
import io.github.msdk.datamodel.featuretables.Sample;
import io.github.mzmine.gui.MZmineGUI;
import io.github.mzmine.modules.MZmineRunnableModule;
import io.github.mzmine.parameters.ParameterSet;
import io.github.mzmine.project.MZmineProject;
import io.github.mzmine.util.TableUtils;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.control.TreeTablePosition;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

public class FeatureTableModule implements MZmineRunnableModule {

    @Nonnull
    private static final String MODULE_NAME = "Feature Table";

    @Nonnull
    private static final String MODULE_DESCRIPTION = "This module creates a TableView of a feature table.";

    private static Map<Integer, TreeTableColumn<FeatureTableRow, Object>> columnMap;
    private static Map<Integer, TreeItem<FeatureTableRow>> rowMap;

    @Override
    public @Nonnull String getName() {
        return MODULE_NAME;
    }

    @Override
    public @Nonnull String getDescription() {
        return MODULE_DESCRIPTION;
    }

    @Override
    public void runModule(@Nonnull MZmineProject project,
            @Nonnull ParameterSet parameters,
            @Nonnull Collection<Task<?>> tasks) {

        // FeatureTable featureTable
        final List<FeatureTable> featureTables = parameters
                .getParameter(FeatureTableModuleParameters.featureTables)
                .getValue().getMatchingFeatureTables();
        FeatureTable featureTable = featureTables.get(0);

        // Variables
        final List<FeatureTableColumn<?>> columns = featureTable.getColumns();
        final List<FeatureTableRow> rows = featureTable.getRows();
        columnMap = new HashMap<Integer, TreeTableColumn<FeatureTableRow, Object>>();
        rowMap = new HashMap<Integer, TreeItem<FeatureTableRow>>();
        TreeTableColumn<FeatureTableRow, Object> tableColumn = null;
        TreeTableColumn<FeatureTableRow, Object> sampleColumn = null;
        Sample prevSample = null, currentSample = null;
        int totalColumns = 0;

        // Table tree root
        final TreeItem<FeatureTableRow> root = new TreeItem<>();
        root.setExpanded(true);

        // Table tree items
        TreeItem<FeatureTableRow> treeItem;

        // Group rows
        FeatureTableColumn<Integer> groupColoumn = featureTable
                .getColumn(ColumnName.GROUPID, null);
        FeatureTableColumn<Integer> idColoumn = featureTable
                .getColumn(ColumnName.ID, null);
        for (FeatureTableRow row : rows) {
            // No group column
            if (groupColoumn == null) {
                treeItem = new TreeItem<>(row);
                root.getChildren().add(treeItem);
            }
            // No group id
            else if (row.getData(groupColoumn) == null) {
                treeItem = new TreeItem<>(row);
                treeItem.setExpanded(true);
                root.getChildren().add(treeItem);
                rowMap.put(row.getData(idColoumn), treeItem);
            }
            // The row has a group id
            else {
                Integer groupID = row.getData(groupColoumn);
                TreeItem<FeatureTableRow> parentTreeItem = rowMap.get(groupID);
                if (parentTreeItem == null) {
                    parentTreeItem = root;
                }
                treeItem = new TreeItem<>(row);
                parentTreeItem.getChildren().add(treeItem);
                rowMap.put(row.getData(idColoumn), treeItem);
            }
        }

        // New tree table
        TreeTableView<FeatureTableRow> treeTable = new TreeTableView<>(root);

        // Common columns
        for (FeatureTableColumn<?> column : columns) {

            // Don't show Group ID column
            if (column.getName() == ColumnName.GROUPID.getName())
                continue;

            currentSample = column.getSample();
            if (currentSample == null) {
                tableColumn = new TreeTableColumn<FeatureTableRow, Object>(
                        column.getName());

                tableColumn.setCellValueFactory(
                        new Callback<CellDataFeatures<FeatureTableRow, Object>, ObservableValue<Object>>() {
                            public ObservableValue<Object> call(
                                    CellDataFeatures<FeatureTableRow, Object> p) {

                                if (p.getValue() != null) {
                                    if (p.getValue().getValue() != null) {
                                        FeatureTableRow featureTableRow = (FeatureTableRow) p
                                                .getValue().getValue();
                                        if (featureTableRow
                                                .getData(column) != null) {
                                            return new SimpleObjectProperty<>(
                                                    featureTableRow
                                                            .getData(column));
                                        }
                                    }
                                }

                                return null;

                            }
                        });

                // Set column renderer
                Class<?> renderClass = ColumnRenderers
                        .getRenderClass(column.getName());
                Callback<TreeTableColumn<FeatureTableRow, Object>, TreeTableCell<FeatureTableRow, Object>> rendeder = null;
                try {
                    rendeder = (Callback<TreeTableColumn<FeatureTableRow, Object>, TreeTableCell<FeatureTableRow, Object>>) renderClass
                            .newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
                tableColumn.setCellFactory(rendeder);

                treeTable.getColumns().add(tableColumn);
                columnMap.put(totalColumns, tableColumn);
                totalColumns++;
            }
        }

        // Sample columns
        for (FeatureTableColumn<?> column : columns) {
            currentSample = column.getSample();

            if (currentSample != null) {
                // Create sample header
                if (prevSample == null || !prevSample.equals(currentSample)) {
                    sampleColumn = new TreeTableColumn(currentSample.getName());
                    treeTable.getColumns().add(sampleColumn);
                    prevSample = currentSample;
                }

                // Creates sample columns
                tableColumn = new TreeTableColumn<>(column.getName());

                tableColumn.setCellValueFactory(
                        new Callback<CellDataFeatures<FeatureTableRow, Object>, ObservableValue<Object>>() {
                            public ObservableValue<Object> call(
                                    CellDataFeatures<FeatureTableRow, Object> p) {

                                if (p.getValue() != null) {
                                    if (p.getValue().getValue() != null) {
                                        FeatureTableRow featureTableRow = (FeatureTableRow) p
                                                .getValue().getValue();
                                        if (featureTableRow
                                                .getData(column) != null) {
                                            return new SimpleObjectProperty<>(
                                                    featureTableRow
                                                            .getData(column));
                                        }
                                    }
                                }

                                return null;

                            }
                        });

                // Set column renderer
                Class<?> renderClass = ColumnRenderers
                        .getRenderClass(column.getName());
                Callback<TreeTableColumn<FeatureTableRow, Object>, TreeTableCell<FeatureTableRow, Object>> rendeder = null;
                try {
                    rendeder = (Callback<TreeTableColumn<FeatureTableRow, Object>, TreeTableCell<FeatureTableRow, Object>>) renderClass
                            .newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
                tableColumn.setCellFactory(rendeder);

                tableColumn.setStyle("-fx-alignment: CENTER;");
                sampleColumn.getColumns().add(tableColumn);
                columnMap.put(totalColumns, tableColumn);
                totalColumns++;
            }
        }

        // Add right padding on last column to fix issue with scroll bar
        if (tableColumn != null)
            tableColumn.setStyle("-fx-padding: 0 20 0 0;");

        // Table preferences
        treeTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        treeTable.getSelectionModel().setCellSelectionEnabled(true);
        treeTable.setShowRoot(false);

        // Add right click menu
        FeatureTablePopupMenu popupMenu = new FeatureTablePopupMenu(
                featureTable, treeTable);
        treeTable.setContextMenu(popupMenu);

        // Add double click to open XIC chromatogram
        treeTable.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
                    // Find feature table row
                    Node node = ((Node) event.getTarget()).getParent();
                    TreeTableRow<?> row;
                    while (!(node instanceof TreeTableRow)) {
                        node = node.getParent();
                    }
                    row = (TreeTableRow<?>) node;
                    FeatureTableRow featureTableRow = (FeatureTableRow) row
                            .getItem();

                    // Find sample
                    Sample sample = null;
                    ObservableList<TreeTablePosition<FeatureTableRow, ?>> cells = treeTable
                            .getSelectionModel().getSelectedCells();
                    for (TreeTablePosition<FeatureTableRow, ?> cell : cells) {
                        if (cell.getTableColumn().getParentColumn() != null) {
                            TreeTableColumn<?, ?> parentColumn = (TreeTableColumn) cell
                                    .getTableColumn().getParentColumn();
                            List<Sample> samples = featureTable.getSamples();
                            for (Sample s : samples) {
                                if (s.getName()
                                        .equals(parentColumn.getText())) {
                                    sample = s;
                                    break;
                                }
                            }
                        }
                    }

                    /*
                     * TODO: Show XIC chromatogram
                     */
                    System.out.println(
                            "Show XIC chromatogram for row " + featureTableRow);
                    System.out.println("Sample: " + sample);
                }
            }
        });

        // Add column selection button
        treeTable.setTableMenuButtonVisible(true);

        // Enable copy to clipboard
        TableUtils.addCopyHandler(treeTable, columnMap);

        // Add new window with table
        MZmineGUI.addWindow(treeTable, featureTable.getName());

        // Add custom table menu
        FeatureTableMenu.addCustomTableMenu(treeTable);
    }

    public Map<Integer, TreeTableColumn<FeatureTableRow, Object>> getColumnMap() {
        return columnMap;
    }

    @Override
    @Nonnull
    public Class<? extends ParameterSet> getParameterSetClass() {
        return FeatureTableModuleParameters.class;
    }

}
