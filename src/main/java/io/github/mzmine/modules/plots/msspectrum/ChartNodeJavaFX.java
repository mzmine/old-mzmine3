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

import io.github.msdk.datamodel.msspectra.MsSpectrum;
import io.github.msdk.datamodel.msspectra.MsSpectrumType;
import io.github.msdk.datamodel.rawdata.MsScan;
import io.github.msdk.datamodel.rawdata.RawDataFile;
import io.github.mzmine.main.MZmineCore;
import io.github.mzmine.util.JavaFXUtil;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

/**
 * Chart using JavaFX charts library
 */
public class ChartNodeJavaFX extends BorderPane implements ChartNode {

    private final NumberAxis xAxis, yAxis;
    private final LineChart<Number, Number> lineChart;

    ChartNodeJavaFX() {

        xAxis = new NumberAxis();
        yAxis = new NumberAxis();

        xAxis.setLabel("m/z");
        yAxis.setLabel("Intensity");

        lineChart = new LineChart<>(xAxis, yAxis);

        lineChart.setTitle("MS spectrum");
        lineChart.setCreateSymbols(false);

        Node zoomedChart = JavaFXUtil.addZoomSupport(lineChart);

        setCenter(zoomedChart);

    }

    public synchronized void addSpectrum(MsSpectrum spectrum) {

        XYChart.Series<Number, Number> newSeries = new XYChart.Series<>();

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
            String labelText = MZmineCore.getConfiguration().getMZFormat()
                    .format(mz);
            Text labelNode = new Text(labelText);
            newData.setNode(labelNode);
            if (centroided) {
                XYChart.Data<Number, Number> zeroPoint = new XYChart.Data<>(mz,
                        0.0);
                newSeries.getData().add(zeroPoint);
            }
            newSeries.getData().add(newData);
            if (centroided) {
                XYChart.Data<Number, Number> zeroPoint = new XYChart.Data<>(mz,
                        0.0);
                newSeries.getData().add(zeroPoint);
            }

        }

        xAxis.lowerBoundProperty().addListener(e -> {
            // updateLabels();
        });

        lineChart.setStyle("-fx-stroke-width: 5px");
        lineChart.getData().add(newSeries);

    }

    private void updateLabels() {

        System.out.println("zoomed");
        for (XYChart.Series<Number, Number> series : lineChart.getData()) {
            for (XYChart.Data<Number, Number> data : series.getData()) {
                double mz = data.getXValue().doubleValue();
                float intensity = data.getYValue().floatValue();
                if (intensity == 0f)
                    continue;
                String tooltipText = "m/z: "
                        + MZmineCore.getConfiguration().getMZFormat().format(mz)
                        + "\\nIntensity: " + MZmineCore.getConfiguration()
                                .getIntensityFormat().format(intensity);
                Text labelNode = new Text("Test");
                System.out.println(data.getNode());
                data.setNode(labelNode);
                Tooltip.install(data.getNode(), new Tooltip(tooltipText));
                System.out.println(
                        "adding label to mz  " + mz + " " + tooltipText);
            }
            // series.setNode(new Text("seriesXX"));
            // lineChart.getData().remove(series);
        }

    }
}