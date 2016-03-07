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
 * MZmine 3; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

package io.github.mzmine.modules.plots.chromatogram;

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.io.File;
import java.text.DecimalFormat;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.RangeType;
import org.jfree.ui.RectangleInsets;

import io.github.msdk.datamodel.chromatograms.Chromatogram;
import io.github.mzmine.main.MZmineCore;
import io.github.mzmine.modules.plots.chromatogram.datasets.ChromatogramDataSet;
import io.github.mzmine.util.JavaFXUtil;
import io.github.mzmine.util.jfreechart.ChartNodeJFreeChart;
import io.github.mzmine.util.jfreechart.IntelligentItemLabelGenerator;
import io.github.mzmine.util.jfreechart.JFreeChartUtils;
import io.github.mzmine.util.jfreechart.JFreeChartUtils.ImgFileType;
import io.github.mzmine.util.jfreechart.ManualZoomDialog;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Window;

/**
 * Chromatogram plot window
 */
public class ChromatogramPlotWindowController {

    private final ObservableList<ChromatogramPlotDataSet> datasets = FXCollections
            .observableArrayList();
    private int numberOfDataSets = 0;

    // Colors
    private static final Color gridColor = Color.rgb(220, 220, 220, 0.5);
    private static final Color crossHairColor = Color.GRAY;

    private static final Color labelsColor = Color.BLACK;
    private static final Color backgroundColor = Color.WHITE;
    private static final Color[] plotColors = { Color.rgb(0, 0, 192), // blue
            Color.rgb(192, 0, 0), // red
            Color.rgb(0, 192, 0), // green
            Color.MAGENTA, Color.CYAN, Color.ORANGE };

    private static final Font titleFont = new Font("SansSerif", Font.BOLD, 12);

    private static final String LAYERS_DIALOG_FXML = "ChromatogramLayersDialog.fxml";

    private final BooleanProperty itemLabelsVisible = new SimpleBooleanProperty(
            this, "itemLabelsVisible", true);
    private final BooleanProperty legendVisible = new SimpleBooleanProperty(
            this, "legendVisible", true);

    private File lastSaveDirectory;

    private TextTitle chartTitle, chartSubTitle;

    @FXML
    private BorderPane chartPane;

    @FXML
    private ChartNodeJFreeChart chartNode;

    @FXML
    private MenuItem showSpectrumMenuItem;

    @FXML
    private Menu removeDatasetMenu;

    @FXML
    public void initialize() {

        final JFreeChart chart = chartNode.getChart();
        final XYPlot plot = chart.getXYPlot();

        // Do not set colors and strokes dynamically. They are instead provided
        // by the dataset and configured in configureRenderer()
        plot.setDrawingSupplier(null);
        plot.setDomainGridlinePaint(JavaFXUtil.convertColorToAWT(gridColor));
        plot.setRangeGridlinePaint(JavaFXUtil.convertColorToAWT(gridColor));
        plot.setBackgroundPaint(JavaFXUtil.convertColorToAWT(backgroundColor));
        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
        plot.setDomainCrosshairPaint(JavaFXUtil.convertColorToAWT(crossHairColor));
        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(false);

        // chart properties
        chart.setBackgroundPaint(JavaFXUtil.convertColorToAWT(backgroundColor));

        // legend properties
        LegendTitle legend = chart.getLegend();
        // legend.setItemFont(legendFont);
        legend.setFrame(BlockBorder.NONE);

        // set the X axis (retention time) properties
        NumberAxis xAxis = (NumberAxis) plot.getDomainAxis();
        xAxis.setLabel("Retention time (min)");
        xAxis.setUpperMargin(0.03);
        xAxis.setLowerMargin(0.03);
        xAxis.setRangeType(RangeType.POSITIVE);
        xAxis.setTickLabelInsets(new RectangleInsets(0, 0, 20, 20));

        // set the Y axis (intensity) properties
        NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
        yAxis.setLabel("Intensity");
        yAxis.setRangeType(RangeType.POSITIVE);
        yAxis.setAutoRangeIncludesZero(true);

        // set the fixed number formats, because otherwise JFreeChart sometimes
        // shows exponent, sometimes it doesn't
        DecimalFormat mzFormat = MZmineCore.getConfiguration().getMZFormat();
        xAxis.setNumberFormatOverride(mzFormat);
        DecimalFormat intensityFormat = MZmineCore.getConfiguration()
                .getIntensityFormat();
        yAxis.setNumberFormatOverride(intensityFormat);

        chartTitle = chartNode.getChart().getTitle();
        chartTitle.setMargin(5, 0, 0, 0);
        chartTitle.setFont(titleFont);
        chartTitle.setText("Chromatogram");

        chartNode.setCursor(Cursor.CROSSHAIR);

        // Remove the dataset if it is removed from the list
        datasets.addListener((Change<? extends ChromatogramPlotDataSet> c) -> {
            while (c.next()) {
                if (c.wasRemoved()) {
                    for (ChromatogramPlotDataSet ds : c.getRemoved()) {
                        int index = plot.indexOf(ds);
                        plot.setDataset(index, null);
                    }
                }
            }
        });

        itemLabelsVisible.addListener((prop, oldVal, newVal) -> {
            for (ChromatogramPlotDataSet dataset : datasets) {
                int datasetIndex = plot.indexOf(dataset);
                XYItemRenderer renderer = plot.getRenderer(datasetIndex);
                renderer.setBaseItemLabelsVisible(newVal);
            }
        });

        legendVisible.addListener((prop, oldVal, newVal) -> {
            legend.setVisible(newVal);
        });
    }

