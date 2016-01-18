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

import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.data.xy.AbstractXYDataset;
import org.jfree.data.xy.XYDataset;

import io.github.mzmine.util.charts.ChartDataSet;

/**
 * Chart using JfreeChart library
 */
class XYDataSetWrapper extends AbstractXYDataset
        implements XYItemLabelGenerator, XYToolTipGenerator {

    private final ChartDataSet dataSet;

    XYDataSetWrapper(ChartDataSet dataSet) {
        this.dataSet = dataSet;
    }

    @Override
    public int getItemCount(int series) {
        return dataSet.getNumOfDataPoints();
    }

    @Override
    public Number getX(int series, int index) {
        return dataSet.getX(index);
    }

    @Override
    public Number getY(int series, int index) {
        return dataSet.getY(index);
    }

    @Override
    public int getSeriesCount() {
        return 1;
    }

    @Override
    public Comparable getSeriesKey(int series) {
        return dataSet.getName();
    }

    @Override
    public String generateLabel(XYDataset ds, int series, int index) {
        return dataSet.getLabel(index);
    }

    @Override
    public String generateToolTip(XYDataset ds, int series, int index) {
        return dataSet.getToolTip(index);
    }

}