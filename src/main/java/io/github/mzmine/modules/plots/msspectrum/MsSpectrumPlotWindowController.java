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

import java.io.IOException;
import java.net.URL;

import javax.annotation.Nonnull;

import com.google.common.base.Preconditions;

import io.github.msdk.datamodel.msspectra.MsSpectrum;
import io.github.mzmine.util.charts.MZmineChartViewer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * MS spectrum plot window
 */
public class MsSpectrumPlotWindowController {

    private static final String LAYERS_DIALOG_FXML = "MsSpectrumLayersDialog.fxml";

    @FXML
    private BorderPane chartPane;

    @FXML
    private MZmineChartViewer chartNode;

    private final ObservableList<MsSpectrumDataSet> dataSets = FXCollections
            .observableArrayList();

    @FXML
    public void showContextMenu(ContextMenuEvent event) {
    }

    @FXML
    public void hideContextMenu(ContextMenuEvent event) {
    }

    @FXML
    public void addSpectrum(Event e) {

    }

    @FXML
    public void previousScan(Event e) {

    }

    @FXML
    public void setupLayers(Event event) {
        try {
            URL layersDialogFXML = getClass().getResource(LAYERS_DIALOG_FXML);
            FXMLLoader loader = new FXMLLoader(layersDialogFXML);
            Stage dialog = loader.load();
            MsSpectrumLayersDialogController controller = loader
                    .getController();
            controller.setItems(dataSets);
            dialog.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void nextScan(Event e) {

    }

    /**
     * Add a new spectrum to the plot.
     * 
     * @param spectrum
     */
    public void addSpectrum(@Nonnull MsSpectrum spectrum) {

        Preconditions.checkNotNull(spectrum);

        MsSpectrumDataSet newDataSet = new MsSpectrumDataSet(spectrum);
        dataSets.add(newDataSet);
        chartNode.addDataSet(newDataSet);

    }

}