    void addChromatogram(Chromatogram chromatogram) {

        final int datasetIndex = numberOfDataSets;
        numberOfDataSets++;

        String chromatName = "Chromatogram "
                + chromatogram.getChromatogramNumber();
        ChromatogramDataSet newDataSet = new ChromatogramDataSet(chromatogram,
                chromatName);

        datasets.add(newDataSet);

        final XYPlot plot = chartNode.getChart().getXYPlot();

        final Color newColor = plotColors[datasetIndex % plotColors.length];
        newDataSet.setColor(newColor);

        configureRenderer(newDataSet, datasetIndex);

        newDataSet.colorProperty().addListener(e -> {
            Platform.runLater(
                    () -> configureRenderer(newDataSet, datasetIndex));
        });
        newDataSet.lineThicknessProperty().addListener(e -> {
            Platform.runLater(
                    () -> configureRenderer(newDataSet, datasetIndex));
        });
        newDataSet.showDataPointsProperty().addListener(e -> {
            Platform.runLater(
                    () -> configureRenderer(newDataSet, datasetIndex));
        });
        // Once everything is configured, add the dataset to the plot
        Platform.runLater(() -> plot.setDataset(datasetIndex, newDataSet));

    }

    private void configureRenderer(ChromatogramPlotDataSet dataset,
            int datasetIndex) {

        final XYPlot plot = chartNode.getChart().getXYPlot();

        XYLineAndShapeRenderer newRenderer = new XYLineAndShapeRenderer();
        final int lineThickness = dataset.getLineThickness();
        newRenderer.setBaseShape(
                new Ellipse2D.Double(-2 * lineThickness, -2 * lineThickness,
                        4 * lineThickness + 1, 4 * lineThickness + 1));
        newRenderer.setBaseShapesFilled(true);
        newRenderer.setBaseShapesVisible(dataset.getShowDataPoints());
        newRenderer.setDrawOutlines(false);

        Stroke baseStroke = new BasicStroke(lineThickness);
        newRenderer.setBaseStroke(baseStroke);

        // Set tooltips for legend
        newRenderer.setLegendItemToolTipGenerator((ds, series) -> {
            if (ds instanceof ChromatogramPlotDataSet) {
                return ((ChromatogramPlotDataSet) ds).getDescription();
            } else
                return null;
        });

        // Set color
        Color baseColor = dataset.getColor();
        newRenderer.setBasePaint(JavaFXUtil.convertColorToAWT(baseColor));

        // Set label generator
        XYItemLabelGenerator intelligentLabelGenerator = new IntelligentItemLabelGenerator(
                chartNode, 100, dataset);
        newRenderer.setBaseItemLabelGenerator(intelligentLabelGenerator);
        newRenderer.setBaseItemLabelPaint(
                JavaFXUtil.convertColorToAWT(labelsColor));
        newRenderer.setBaseItemLabelsVisible(itemLabelsVisible.get());

        // Set tooltip generator
        newRenderer.setBaseToolTipGenerator(dataset);

        plot.setRenderer(datasetIndex, newRenderer);

    }

    public void handleShowSpectrum(Event e) {

    }

    public void handleNormalizeIntensityScale(Event e) {

    }

    public void handleResetIntensityScale(Event e) {

    }

    public void handleSetupLayers(Event e) {

    }

    public void handleContextMenuShowing(Event e) {

    }

    public void handleChartMousePressed(Event e) {

    }

    public void handleChartKeyPressed(Event e) {

    }

    public void handleAddChromatogramFromFile(Event e) {

    }

    public void handleAddChromatogramFromText(Event e) {

    }

    public void handleCopyImage(Event e) {
        JFreeChartUtils.exportToClipboard(chartNode);
    }

    public void handleCopyChromatogram(Event e) {

    }

    public void handleExportJPG(Event event) {
        JFreeChartUtils.showSaveDialog(chartNode, ImgFileType.JPG);
    }

    public void handleExportPNG(Event event) {
        JFreeChartUtils.showSaveDialog(chartNode, ImgFileType.PNG);
    }

    public void handleExportPDF(Event event) {
        JFreeChartUtils.showSaveDialog(chartNode, ImgFileType.PDF);
    }

    public void handleExportSVG(Event event) {
        JFreeChartUtils.showSaveDialog(chartNode, ImgFileType.SVG);
    }

    public void handleExportEMF(Event event) {
        JFreeChartUtils.showSaveDialog(chartNode, ImgFileType.EMF);
    }

    public void handleExportEPS(Event event) {
        JFreeChartUtils.showSaveDialog(chartNode, ImgFileType.EPS);
    }

    public void handleExportMzML(Event e) {
    }

    public void handleExportCSV(Event e) {
    }

    public void handleExportTXT(Event e) {
    }

    public void handleManualZoom(Event e) {
        XYPlot plot = chartNode.getChart().getXYPlot();
        Window parent = chartNode.getScene().getWindow();
        ManualZoomDialog dialog = new ManualZoomDialog(parent, plot);
        dialog.show();
    }

    public void handleZoomOut(Event e) {
        XYPlot plot = chartNode.getChart().getXYPlot();
        plot.getDomainAxis().setAutoRange(true);
        plot.getRangeAxis().setAutoRange(true);
    }

    public void handleToggleLabels(Event event) {
        itemLabelsVisible.set(!itemLabelsVisible.get());
    }

    public void handleToggleLegend(Event event) {
        legendVisible.set(!legendVisible.get());
    }

    public void handlePrint(Event event) {
        JFreeChartUtils.printChart(chartNode);
    }

}