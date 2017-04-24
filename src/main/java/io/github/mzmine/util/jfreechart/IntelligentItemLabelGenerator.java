/*
 * Copyright 2006-2015 The MZmine 3 Development Team
 * 
 * This file is part of MZmine 3.
 * 
 * MZmine 3 is free software; you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * MZmine 3 is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with MZmine 3; if not,
 * write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301
 * USA
 */

package io.github.mzmine.util.jfreechart;

import org.jfree.chart.fx.ChartViewer;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;

import com.google.common.collect.Range;

/**
 * This implementation of XYItemLabelGenerator assumes that the data points in each series are
 * sorted in the X-axis order. It places the item labels only on local maxima, or on left-most data
 * points in case multiple data points have the same maximal Y value.
 *
 */
public class IntelligentItemLabelGenerator implements XYItemLabelGenerator {

  private final ChartViewer chartNode;

  private final XYItemLabelGenerator underlyingGenerator;
  private final int reservedPixels;

  /**
   * 
   * @param chartNode
   * @param plot
   * @param reservedPoints Number of screen pixels to reserve for each label, so that the labels do
   *        not overlap
   * @param underlyingGenerator
   */
  public IntelligentItemLabelGenerator(ChartViewer chartNode, int reservedPixels,
      XYItemLabelGenerator underlyingGenerator) {
    this.chartNode = chartNode;

    this.reservedPixels = reservedPixels;
    this.underlyingGenerator = underlyingGenerator;
  }

  /**
   * @see org.jfree.chart.labels.XYItemLabelGenerator#generateLabel(org.jfree.data.xy.XYDataset,
   *      int, int)
   */
  public String generateLabel(XYDataset currentDataset, int currentSeries, int currentItem) {

    XYPlot plot = chartNode.getChart().getXYPlot();

    // X and Y values of the current data point
    final double currentXValue = currentDataset.getXValue(currentSeries, currentItem);
    final double currentYValue = currentDataset.getYValue(currentSeries, currentItem);

    // Calculate X axis span of 1 screen pixel
    final double xLength = plot.getDomainAxis().getRange().getLength();
    final double pixelX = xLength / chartNode.getWidth();

    // Calculate the distance from the current point where labels might
    // overlap
    final double dangerZoneX = (reservedPixels / 2) * pixelX;

    // Range on X axis that we're going to check for higher data points. If
    // a higher data point is found, we don't place a label on this one.
    final Range<Double> dangerZoneRange =
        Range.closed(currentXValue - dangerZoneX, currentXValue + dangerZoneX);

    // Iterate through data sets
    for (int datasetIndex = 0; datasetIndex < plot.getDatasetCount(); datasetIndex++) {

      XYDataset dataset = plot.getDataset(datasetIndex);

      // Some data sets could have been removed
      if (dataset == null)
        continue;

      final int seriesCount = dataset.getSeriesCount();

      // Iterate through series
      for (int seriesIndex = 0; seriesIndex < seriesCount; seriesIndex++) {

        final int itemCount = dataset.getItemCount(seriesIndex);

        // Find the index of a data point that is closest to
        // currentXValue
        int closestValueIndex;
        if (dataset == currentDataset && seriesIndex == currentSeries) {
          closestValueIndex = currentItem;
        } else {
          closestValueIndex =
              findClosestXIndex(dataset, seriesIndex, currentXValue, 0, itemCount - 1);
        }

        // Search to the left of the closest data point
        for (int i = closestValueIndex; (i >= 0)
            && (dangerZoneRange.contains(dataset.getX(seriesIndex, i).doubleValue())); i--) {
          if (dataset.getYValue(seriesIndex, i) > currentYValue)
            return null;

          // In the case there are equal values, only place the label
          // on the leftmost value
          if (dataset.getYValue(seriesIndex, i) == currentYValue
              && (dataset.getXValue(seriesIndex, i) < currentXValue))
            return null;

        }

        // Search to the right of the closest data point
        for (int i = closestValueIndex + 1; (i < itemCount)
            && (dangerZoneRange.contains(dataset.getX(seriesIndex, i).doubleValue())); i++) {
          if (dataset.getYValue(seriesIndex, i) > currentYValue)
            return null;
        }

      }

    }

    // If no higher data point was found, create the label
    String label = underlyingGenerator.generateLabel(currentDataset, currentSeries, currentItem);

    return label;

  }

  /**
   * Finds the data point in given dataset/series that is closest to the given xValue. Uses
   * recursion and binary search , minIndex and maxIndex provide the boundaries of the currently
   * examined region.
   */
  private int findClosestXIndex(XYDataset dataset, int series, double xValue, int minIndex,
      int maxIndex) {

    if (minIndex == maxIndex)
      return minIndex;

    if (minIndex == maxIndex - 1) {
      double minIndexValue = dataset.getXValue(series, minIndex);
      double maxIndexValue = dataset.getXValue(series, maxIndex);

      if (Math.abs(minIndexValue - xValue) < Math.abs(maxIndexValue - xValue))
        return minIndex;
      else
        return maxIndex;
    }

    int middleIndex = (maxIndex + minIndex) / 2;
    double middleValue = dataset.getXValue(series, middleIndex);
    if (middleValue == xValue)
      return middleIndex;
    else if (middleValue > xValue)
      return findClosestXIndex(dataset, series, xValue, minIndex, middleIndex);
    else
      return findClosestXIndex(dataset, series, xValue, middleIndex, maxIndex);

  }

}
