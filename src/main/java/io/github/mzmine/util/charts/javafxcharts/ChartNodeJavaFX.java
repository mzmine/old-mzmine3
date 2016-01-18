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

package io.github.mzmine.util.charts.javafxcharts;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import io.github.mzmine.main.MZmineCore;
import io.github.mzmine.util.charts.ChartDataSet;
import io.github.mzmine.util.charts.ChartType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.StringConverter;

/**
 * Chart using JavaFX charts library
 */
public class ChartNodeJavaFX extends StackPane {

    private final NumberAxis xAxis, yAxis;
    private final LineChart<Number, Number> lineChart;

    public ChartNodeJavaFX() {

        xAxis = new NumberAxis();
        yAxis = new NumberAxis();

        NumberFormat intensityFormat = MZmineCore.getConfiguration()
                .getIntensityFormat();
        yAxis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number number) {
                return intensityFormat.format(number.doubleValue());
            }

            @Override
            public Number fromString(String string) {
                try {
                    return intensityFormat.parse(string);
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });

        lineChart = new LineChart<>(xAxis, yAxis);

        lineChart.setTitle("MS spectrum");
        lineChart.setCreateSymbols(false);
        lineChart.setAnimated(false);

        final Rectangle rect = new Rectangle();
        final Text zoomOut = new Text("Zoom out");

