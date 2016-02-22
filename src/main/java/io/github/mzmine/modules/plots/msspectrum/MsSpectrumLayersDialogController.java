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

package io.github.mzmine.modules.plots.msspectrum;

import java.util.List;

import io.github.msdk.datamodel.msspectra.MsSpectrumType;
import io.github.mzmine.main.MZmineCore;
import io.github.mzmine.modules.plots.msspectrum.fxcomponents.ColorTableCell;
import io.github.mzmine.modules.plots.msspectrum.fxcomponents.SpinnerTableCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;

/**
 * MS spectrum layer setup dialog controller
 */
public class MsSpectrumLayersDialogController {

    @FXML
    private Stage dialogStage;

    @FXML
    private TableView<MsSpectrumDataSet> layersTable;

    @FXML
    private TableColumn<MsSpectrumDataSet, MsSpectrumType> renderingTypeColumn;

    @FXML
    private TableColumn<MsSpectrumDataSet, Color> colorColumn;

    @FXML
    private TableColumn<MsSpectrumDataSet, Number> intensityScaleColumn;

    @FXML
    private TableColumn<MsSpectrumDataSet, Integer> lineThicknessColumn;

    @FXML
    private TableColumn<MsSpectrumDataSet, Boolean> showDataPointsColumn;

    private MsSpectrumPlotWindowController plotController;

    public void initialize() {

        final ObservableList<MsSpectrumType> renderingChoices = FXCollections
                .observableArrayList(MsSpectrumType.CENTROIDED,
                        MsSpectrumType.PROFILE);
        renderingTypeColumn.setCellFactory(
                ChoiceBoxTableCell.forTableColumn(renderingChoices));

        colorColumn.setCellFactory(
                column -> new ColorTableCell<MsSpectrumDataSet>(column));

        lineThicknessColumn.setCellFactory(
                column -> new SpinnerTableCell<MsSpectrumDataSet>(column, 1,
                        5));

        intensityScaleColumn.setCellFactory(
                TextFieldTableCell.forTableColumn(new NumberStringConverter(
                        MZmineCore.getConfiguration().getIntensityFormat())));

        showDataPointsColumn.setCellFactory(
                column -> new CheckBoxTableCell<MsSpectrumDataSet, Boolean>() {
                    {
                        tableRowProperty().addListener(e -> {
                            TableRow row = getTableRow();
                            if (row == null)
                                return;
                            MsSpectrumDataSet dataSet = (MsSpectrumDataSet) row
                                    .getItem();
                            if (dataSet == null)
                                return;
                            disableProperty().bind(
                                    dataSet.renderingTypeProperty().isEqualTo(
                                            MsSpectrumType.CENTROIDED));

                        });
                    }
                });
    }

    public void handleDeleteLayer(Event event) {
        List<MsSpectrumDataSet> selected = layersTable.getSelectionModel()
                .getSelectedItems();
        layersTable.getItems().removeAll(selected);
    }

    public void handleAddScan(Event event) {
        plotController.handleAddScan(event);
    }

    public void handleAddSpectrumFromText(Event event) {
        plotController.handleAddSpectrumFromText(event);
    }

    public void handleAddIsotopePattern(Event event) {
        plotController.handleAddIsotopePattern(event);
    }

    public void handleClose(Event event) {
        dialogStage.close();
    }

    public void configure(ObservableList<MsSpectrumDataSet> items,
            MsSpectrumPlotWindowController plotController) {
        layersTable.setItems(items);
        this.plotController = plotController;
    }

}