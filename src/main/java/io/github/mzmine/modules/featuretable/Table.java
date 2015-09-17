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

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.msdk.datamodel.featuretables.FeatureTable;
import io.github.msdk.datamodel.featuretables.FeatureTableColumn;
import io.github.msdk.datamodel.featuretables.FeatureTableRow;
import io.github.msdk.datamodel.featuretables.Sample;
import io.github.msdk.datamodel.ionannotations.IonAnnotation;
import io.github.msdk.datamodel.rawdata.ChromatographyInfo;
import javafx.collections.ObservableList;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.MapValueFactory;
import javafx.util.Callback;

public class Table {

    public static TableView getFeatureTable(FeatureTable featureTable) {

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

                tableColumn.setCellFactory(new Callback<TableColumn<Map, Object>, TableCell<Map, Object>>() {

                    @Override
                    public TableCell<Map, Object> call(TableColumn<Map, Object> p) {
                        return new TableCell<Map, Object>() {
                            @Override
                            public void updateItem(Object object, boolean empty) {
                                super.updateItem(object, empty);
                                if (object == null) {
                                    setTooltip(null);
                                    setText(null);
                                } else {
                                    // Tooltip
                                    Tooltip tooltip = new Tooltip();
                                    tooltip.setText("This is a tooltip");
                                    setTooltip(tooltip);

                                    // Format value
                                    String dataTypeClass = object.getClass().getSimpleName();
                                    String value = object.toString();
                                    NumberFormat formatter = new DecimalFormat("#0.00");

                                    switch (dataTypeClass) {
                                        case "Double":
                                            Double doubleValue = (Double) object;
                                            value = formatter.format(doubleValue);
                                            // object.toString();
                                            break;
                                        case "SimpleChromatographyInfo":
                                            ChromatographyInfo chromatographyInfo = (ChromatographyInfo) object;
                                            Float floatValue = chromatographyInfo.getRetentionTime();
                                            value = formatter.format(floatValue);
                                            break;
                                        case "SimpleIonAnnotation":
                                            IonAnnotation ionAnnotation = (IonAnnotation) object;
                                            value = ionAnnotation.getDescription();
                                    }
                                    setText(value);
                                }
                            }
                        };
                    }

                });

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
                    tableColumn.setCellValueFactory(
                            new MapValueFactory<Object>(mapChar));
                    sampleColumn.getColumns().add(tableColumn);
                    totalColumns++;
                } else { // Create sample header and sample columns
                    sampleColumn = new TableColumn(currentSample.getName());
                    table.getColumns().add(sampleColumn);
                    prevSample = currentSample;

                    mapChar = String.valueOf(nextChar++);
                    tableColumn = new TableColumn<>(col.getName());
                    tableColumn.setCellValueFactory(
                            new MapValueFactory<Object>(mapChar));
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

        return table;

    }

}
