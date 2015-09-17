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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.controlsfx.control.spreadsheet.GridBase;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

import io.github.msdk.datamodel.featuretables.FeatureTable;
import io.github.msdk.datamodel.featuretables.FeatureTableColumn;
import io.github.msdk.datamodel.featuretables.FeatureTableRow;
import io.github.msdk.datamodel.featuretables.Sample;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;

public class Table {

    public static TableView getFeatureTable(FeatureTable featureTable) {

        char nextChar = 'A';
        String mapChar;
        int totalColumns = 0;
        final List<FeatureTableColumn<?>> columns = featureTable.getColumns();
        TableColumn<Map, String> tableColumn = null;
        TableColumn sampleColumn = null;
        Sample prevSample = null, currentSample = null;

        // New Table
        TableView table = new TableView();

        // Common columns
        for (FeatureTableColumn<?> col : columns) {
            currentSample = col.getSample();
            if (currentSample == null) {
                mapChar = String.valueOf(nextChar++);
                tableColumn = new TableColumn<>(col.getName());
                tableColumn.setCellValueFactory(new MapValueFactory(mapChar));
                table.getColumns().add(tableColumn);
                totalColumns++;
            }
        }

        // Sample columns
        for (FeatureTableColumn<?> col : columns) {
            currentSample = col.getSample();

            if (currentSample != null) {

                // Creates sample columns
                if (prevSample != null && prevSample.equals(currentSample)) {
                    mapChar = String.valueOf(nextChar++);
                    tableColumn = new TableColumn<>(col.getName());
                    tableColumn
                            .setCellValueFactory(new MapValueFactory(mapChar));
                    sampleColumn.getColumns().add(tableColumn);
                    totalColumns++;
                } else { // Create sample header and sample columns
                    sampleColumn = new TableColumn(currentSample.getName());
                    table.getColumns().add(sampleColumn);
                    prevSample = currentSample;

                    mapChar = String.valueOf(nextChar++);
                    tableColumn = new TableColumn<>(col.getName());
                    tableColumn
                            .setCellValueFactory(new MapValueFactory(mapChar));
                    sampleColumn.getColumns().add(tableColumn);
                    totalColumns++;
                }
            }

        }

        // Add rows
        final List<FeatureTableRow> rows = featureTable.getRows();
        int rowNr = 0;
        ObservableList<Map> allData = table.getItems();
        for (FeatureTableRow row : rows) {
            rowNr++;
            int columnNr = 0;
            Map<String, String> dataRow = new HashMap<>();

            for (FeatureTableColumn<?> column : columns) {

                String mapKey = Character.toString((char) ('A' + columnNr));
                if (row.getData(column) != null) {
                    String value1 = row.getData(column).toString();
                    dataRow.put(mapKey, value1);
                    // System.out.println(mapKey + ": " + value1 + ", " +
                    // column.getName());
                } else {
                    dataRow.put(mapKey, null);
                }

                columnNr++;
            }

            allData.add(dataRow);
        }

        // Table preferences
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        return table;

    }

    public static SpreadsheetView getFeatureTable2(FeatureTable featureTable) {

        final List<FeatureTableColumn<?>> featureColumns = featureTable
                .getColumns();
        final List<FeatureTableRow> featureRows = featureTable.getRows();

        int rowCount = featureRows.size();
        int columnCount = featureColumns.size();
        GridBase grid = new GridBase(rowCount, columnCount);

        ObservableList<ObservableList<SpreadsheetCell>> rows = FXCollections
                .observableArrayList();
        for (int row = 0; row < grid.getRowCount(); ++row) {
            final ObservableList<SpreadsheetCell> list = FXCollections
                    .observableArrayList();
            for (int column = 0; column < grid.getColumnCount(); ++column) {
                list.add(SpreadsheetCellType.STRING.createCell(row, column, 1,
                        1, "value"));
            }
            rows.add(list);
        }
        grid.setRows(rows);

        SpreadsheetView table = new SpreadsheetView(grid);

        return table;

    }

}