        addZoomSupport(lineChart, rect, zoomOut);
        getChildren().addAll(lineChart, rect, zoomOut);

    }

    public synchronized void addDataSet(ChartDataSet newDataSet) {

        xAxis.setLabel(newDataSet.getXAxisName());
        yAxis.setLabel(newDataSet.getYAxisName());

        XYChart.Series<Number, Number> newSeries = new XYChart.Series<>();
        ObservableList<Data<Number, Number>> newSeriesData = FXCollections
                .observableArrayList();

        String spectrumTitle = newDataSet.getName();
        newSeries.setName(spectrumTitle);

        final boolean isBarChart = newDataSet.getType() == ChartType.BAR;

        for (int i = 0; i < newDataSet.getNumOfDataPoints(); i++) {
            final double x = newDataSet.getX(i);
            final double y = newDataSet.getY(i);
            XYChart.Data<Number, Number> newData = new XYChart.Data<>(x, y);

            Text labelNode = new Text();
            labelNode.getStyleClass().add("chart-item-label");
            newData.setNode(labelNode);

            if (isBarChart) {
                XYChart.Data<Number, Number> zeroPoint = new XYChart.Data<>(x,
                        0.0);
                newSeriesData.add(zeroPoint);
            }
            newSeriesData.add(newData);
            if (isBarChart) {
                XYChart.Data<Number, Number> zeroPoint = new XYChart.Data<>(x,
                        0.0);
                newSeriesData.add(zeroPoint);
            }

        }

        // Add data set to the series
        newSeries.setData(newSeriesData);

        xAxis.lowerBoundProperty().addListener(e -> {
            updateLabels();
        });
        xAxis.upperBoundProperty().addListener(e -> {
            updateLabels();
        });

        lineChart.getStyleClass().add("spectrum-plot");

        lineChart.getData().add(newSeries);
        updateLabels();

    }

    private void updateLabels() {

        List<XYChart.Series<Number, Number>> seriesCopy = new ArrayList<>();
        seriesCopy.addAll(lineChart.getData());

        for (XYChart.Series<Number, Number> series : seriesCopy) {
            ObservableList<XYChart.Data<Number, Number>> seriesData = series
                    .getData();

            for (int i = 0; i < seriesData.size(); i++) {
                XYChart.Data<Number, Number> data = seriesData.get(i);
                float intensity = data.getYValue().floatValue();
                if (intensity == 0f)
                    continue;
                Text labelNode = (Text) data.getNode();
                if (labelNode == null)
                    continue;

                boolean visible = shouldLabelBeVisible(seriesData, i);
                if (visible) {
                    double mz = data.getXValue().doubleValue();
                    String labelText = MZmineCore.getConfiguration()
                            .getMZFormat().format(mz);
                    labelNode.setText(labelText);
                } else
                    labelNode.setText("    ");

            }
        }

    }

    private boolean shouldLabelBeVisible(
            ObservableList<XYChart.Data<Number, Number>> seriesData, int item) {

        // X and Y values of current data point
        double originalX = seriesData.get(item).getXValue().doubleValue();
        double originalY = seriesData.get(item).getYValue().doubleValue();

        // Calculate data size of 1 screen pixel
        double xLength = xAxis.getUpperBound() - xAxis.getLowerBound();
        double pixelX = xLength / lineChart.getWidth();

        // Size of data set
        int itemCount = seriesData.size();
        int POINTS_RESERVE_X = 100;

        // Search for data points higher than this one in the interval
        // from limitLeft to limitRight
        double limitLeft = originalX - ((POINTS_RESERVE_X / 2) * pixelX);
        double limitRight = originalX + ((POINTS_RESERVE_X / 2) * pixelX);

        // Iterate data points to the left and right
        for (int i = 1; (item - i > 0) || (item + i < itemCount); i++) {

            // If we get out of the limit we can stop searching
            if ((item - i > 0)
                    && (seriesData.get(item - i).getXValue()
                            .doubleValue() < limitLeft)
                    && ((item + i >= itemCount) || (seriesData.get(item + i)
                            .getXValue().doubleValue() > limitRight)))
                break;

            if ((item + i < itemCount)
                    && (seriesData.get(item + i).getXValue()
                            .doubleValue() > limitRight)
                    && ((item - i <= 0) || (seriesData.get(item - i).getXValue()
                            .doubleValue() < limitLeft)))
                break;

            // If we find higher data point, bail out
            if ((item - i > 0) && (originalY <= seriesData.get(item - i)
                    .getYValue().doubleValue()))
                return false;

            if ((item + i < itemCount) && (originalY <= seriesData.get(item + i)
                    .getYValue().doubleValue()))
                return false;

        }

        return true;

    }

    private void addZoomSupport(XYChart<Number, Number> chart, Rectangle rect,
            Text zoomOut) {

        rect.setManaged(false);
        rect.setFill(Color.LIGHTBLUE.deriveColor(0, 1, 1, 0.5));
        final NumberAxis yAxis = (NumberAxis) chart.getYAxis();
        final NumberAxis xAxis = (NumberAxis) chart.getXAxis();
        yAxis.setForceZeroInRange(true);
        xAxis.setForceZeroInRange(false);

        zoomOut.setVisible(false);
        zoomOut.setManaged(false);
        zoomOut.setFill(Color.BLUE);

        chart.setOnMousePressed(event -> {
            if (event.getButton() != MouseButton.PRIMARY)
                return;
            rect.setX(event.getX());
            rect.setY(event.getY());
        });
        chart.setOnMouseDragged(event -> {
            if (event.getButton() != MouseButton.PRIMARY)
                return;
            double mouseX = event.getX();
            double mouseY = event.getY();
            double rectX = rect.getX();
            double rectY = rect.getY();
            final boolean isZoomedOut = xAxis.isAutoRanging()
                    && yAxis.isAutoRanging();
            if ((!isZoomedOut) && (mouseX < rectX - 5.0)
                    && (mouseY < rectY - 5.0)) {
                rect.setVisible(false);
                zoomOut.setVisible(true);
                zoomOut.setX(rect.getX() - 60.0);
                zoomOut.setY(rect.getY() - 5.0);
            } else if ((mouseX > rectX) && (mouseY > rectY)) {
                zoomOut.setVisible(false);
                rect.setWidth(mouseX - rectX);
                rect.setHeight(mouseY - rectY);
                rect.setVisible(true);
            } else {
                rect.setVisible(false);
                zoomOut.setVisible(false);
            }
        });

        chart.setOnMouseReleased(event -> {
            if (event.getButton() != MouseButton.PRIMARY)
                return;

            rect.setVisible(false);
            zoomOut.setVisible(false);

            double mouseX = event.getX();
            double mouseY = event.getY();
            double rectX = rect.getX();
            double rectY = rect.getY();
            if ((mouseX < rectX - 5.0) && (mouseY < rectY - 5.0)) {
                xAxis.setAutoRanging(true);
                yAxis.setAutoRanging(true);
                return;
            }

            if ((rect.getWidth() < 5) || (rect.getHeight() < 5)) {
                return;
            }

            xAxis.setAutoRanging(false);
            yAxis.setAutoRanging(false);
            Point2D xAxisInScene = xAxis.localToScene(0, 0);
            Point2D mouseInScene = chart.localToScene(rect.getX(), rect.getY());

            double xOffset = mouseInScene.getX() - xAxisInScene.getX();
            double yOffset = xAxisInScene.getY() - mouseInScene.getY();
            if (xOffset < 0)
                xOffset = 0;
            if (yOffset < 0)
                yOffset = 0;
            if (xOffset > xAxis.getWidth())
                xOffset = xAxis.getWidth();
            if (yOffset > yAxis.getHeight())
                yOffset = yAxis.getHeight();

            double xAxisScale = xAxis.getScale();
            double yAxisScale = yAxis.getScale();

            double newXLowerBound = xAxis.getLowerBound()
                    + xOffset / xAxisScale;
            double newXUpperBound = newXLowerBound
                    + rect.getWidth() / xAxisScale;
            double newYLowerBound = yAxis.getLowerBound()
                    - (yOffset - rect.getHeight()) / yAxisScale;
            double newYUpperBound = yAxis.getLowerBound()
                    - yOffset / yAxisScale;
            newYLowerBound = Math.max(newYLowerBound, 0.0);
            newYUpperBound = Math.max(newYUpperBound, newYLowerBound + 1.0);
            xAxis.setLowerBound(newXLowerBound);
            xAxis.setUpperBound(newXUpperBound);
            yAxis.setLowerBound(newYLowerBound);
            yAxis.setUpperBound(newYUpperBound);

            xAxis.setTickUnit((newXUpperBound - newXLowerBound) / 10);
            yAxis.setTickUnit((newYUpperBound - newYLowerBound) / 10);

            rect.setWidth(0);
            rect.setHeight(0);

        });

    }

}