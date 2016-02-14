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

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.net.URL;

import javax.annotation.Nonnull;

import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;

import com.google.common.base.Preconditions;

import io.github.msdk.datamodel.msspectra.MsSpectrum;
import io.github.msdk.datamodel.msspectra.MsSpectrumType;
import io.github.mzmine.util.JavaFXUtil;
import io.github.mzmine.util.charts.jfreechart.ChartNodeJFreeChart;
import io.github.mzmine.util.charts.jfreechart.IntelligentItemLabelGenerator;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * MS spectrum plot window
 */
public class MsSpectrumPlotWindowController {

    // Colors
    private static final Color gridColor = Color.rgb(220, 220, 220, 0.3);
    private static final Color labelsColor = Color.BLACK;
    private static final Color[] plotColors = { Color.rgb(0, 0, 192), // blue
            Color.rgb(192, 0, 0), // red
            Color.rgb(0, 192, 0), // green
            Color.MAGENTA, Color.CYAN, Color.ORANGE };

    // Font
    private static final Font legendFont = new Font("SansSerif", Font.PLAIN,
            11);

    private static final String LAYERS_DIALOG_FXML = "MsSpectrumLayersDialog.fxml";

    private final ObservableList<MsSpectrumDataSet> dataSets = FXCollections
            .observableArrayList();
    private int numberOfDataSets = 0;

    @FXML
    private BorderPane chartPane;

    @FXML
    private ChartNodeJFreeChart chartNode;

    @FXML
    public void initialize() {

        final XYPlot plot = chartNode.getChart().getXYPlot();

        // Do not set colors and strokes dynamically. They are instead provided
        // by the dataset and configured in configureRenderer()
        plot.setDrawingSupplier(null);

        plot.setDomainGridlinePaint(JavaFXUtil.convertColorToAWT(gridColor));
        plot.setRangeGridlinePaint(JavaFXUtil.convertColorToAWT(gridColor));

        chartNode.setCursor(Cursor.CROSSHAIR);
    }

    @FXML
    public void showContextMenu(ContextMenuEvent event) {
        // Update context menu items
    }

    @FXML
    public void hideContextMenu(ContextMenuEvent event) {
    }

    @FXML
    public void handleAddSpectrum(Event e) {

    }

    @FXML
    public void handlePreviousScan(Event e) {

    }

    @FXML
    public void handleNextScan(Event e) {

    }

    @FXML
    public void handleSetupLayers(Event event) {
        try {
            URL layersDialogFXML = getClass().getResource(LAYERS_DIALOG_FXML);
            FXMLLoader loader = new FXMLLoader(layersDialogFXML);
            Stage layersDialog = loader.load();
            MsSpectrumLayersDialogController controller = loader
                    .getController();
            controller.setItems(dataSets);
            layersDialog.initModality(Modality.APPLICATION_MODAL);
            layersDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Add a new spectrum to the plot.
     * 
     * @param spectrum
     */
    public synchronized void addSpectrum(@Nonnull MsSpectrum spectrum) {

        Preconditions.checkNotNull(spectrum);

        MsSpectrumDataSet newDataSet = new MsSpectrumDataSet(spectrum);
        dataSets.add(newDataSet);
        final int datasetIndex = numberOfDataSets;
        numberOfDataSets++;

        final XYPlot plot = chartNode.getChart().getXYPlot();

        final Color newColor = plotColors[datasetIndex % plotColors.length];
        newDataSet.setColor(newColor);

        configureRenderer(newDataSet, datasetIndex);

        newDataSet.renderingTypeProperty().addListener(e -> {
            configureRenderer(newDataSet, datasetIndex);
        });
        newDataSet.colorProperty().addListener(e -> {
            configureRenderer(newDataSet, datasetIndex);
        });
        newDataSet.lineThicknessProperty().addListener(e -> {
            configureRenderer(newDataSet, datasetIndex);
        });
        newDataSet.showDataPointsProperty().addListener(e -> {
            configureRenderer(newDataSet, datasetIndex);
        });

        // Once everything is configured, add the dataset to the plot
        Platform.runLater(() -> plot.setDataset(datasetIndex, newDataSet));

    }

    private void configureRenderer(MsSpectrumDataSet dataSet,
            int dataSetIndex) {

        final MsSpectrumType renderingType = dataSet.getRenderingType();
        final XYPlot plot = chartNode.getChart().getXYPlot();

        // Set renderer
        XYItemRenderer newRenderer;
        switch (renderingType) {
        case PROFILE:
        case THRESHOLDED:
            XYLineAndShapeRenderer newLineRenderer = new XYLineAndShapeRenderer();
            final int lineThickness = dataSet.getLineThickness();
            newLineRenderer.setBaseShape(
                    new Ellipse2D.Double(-2 * lineThickness, -2 * lineThickness,
                            4 * lineThickness + 1, 4 * lineThickness + 1));
            newLineRenderer.setBaseShapesFilled(true);
            newLineRenderer.setBaseShapesVisible(dataSet.getShowDataPoints());
            newLineRenderer.setDrawOutlines(false);
            Stroke baseStroke = new BasicStroke(lineThickness);
            newLineRenderer.setBaseStroke(baseStroke);
            newRenderer = newLineRenderer;
            break;
        case CENTROIDED:
        default:
            XYBarRenderer newBarRenderer = new XYBarRenderer();
            // Avoid gradients
            newBarRenderer.setBarPainter(new StandardXYBarPainter());
            newBarRenderer.setShadowVisible(false);
            newRenderer = newBarRenderer;
            break;
        }

        // Set color
        Color baseColor = dataSet.getColor();
        newRenderer.setBasePaint(JavaFXUtil.convertColorToAWT(baseColor));

        // Set label generator
        XYItemLabelGenerator intelligentLabelGenerator = new IntelligentItemLabelGenerator(
                chartNode, plot, 100, dataSet);
        // newRenderer.setBaseItemLabelGenerator(intelligentLabelGenerator);
        newRenderer.setBaseItemLabelPaint(
                JavaFXUtil.convertColorToAWT(labelsColor));
        newRenderer.setBaseItemLabelsVisible(true);

        // Set tooltip generator
        newRenderer.setBaseToolTipGenerator(dataSet);

        plot.setRenderer(dataSetIndex, newRenderer);

    }
}