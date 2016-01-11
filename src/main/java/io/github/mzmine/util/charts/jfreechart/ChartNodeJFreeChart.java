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

package io.github.mzmine.util.charts.jfreechart;

import java.awt.Color;
import java.text.NumberFormat;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.fx.ChartViewer;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleInsets;

import io.github.mzmine.main.MZmineCore;
import io.github.mzmine.util.charts.ChartDataSet;

/**
 * Chart using JfreeChart library
 */
public class ChartNodeJFreeChart extends ChartViewer {

    // grid color
    private static final Color gridColor = Color.lightGray;

    private final JFreeChart chart;
    private final XYPlot plot;
    private int numberOfDataSets = 0;

    public ChartNodeJFreeChart() {

        super(ChartFactory.createXYLineChart("", // title
                "m/z", // x-axis label
                "Intensity", // y-axis label
                null, // data set
                PlotOrientation.VERTICAL, // orientation
                true, // create legend?
                true, // generate tooltips?
                false // generate URLs?
        ));

        this.chart = getChart();

        chart.setBackgroundPaint(Color.white);

        NumberFormat mzFormat = MZmineCore.getConfiguration().getMZFormat();
        NumberFormat intensityFormat = MZmineCore.getConfiguration()
                .getIntensityFormat();

        // set the plot properties
        plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.white);
        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));

        // set rendering order
        plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);

        // set grid properties
        plot.setDomainGridlinePaint(gridColor);
        plot.setRangeGridlinePaint(gridColor);

        // set crosshair (selection) properties
        plot.setDomainCrosshairVisible(false);
        plot.setRangeCrosshairVisible(false);

        // set the X axis (retention time) properties
        NumberAxis xAxis = (NumberAxis) plot.getDomainAxis();
        xAxis.setNumberFormatOverride(mzFormat);
        xAxis.setUpperMargin(0.001);
        xAxis.setLowerMargin(0.001);
        xAxis.setTickLabelInsets(new RectangleInsets(0, 0, 20, 20));

        // set the Y axis (intensity) properties
        NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
        yAxis.setNumberFormatOverride(intensityFormat);

        // set focusable state to receive key events
        setFocusTraversable(true);

    }

    public synchronized void addDataSet(ChartDataSet newDataSet) {

        XYDataset dataset = new XYDataSetWrapper(newDataSet);
        plot.setDataset(numberOfDataSets, dataset);
        ItemLabelGenerator labelGen = new ItemLabelGenerator(this, plot);
        plot.getRenderer().setBaseItemLabelGenerator(labelGen);
        plot.getRenderer().setBaseItemLabelsVisible(true);
        numberOfDataSets++;

    }

}