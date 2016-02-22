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

package io.github.mzmine.modules.plots.msspectrum.fxcomponents;

import io.github.msdk.datamodel.msspectra.MsSpectrumType;
import io.github.mzmine.modules.plots.msspectrum.MsSpectrumDataSet;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;

public class SpinnerTableCell<T> extends TableCell<T, Integer> {

    private final Spinner<Number> spinner;

    public SpinnerTableCell(TableColumn<T, Integer> column, int min, int max) {
        
        spinner = new Spinner<>(min, max, 1);
        
        spinner.editableProperty().bind(column.editableProperty());
        spinner.disableProperty().bind(column.editableProperty().not());
        
        tableRowProperty().addListener(e -> {
            TableRow row = getTableRow();
            if (row == null)
                return;
            MsSpectrumDataSet dataSet = (MsSpectrumDataSet) row
                    .getItem();
            if (dataSet == null)
                return;
            spinner.getValueFactory().valueProperty()
                    .bindBidirectional(
                            dataSet.lineThicknessProperty());
            disableProperty().bind(dataSet.renderingTypeProperty()
                    .isEqualTo(MsSpectrumType.CENTROIDED));

        });
    }

    @Override
    protected void updateItem(Integer c, boolean empty) {

        super.updateItem(c, empty);
        if (empty || c == null) {
            setText(null);
            setGraphic(null);
            return;
        }
        spinner.getValueFactory().setValue(c);

        setGraphic(spinner);
    }
}


