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

import java.awt.Color;
import java.text.NumberFormat;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.fx.ChartViewer;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.AbstractXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleInsets;

import io.github.msdk.datamodel.msspectra.MsSpectrum;
import io.github.msdk.datamodel.msspectra.MsSpectrumType;
import io.github.msdk.datamodel.rawdata.MsScan;
import io.github.msdk.datamodel.rawdata.RawDataFile;
import io.github.mzmine.main.MZmineCore;
import javafx.scene.layout.BorderPane;

/**
 * Chart using JfreeChart library
 */
public class ChartNodeJFreeChart extends BorderPane implements ChartNode {

    // grid color
    private static final Color gridColor = Color.lightGray;

    private final ChartViewer chartNode;
    private final JFreeChart chart;
    private final XYPlot plot;
    private int numberOfDataSets = 0;

    ChartNodeJFreeChart() {

        // initialize the chart by default time series chart from factory
        chart = ChartFactory.createXYLineChart("", // title
                "m/z", // x-axis label
                "Intensity", // y-axis label
                null, // data set
                PlotOrientation.VERTICAL, // orientation
                true, // create legend?
                true, // generate tooltips?
                false // generate URLs?
        );
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

        chartNode = new ChartViewer(chart);

        // set focusable state to receive key events
        chartNode.setFocusTraversable(true);

        setCenter(chartNode);

    }

    public synchronized void addSpectrum(MsSpectrum spectrum) {

        String spectrumTitle = "MS spectrum";
        if (spectrum instanceof MsScan) {
            MsScan scan = (MsScan) spectrum;
            RawDataFile dataFile = scan.getRawDataFile();
            if (dataFile != null)
                spectrumTitle += " " + dataFile.getName();
            spectrumTitle += "#" + scan.getScanNumber();
        }
        final String seriesKey = spectrumTitle;

        double mzValues[] = spectrum.getMzValues();
        float intensityValues[] = spectrum.getIntensityValues();
        final boolean centroided = spectrum
                .getSpectrumType() == MsSpectrumType.CENTROIDED;

        XYDataset dataset = new AbstractXYDataset() {

            @Override
            public Number getY(int arg0, int arg1) {
                return intensityValues[arg1];
            }

            @Override
            public Number getX(int arg0, int arg1) {
                return mzValues[arg1];
            }

            @Override
            public int getItemCount(int arg0) {
                return mzValues.length;
            }

            @Override
            public Comparable getSeriesKey(int arg0) {
                return seriesKey;
            }

            @Override
            public int getSeriesCount() {
                return 1;
            }
        };

        plot.setDataset(numberOfDataSets, dataset);
        plot.getRenderer().setBaseItemLabelGenerator(new SpectraItemLabelGenerator());
        plot.getRenderer().setBaseItemLabelsVisible(true);
        numberOfDataSets++;

    }

    class SpectraItemLabelGenerator implements XYItemLabelGenerator {

        /*
         * Number of screen pixels to reserve for each label, so that the labels
         * do not overlap
         */
        public static final int POINTS_RESERVE_X = 100;

        /**
         * @see org.jfree.chart.labels.XYItemLabelGenerator#generateLabel(org.jfree.data.xy.XYDataset,
         *      int, int)
         */
        public String generateLabel(XYDataset dataset, int series, int item) {
            
            // X and Y values of current data point
            double originalX = dataset.getX(series, item).doubleValue();
            double originalY = dataset.getY(series, item).doubleValue();

            // Calculate data size of 1 screen pixel
            double xLength = (double) plot.getDomainAxis().getRange()
                    .getLength();
            double pixelX = xLength / chartNode.getWidth();

            // Size of data set
            int itemCount = dataset.getItemCount(series);

            // Search for data points higher than this one in the interval
            // from limitLeft to limitRight
            double limitLeft = originalX - ((POINTS_RESERVE_X / 2) * pixelX);
            double limitRight = originalX + ((POINTS_RESERVE_X / 2) * pixelX);

            // Iterate data points to the left and right
            for (int i = 1; (item - i > 0) || (item + i < itemCount); i++) {

                // If we get out of the limit we can stop searching
                if ((item - i > 0)
                        && (dataset.getXValue(series, item - i) < limitLeft)
                        && ((item + i >= itemCount) || (dataset
                                .getXValue(series, item + i) > limitRight)))
                    break;

                if ((item + i < itemCount)
                        && (dataset.getXValue(series, item + i) > limitRight)
                        && ((item - i <= 0) || (dataset.getXValue(series,
                                item - i) < limitLeft)))
                    break;

                // If we find higher data point, bail out
                if ((item - i > 0)
                        && (originalY <= dataset.getYValue(series, item - i)))
                    return null;

                if ((item + i < itemCount)
                        && (originalY <= dataset.getYValue(series, item + i)))
                    return null;

            }

            // Create label
            String label = null;
            if (label == null) {
                double mzValue = dataset.getXValue(series, item);
                NumberFormat mzFormat = MZmineCore.getConfiguration()
                        .getMZFormat();
                label = mzFormat.format(mzValue);
            }

            return label;

        }
    }

}