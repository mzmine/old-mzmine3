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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.msdk.datamodel.featuretables.ColumnName;
import io.github.msdk.datamodel.featuretables.FeatureTable;
import io.github.msdk.datamodel.featuretables.FeatureTableColumn;
import io.github.msdk.datamodel.featuretables.FeatureTableRow;
import io.github.msdk.datamodel.featuretables.Sample;
import io.github.mzmine.gui.MZmineGUI;
import io.github.mzmine.modules.MZmineModuleCategory;
import io.github.mzmine.modules.MZmineRunnableModule;
import io.github.mzmine.parameters.ParameterSet;
import io.github.mzmine.project.MZmineProject;
import io.github.mzmine.util.TableUtils;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;

public class FeatureTableModule implements MZmineRunnableModule {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Nonnull
    private static final String MODULE_NAME = "Feature Table";

    @Nonnull
    private static final String MODULE_DESCRIPTION = "This module creates a TableView of a feature table.";

    private static Map<Integer, TableColumn<Map, Object>> columnMap;

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

        columnMap = new HashMap<Integer, TableColumn<Map, Object>>();

        // FeatureTable featureTable
        final List<FeatureTable> featureTables = parameters
                .getParameter(FeatureTableModuleParameters.featureTables)
                .getValue().getMatchingFeatureTables();
        FeatureTable featureTable = featureTables.get(0);

        char nextChar = 'A';
        String mapChar;
        int totalColumns = 0;
        final List<FeatureTableColumn<?>> columns = featureTable.getColumns();
        TableColumn<Map, Object> tableColumn = null;
        TableColumn sampleColumn = null;
        Sample prevSample = null, currentSample = null;

        // New Table
        TableView table = new TableView();

        // Common columns
        for (FeatureTableColumn<?> col : columns) {
            currentSample = col.getSample();
            Class dataType = col.getDataTypeClass();
            if (currentSample == null) {
                mapChar = String.valueOf(nextChar++);
                tableColumn = new TableColumn<>(col.getName());
                tableColumn.setCellValueFactory(
                        new MapValueFactory<Object>(mapChar));
                tableColumn
                        .setCellFactory(new CellFactoryCallback(col.getName()));
                if (col.getName().equals(ColumnName.ID.getName())
                        || col.getName().equals(ColumnName.MZ.getName())
                        || col.getName().equals(ColumnName.CHARGE.getName())) {
                    tableColumn.setStyle("-fx-alignment: CENTER;");
                }
                table.getColumns().add(tableColumn);
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
                    sampleColumn = new TableColumn(currentSample.getName());
                    table.getColumns().add(sampleColumn);
                    prevSample = currentSample;
                }

                // Creates sample columns
                mapChar = String.valueOf(nextChar++);
                tableColumn = new TableColumn<>(col.getName());
                tableColumn.setCellValueFactory(
                        new MapValueFactory<Object>(mapChar));
                tableColumn
                        .setCellFactory(new CellFactoryCallback(col.getName()));
                tableColumn.setStyle("-fx-alignment: CENTER;");
                sampleColumn.getColumns().add(tableColumn);
                columnMap.put(totalColumns, tableColumn);
                totalColumns++;
            }
        }

        // Add blank column at the end of the table to make space for the scroll
        // bar
        tableColumn = new TableColumn<>("");
        tableColumn.setPrefWidth(15);
        table.getColumns().add(tableColumn);
        columnMap.put(totalColumns, tableColumn);

        // Add rows
        final List<FeatureTableRow> rows = featureTable.getRows();
        int rowNr = 0;
        ObservableList<Map> allData = table.getItems();
        for (FeatureTableRow row : rows) {
            rowNr++;
            int columnNr = 0;
            Map<String, Object> dataRow = new HashMap<>();

            for (FeatureTableColumn<?> column : columns) {
                String mapKey = Character.toString((char) ('A' + columnNr));
                if (row.getData(column) != null) {
                    Object value1 = row.getData(column);
                    dataRow.put(mapKey, value1);
                } else {
                    dataRow.put(mapKey, null);
                }

                columnNr++;
            }

            allData.add(dataRow);
        }

        // Table preferences
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        table.getSelectionModel().setCellSelectionEnabled(true);

        // Enable copy to clipboard
        TableUtils.addCopyHandler(table, columnMap);

        // Add new window with table
        MZmineGUI.addWindow(table, featureTable.getName());

    }

    public Map<Integer, TableColumn<Map, Object>> getColumnMap() {
        return columnMap;
    }

    @Override
    @Nonnull
    public Class<? extends ParameterSet> getParameterSetClass() {
        return FeatureTableModuleParameters.class;
    }

    @Override
    @Nonnull
    public MZmineModuleCategory getModuleCategory() {
        // TODO REMOVE!
        return MZmineModuleCategory.VISUALIZATIONRAWDATA;
    }

}
