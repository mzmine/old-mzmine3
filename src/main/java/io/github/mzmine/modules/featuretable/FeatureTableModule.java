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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

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
import javafx.concurrent.Task;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.control.TreeTableView;
import javafx.util.Callback;

public class FeatureTableModule implements MZmineRunnableModule {

    @Nonnull
    private static final String MODULE_NAME = "Feature Table";

    @Nonnull
    private static final String MODULE_DESCRIPTION = "This module creates a TableView of a feature table.";

    private static Map<Integer, TreeTableColumn<FeatureTableRow, Object>> columnMap;

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
        TreeTableColumn<FeatureTableRow, Object> tableColumn = null;
        TreeTableColumn<FeatureTableRow, Object> sampleColumn = null;
        Sample prevSample = null, currentSample = null;
        int totalColumns = 0;

        // Table tree root
        final TreeItem<FeatureTableRow> root = new TreeItem<>();
        root.setExpanded(true);

        // Add all rows to their group
        for (FeatureTableRow row : rows) {
            root.getChildren().add(new TreeItem(new TreeItem<>(row)));
        }

        // New tree table
        TreeTableView<FeatureTableRow> treeTable = new TreeTableView<>(root);

        // Common columns
        for (FeatureTableColumn<?> col : columns) {
            currentSample = col.getSample();
            if (currentSample == null) {
                tableColumn = new TreeTableColumn<FeatureTableRow, Object>(
                        col.getName());

                tableColumn.setCellValueFactory(
                        new Callback<CellDataFeatures<FeatureTableRow, Object>, ObservableValue<Object>>() {
                            public ObservableValue<Object> call(
                                    CellDataFeatures<FeatureTableRow, Object> p) {

                                if (p.getValue() != null) {
                                    if (p.getValue().getValue() != null) {
                                        TreeItem treeItem = (TreeItem) p
                                                .getValue().getValue();
                                        FeatureTableRow featureTableRow = (FeatureTableRow) treeItem
                                                .getValue();
                                        if (featureTableRow
                                                .getData(col) != null) {
                                            return new SimpleObjectProperty<>(
                                                    featureTableRow
                                                            .getData(col));
                                        }
                                    }
                                }

                                return null;

                            }
                        });

                // Set column renderer
                Class renderClass = ColumnRenderers
                        .getRenderClass(col.getName());
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
        for (FeatureTableColumn<?> col : columns) {
            currentSample = col.getSample();

            if (currentSample != null) {
                // Create sample header
                if (prevSample == null || !prevSample.equals(currentSample)) {
                    sampleColumn = new TreeTableColumn(currentSample.getName());
                    treeTable.getColumns().add(sampleColumn);
                    prevSample = currentSample;
                }

                // Creates sample columns
                tableColumn = new TreeTableColumn<>(col.getName());

                tableColumn.setCellValueFactory(
                        new Callback<CellDataFeatures<FeatureTableRow, Object>, ObservableValue<Object>>() {
                            public ObservableValue<Object> call(
                                    CellDataFeatures<FeatureTableRow, Object> p) {

                                if (p.getValue() != null) {
                                    if (p.getValue().getValue() != null) {
                                        TreeItem treeItem = (TreeItem) p
                                                .getValue().getValue();
                                        FeatureTableRow featureTableRow = (FeatureTableRow) treeItem
                                                .getValue();
                                        if (featureTableRow
                                                .getData(col) != null) {
                                            return new SimpleObjectProperty<>(
                                                    featureTableRow
                                                            .getData(col));
                                        }
                                    }
                                }

                                return null;

                            }
                        });

                // Set column renderer
                Class renderClass = ColumnRenderers
                        .getRenderClass(col.getName());
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
