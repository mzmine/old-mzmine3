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

package io.github.mzmine.util;

import java.util.Map;

import io.github.msdk.datamodel.chromatograms.Chromatogram;
import io.github.msdk.datamodel.featuretables.ColumnName;
import io.github.msdk.datamodel.featuretables.FeatureTableRow;
import io.github.msdk.datamodel.ionannotations.IonAnnotation;
import io.github.msdk.datamodel.rawdata.ChromatographyInfo;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTablePosition;
import javafx.scene.control.TreeTableView;
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
    public static void addCopyHandler(TreeTableView<?> table,
            Map<Integer, TreeTableColumn<FeatureTableRow, Object>> columnMap) {
        table.setOnKeyPressed(new KeyEventHandler(columnMap));
    }

    /**
     * Copy event handler.
     */
    public static class KeyEventHandler implements EventHandler<KeyEvent> {
        KeyCodeCombination keyCodeCompination = new KeyCodeCombination(
                KeyCode.C, KeyCombination.CONTROL_ANY);

        Map<Integer, TreeTableColumn<FeatureTableRow, Object>> columnMap;

        KeyEventHandler(
                Map<Integer, TreeTableColumn<FeatureTableRow, Object>> columnMap) {
            this.columnMap = columnMap;
        }

        public void handle(final KeyEvent keyEvent) {
            if (keyCodeCompination.match(keyEvent)) {
                if (keyEvent.getSource() instanceof TreeTableView) {
                    // Copy to clipboard
                    copySelectionToClipboard(
                            (TreeTableView<?>) keyEvent.getSource());

                    // Event is handled, consume it
                    keyEvent.consume();
                }
            }
        }

        /**
         * Get table selection and copy it to the clipboard.
         * 
         * @param <S>
         * 
         * @param table
         */
        public void copySelectionToClipboard(TreeTableView table) {
            StringBuilder clipboardString = new StringBuilder();

            // ObservableList<TreeTablePosition> positionList =
            // table.getSelectionModel().getSelectedCells();
            ObservableList<TreeTablePosition<?, ?>> positionList = table
                    .getSelectionModel().getSelectedCells();
            int prevRow = -1;

            // Add sample headers
            int hiddenColumns = 0;
            for (TreeTablePosition position : positionList) {
                int rowNr = position.getRow();
                int columnNr = position.getColumn() + hiddenColumns;

                // Account for hidden columns
                while (!columnMap.get(columnNr).isVisible()) {
                    hiddenColumns++;
                    columnNr++;
                }

                // Get the column from the map to avoid trouble with sample
                // headers
                TreeTableColumn column = columnMap.get(columnNr);
                Object object = (Object) column.getCellData(rowNr);

                // Skip chromatogram column
                if (object instanceof Chromatogram)
                    continue;

                if (prevRow == rowNr || prevRow == -1) {
                    String columnTitle;
                    if (column.getParentColumn() != null) {
                        columnTitle = column.getParentColumn().getText();
                    } else {
                        columnTitle = "";
                    }

                    clipboardString.append(columnTitle);
                    clipboardString.append('\t');
                } else {
                    break;
                }
                prevRow = rowNr;
            }
            prevRow = -1;
            clipboardString.append('\n');

            // Add column headers
            hiddenColumns = 0;
            for (TreeTablePosition position : positionList) {
                int rowNr = position.getRow();
                int columnNr = position.getColumn() + hiddenColumns;

                // Account for hidden columns
                while (!columnMap.get(columnNr).isVisible()) {
                    hiddenColumns++;
                    columnNr++;
                }

                // Get the column from the map to avoid trouble with sample
                // headers
                TreeTableColumn column = columnMap.get(columnNr);
                Object object = (Object) column.getCellData(rowNr);

                // Skip chromatogram column
                if (object instanceof Chromatogram)
                    continue;

                // Add all columns
                if (prevRow == rowNr || prevRow == -1) {
                    String columnTitle = column.getText();
                    clipboardString.append(columnTitle);
                    clipboardString.append('\t');
                } else {
                    break;
                }
                prevRow = rowNr;
            }
            prevRow = -1;
            clipboardString.append('\n');

            // Add data
            hiddenColumns = 0;
            for (TreeTablePosition position : positionList) {

                int rowNr = position.getRow();
                if (prevRow != rowNr) {
                    hiddenColumns = 0;
                }
                int columnNr = position.getColumn() + hiddenColumns;

                // Account for hidden columns
                while (!columnMap.get(columnNr).isVisible()) {
                    hiddenColumns++;
                    columnNr++;
                }

                // Get the column from the map to avoid trouble with sample
                // headers
                TreeTableColumn column = columnMap.get(columnNr);

                String text = null;
                Object object = (Object) column.getCellData(rowNr);
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
                } else if (object instanceof Chromatogram) {
                    continue;
                } else {
                    if (object != null)
                        text = object.toString();
                }

                // Are we looping over a row or column?
                if (prevRow == rowNr) {
                    clipboardString.append('\t');
                } else if (prevRow != -1) {
                    clipboardString.append('\n');
                }

                if (text == null || text.equals("null"))
                    text = "";

                // Add new item to clipboard
                clipboardString.append(text);

                // Remember previous
                prevRow = rowNr;
            }

            // Set string to the clipboard
            final ClipboardContent clipboardContent = new ClipboardContent();
            clipboardContent.putString(clipboardString.toString());
            Clipboard.getSystemClipboard().setContent(clipboardContent);
        }
    }

}
