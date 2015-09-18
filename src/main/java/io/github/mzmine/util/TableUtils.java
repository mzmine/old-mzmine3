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
 * MZmine 3; if not, write to the Free Software Foundation, Inc., 51 Franklin
 * St, Fifth Floor, Boston, MA 02110-1301 USA
 */

package io.github.mzmine.util;

import java.util.Map;

import io.github.msdk.datamodel.ionannotations.IonAnnotation;
import io.github.msdk.datamodel.rawdata.ChromatographyInfo;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;

public class TableUtils {

    /**
     * Add the keyboard handler.
     * 
     * @param table
     */
    public static void addCopyHandler(TableView<?> table) {
        table.setOnKeyPressed(new KeyEventHandler());
    }

    /**
     * Copy keyboard event handler.
     */
    public static class KeyEventHandler implements EventHandler<KeyEvent> {
        KeyCodeCombination keyCodeCompination = new KeyCodeCombination(
                KeyCode.C, KeyCombination.CONTROL_ANY);

        public void handle(final KeyEvent keyEvent) {
            if (keyCodeCompination.match(keyEvent)) {
                if (keyEvent.getSource() instanceof TableView) {
                    // copy to clipboard
                    copySelectionToClipboard(
                            (TableView<?>) keyEvent.getSource());

                    // event is handled, consume it
                    keyEvent.consume();
                }
            }
        }

    }

    /**
     * Get table selection and copy it to the clipboard.
     * 
     * @param table
     */
    public static void copySelectionToClipboard(TableView<?> table) {
        StringBuilder clipboardString = new StringBuilder();

        ObservableList<TablePosition> positionList = table.getSelectionModel()
                .getSelectedCells();
        int prevRow = -1;

        //Map<Integer, TableColumn<Map,Object>> columnMap = table.getColumnMap();

        // Add column headers
        for (TablePosition position : positionList) {
            int rowNr = position.getRow();
            int columnNr = position.getColumn();

            if (prevRow == rowNr || prevRow == -1) {
                String columnTitle = table.getColumns().get(columnNr).getText();
                clipboardString.append(columnTitle);
                clipboardString.append('\t');
            } else {
                clipboardString.append('\n');
                prevRow = -1;
                break;
            }
            prevRow = rowNr;
        }

        // Add data
        for (TablePosition position : positionList) {

            int rowNr = position.getRow();
            int columnNr = position.getColumn();
            TableColumn column = table.getColumns().get(columnNr);
            int columnClildren = table.getColumns().get(columnNr).getColumns()
                    .size();
            String text = null;

            if (columnClildren == 0) {
                Object object = (Object) table.getColumns().get(columnNr)
                        .getCellData(rowNr);
                if (object instanceof ChromatographyInfo) {
                    // Format to RT1, RT2
                    ChromatographyInfo chromatographyInfo = (ChromatographyInfo) object;
                    Float rt1 = chromatographyInfo.getRetentionTime();
                    Float rt2 = chromatographyInfo.getSecondaryRetentionTime();
                    if (rt1 != null) {
                        text = rt1.toString();
                    }
                    if (text != null && rt2 != null) {
                        text = text + ", ";
                    }
                    if (rt2 != null) {
                        text = text + rt2.toString();
                    }
                } else if (object instanceof IonAnnotation) {
                    IonAnnotation ionAnnotation = (IonAnnotation) object;
                    text = ionAnnotation.getDescription();
                    if (text == null)
                        text = ionAnnotation.getAnnotationId();
                } else {
                    if (object != null)
                        text = object.toString();
                }

                // determine whether we advance in a row or a column
                if (prevRow == rowNr) {
                    clipboardString.append('\t');
                } else if (prevRow != -1) {
                    clipboardString.append('\n');
                }

                // add new item to clipboard
                clipboardString.append(text);
            } else {
                System.out.println(column.getText());
                System.out.println(columnClildren);

                // determine whether we advance in a row or a column
                if (prevRow == rowNr) {
                    clipboardString.append('\t');
                } else if (prevRow != -1) {
                    clipboardString.append('\n');
                }

                clipboardString.append("FIX");
            }

            // remember previous
            prevRow = rowNr;
        }

        // Set the clipboardString to the clipboard
        final ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(clipboardString.toString());
        Clipboard.getSystemClipboard().setContent(clipboardContent);
    }
}
