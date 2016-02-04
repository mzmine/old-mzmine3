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

package io.github.mzmine.util.charts.jfreechart;

import org.jfree.chart.fx.ChartViewer;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;

public class IntelligentItemLabelGenerator implements XYItemLabelGenerator {

    private final ChartViewer chartNode;
    private final XYPlot plot;
    private final XYItemLabelGenerator underlyingGenerator;
    private final int reservedPoints;

    /**
     * 
     * @param chartNode
     * @param plot
     * @param reservedPoints
     *            Number of screen pixels to reserve for each label, so that the
     *            labels do not overlap
     * @param underlyingGenerator
     */
    public IntelligentItemLabelGenerator(ChartViewer chartNode, XYPlot plot,
            int reservedPoints, XYItemLabelGenerator underlyingGenerator) {
        this.chartNode = chartNode;
        this.plot = plot;
        this.reservedPoints = reservedPoints;
        this.underlyingGenerator = underlyingGenerator;
    }

    /**
     * @see org.jfree.chart.labels.XYItemLabelGenerator#generateLabel(org.jfree.data.xy.XYDataset,
     *      int, int)
     */
    public String generateLabel(XYDataset currentDataset, int currentSeries,
            int currentItem) {

        // X and Y values of current data point
        final double currentXValue = currentDataset
                .getX(currentSeries, currentItem).doubleValue();
        final double currentYValue = currentDataset
                .getY(currentSeries, currentItem).doubleValue();

        // Calculate X axis span of 1 screen pixel
        final double xLength = (double) plot.getDomainAxis().getRange()
                .getLength();
        final double pixelX = xLength / chartNode.getWidth();

        // Calculate the distance from the current point where labels might
        // overlap
        final double dangerZoneX = (reservedPoints / 2) * pixelX;

        // Iterate through data sets
        for (int datasetIndex = 0; datasetIndex < plot
                .getDatasetCount(); datasetIndex++) {

            XYDataset dataset = plot.getDataset(datasetIndex);
            final int seriesCount = dataset.getSeriesCount();

            // Iterate through series
            for (int seriesIndex = 0; seriesIndex < seriesCount; seriesIndex++) {

                final int itemCount = dataset.getItemCount(seriesIndex);

                // Iterate through items
                for (int itemIndex = 0; itemIndex < itemCount; itemIndex++) {

                    // Skip the current data point
                    if ((dataset == currentDataset)
                            && (seriesIndex == currentSeries)
                            && (itemIndex == currentItem))
                        continue;

                    // Only check points in the danger zone
                    final double xValue = dataset.getXValue(seriesIndex,
                            itemIndex);
                    if (Math.abs(xValue - currentXValue) > dangerZoneX)
                        continue;

                    final double yValue = dataset.getYValue(seriesIndex,
                            itemIndex);

                    // If we find a higher data point, do not add a label to
                    // this one
                    if (yValue > currentYValue)
                        return null;

                    // If the values are equal, only add label to the left-most
                    // value
                    if ((yValue == currentYValue) && (currentXValue > xValue))
                        return null;

                }

            }
        }

        // Create label
        String label = underlyingGenerator.generateLabel(currentDataset,
                currentSeries, currentItem);

        return label;

    }

}
