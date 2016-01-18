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
 * MZmine 3; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

package io.github.mzmine.util.charts;

import io.github.mzmine.gui.preferences.MZminePreferences;
import io.github.mzmine.main.MZmineCore;
import io.github.mzmine.util.charts.javafxcharts.ChartNodeJavaFX;
import io.github.mzmine.util.charts.jfreechart.ChartNodeJFreeChart;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

/**
 * MZmine chart component
 */
public class MZmineChartViewer extends BorderPane {

    private final Node chart;

    public MZmineChartViewer() {

        ChartLibrary selectedLibrary = MZmineCore.getConfiguration()
                .getPreferences().getParameter(MZminePreferences.chartLibrary)
                .getValue();
        if (selectedLibrary == null)
            selectedLibrary = ChartLibrary.JFREECHART;

        switch (selectedLibrary) {
        case JAVAFX:
            ChartNodeJavaFX chartJavaFX = new ChartNodeJavaFX();
            onContextMenuRequestedProperty().bindBidirectional(
                    chartJavaFX.onContextMenuRequestedProperty());
            this.chart = chartJavaFX;
            break;
        default:
        case JFREECHART:
            ChartNodeJFreeChart chartJFree = new ChartNodeJFreeChart();
            onContextMenuRequestedProperty().bindBidirectional(
                    chartJFree.onContextMenuRequestedProperty());
            this.chart = chartJFree;
            break;
        }

        setCenter(chart);

    }

    public void addDataSet(ChartDataSet newDataSet) {

        if (chart instanceof ChartNodeJavaFX) {
            ChartNodeJavaFX javaFXNode = (ChartNodeJavaFX) chart;
            javaFXNode.addDataSet(newDataSet);
        } else if (chart instanceof ChartNodeJFreeChart) {
            ChartNodeJFreeChart jFreeChartNode = (ChartNodeJFreeChart) chart;
            jFreeChartNode.addDataSet(newDataSet);
        }
    }

}