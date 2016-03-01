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

package io.github.mzmine.util.jfreechart;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.fx.ChartViewer;
import org.jfree.chart.plot.PlotOrientation;

/**
 * This adds a no-parameter constructor to ChartViewer, so it can be constructed
 * from FXML. Should be unnecessary once JFreeChart 1.0.20 is released.
 */
public class ChartNodeJFreeChart extends ChartViewer {

    public ChartNodeJFreeChart() {

        super(ChartFactory.createXYLineChart("", // title
                "", // x-axis label
                "", // y-axis label
                null, // data set
                PlotOrientation.VERTICAL, // orientation
                true, // create legend?
                true, // generate tooltips?
                false // generate URLs?
        ), false);

    }

}