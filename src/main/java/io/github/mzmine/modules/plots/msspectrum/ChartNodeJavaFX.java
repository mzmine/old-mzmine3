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

package io.github.mzmine.modules.plots.msspectrum;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import io.github.msdk.datamodel.msspectra.MsSpectrum;
import io.github.msdk.datamodel.msspectra.MsSpectrumType;
import io.github.msdk.datamodel.rawdata.MsScan;
import io.github.msdk.datamodel.rawdata.RawDataFile;
import io.github.mzmine.main.MZmineCore;
import io.github.mzmine.util.JavaFXUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.util.StringConverter;

/**
 * Chart using JavaFX charts library
 */
public class ChartNodeJavaFX extends BorderPane implements ChartNode {

    private final NumberAxis xAxis, yAxis;
    private final LineChart<Number, Number> lineChart;

    ChartNodeJavaFX() {

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

        xAxis.setLabel("m/z");
        yAxis.setLabel("Intensity");

        lineChart = new LineChart<>(xAxis, yAxis);

        lineChart.setTitle("MS spectrum");
        lineChart.setCreateSymbols(false);
        lineChart.setAnimated(false);

        Node zoomedChart = JavaFXUtil.addZoomSupport(lineChart);

        setCenter(zoomedChart);

    }

    public synchronized void addSpectrum(MsSpectrum spectrum) {

        XYChart.Series<Number, Number> newSeries = new XYChart.Series<>();
        ObservableList<Data<Number, Number>> dataSet = FXCollections
                .<Data<Number, Number>> observableArrayList();

        String spectrumTitle = "MS spectrum";
        if (spectrum instanceof MsScan) {
            MsScan scan = (MsScan) spectrum;
            RawDataFile dataFile = scan.getRawDataFile();
            if (dataFile != null)
                spectrumTitle += " " + dataFile.getName();
            spectrumTitle += "#" + scan.getScanNumber();
        }
        newSeries.setName(spectrumTitle);

        double mzValues[] = spectrum.getMzValues();
        float intensityValues[] = spectrum.getIntensityValues();
        final boolean centroided = spectrum
                .getSpectrumType() == MsSpectrumType.CENTROIDED;

        for (int i = 0; i < spectrum.getNumberOfDataPoints(); i++) {
            final float intensity = intensityValues[i];
            final double mz = mzValues[i];
            XYChart.Data<Number, Number> newData = new XYChart.Data<>(mz,
                    intensity);

            Text labelNode = new Text();
            labelNode.getStyleClass().add("chart-item-label");
            newData.setNode(labelNode);

            if (centroided) {
                XYChart.Data<Number, Number> zeroPoint = new XYChart.Data<>(mz,
                        0.0);
                dataSet.add(zeroPoint);
            }
            dataSet.add(newData);
            if (centroided) {
                XYChart.Data<Number, Number> zeroPoint = new XYChart.Data<>(mz,
                        0.0);
                dataSet.add(zeroPoint);
            }

        }

        // Add data set to the series
        newSeries.setData(dataSet);

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
}