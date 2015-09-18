/*
 * Copyright 2006-2015 The MZmine 2 Development Team
 * 
 * This file is part of MZmine 2.
 * 
 * MZmine 2 is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * MZmine 2 is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * MZmine 2; if not, write to the Free Software Foundation, Inc., 51 Franklin
 * St, Fifth Floor, Boston, MA 02110-1301 USA
 */

package io.github.mzmine.modules.plots.chromatogram;

import io.github.mzmine.util.JavaFXUtil;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.BorderPane;

/**
 * Chromatogram plot window
 */
public class ChromatogramPlotWindow extends BorderPane {

    ChromatogramPlotWindow() {

        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();

        xAxis.setLabel("Month");
        final LineChart<Number, Number> lineChart = new LineChart<>(
                xAxis, yAxis);

        lineChart.setTitle("Stock Monitoring, 2010");
        lineChart.setCreateSymbols(false);
        XYChart.Series series1 = new XYChart.Series();
        series1.setName("Portfolio 1");

        series1.getData().add(new XYChart.Data(10, 23));
        series1.getData().add(new XYChart.Data(12, 14));
        series1.getData().add(new XYChart.Data(13, 15));
        series1.getData().add(new XYChart.Data(14, 24));
        series1.getData().add(new XYChart.Data(15, 34));
        series1.getData().add(new XYChart.Data(18, 36));
        series1.getData().add(new XYChart.Data(19, 22));
        series1.getData().add(new XYChart.Data(22, 45));
        series1.getData().add(new XYChart.Data(23, 43));
        series1.getData().add(new XYChart.Data(26, 17));
        series1.getData().add(new XYChart.Data(27, 29));
        series1.getData().add(new XYChart.Data(28, 25));


        lineChart.getData().addAll(series1);

        
        
        Node zoomedChart = JavaFXUtil.addZoomSupport(lineChart);
        
        setCenter(zoomedChart);

    }

}