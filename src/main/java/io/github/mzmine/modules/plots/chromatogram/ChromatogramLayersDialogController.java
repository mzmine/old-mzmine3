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

package io.github.mzmine.modules.plots.chromatogram;

import java.util.List;

import io.github.mzmine.main.MZmineCore;
import io.github.mzmine.util.fxcomponents.ColorTableCell;
import io.github.mzmine.util.fxcomponents.SpinnerTableCell;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;

/**
 * MS spectrum layer setup dialog controller
 */
public class ChromatogramLayersDialogController {

    @FXML
    private Stage dialogStage;

    @FXML
    private TableView<ChromatogramPlotDataSet> layersTable;

    @FXML
    private TableColumn<ChromatogramPlotDataSet, Color> colorColumn;

    @FXML
    private TableColumn<ChromatogramPlotDataSet, Number> intensityScaleColumn;

    @FXML
    private TableColumn<ChromatogramPlotDataSet, Integer> lineThicknessColumn;

    @FXML
    private TableColumn<ChromatogramPlotDataSet, Boolean> showDataPointsColumn;

    private ChromatogramPlotWindowController plotController;

    public void initialize() {

        colorColumn.setCellFactory(
                column -> new ColorTableCell<ChromatogramPlotDataSet>(column));

        lineThicknessColumn.setCellFactory(
                column -> new SpinnerTableCell<ChromatogramPlotDataSet>(column,
                        1, 5));

        intensityScaleColumn.setCellFactory(
                TextFieldTableCell.forTableColumn(new NumberStringConverter(
                        MZmineCore.getConfiguration().getIntensityFormat())));

        showDataPointsColumn.setCellFactory(
                column -> new CheckBoxTableCell<ChromatogramPlotDataSet, Boolean>());

    }

    public void handleDeleteLayer(Event event) {
        List<ChromatogramPlotDataSet> selected = layersTable.getSelectionModel()
                .getSelectedItems();
        layersTable.getItems().removeAll(selected);
    }

    public void handleAddScan(Event event) {

    }

    public void handleAddSpectrumFromText(Event event) {

    }

    public void handleAddIsotopePattern(Event event) {

    }

    public void handleClose(Event event) {
        dialogStage.close();
    }

    public void configure(ObservableList<ChromatogramPlotDataSet> items,
            ChromatogramPlotWindowController plotController) {
        layersTable.setItems(items);
        this.plotController = plotController;
    }

}