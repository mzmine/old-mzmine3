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

package io.github.mzmine.modules.featuretable.renderers;

import io.github.msdk.datamodel.chromatograms.Chromatogram;
import io.github.msdk.datamodel.chromatograms.ChromatogramDataPointList;
import io.github.msdk.datamodel.featuretables.FeatureTableRow;
import io.github.msdk.datamodel.impl.MSDKObjectBuilder;
import io.github.msdk.datamodel.rawdata.ChromatographyInfo;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.util.Callback;

public class ChromatogramRenderer implements
        Callback<TreeTableColumn<FeatureTableRow, Object>, TreeTableCell<FeatureTableRow, Object>> {

    @Override
    public TreeTableCell<FeatureTableRow, Object> call(
            TreeTableColumn<FeatureTableRow, Object> p) {
        return new TreeTableCell<FeatureTableRow, Object>() {
            @Override
            public void updateItem(Object object, boolean empty) {
                super.updateItem(object, empty);
                setStyle(
                        "-fx-border-color: transparent -fx-table-cell-border-color -fx-table-cell-border-color transparent;");
                if (object == null) {
                    setText(null);
                } else {
                    // Get the data point list
                    Chromatogram chromatogram = (Chromatogram) object;
                    ChromatogramDataPointList dataPointList = MSDKObjectBuilder
                            .getChromatogramDataPointList();
                    chromatogram.getDataPoints(dataPointList);

                    ChromatographyInfo[] chromatographyInfoValues = dataPointList
                            .getRtBuffer();
                    float[] intensityValues = dataPointList
                            .getIntensityBuffer();

                    // x-axis
                    NumberAxis xAxis = new NumberAxis();
                    xAxis.setTickLabelsVisible(false);
                    xAxis.setOpacity(0);
                    xAxis.setAutoRanging(false);
                    xAxis.setLowerBound(dataPointList.getRtRange()
                            .lowerEndpoint().getRetentionTime());
                    xAxis.setUpperBound(dataPointList.getRtRange()
                            .upperEndpoint().getRetentionTime());

                    // y-axis
                    NumberAxis yAxis = new NumberAxis();
                    yAxis.setTickLabelsVisible(false);
                    yAxis.setOpacity(0);

                    // Chart line
                    final LineChart<Number, Number> lineChart = new LineChart<Number, Number>(
                            xAxis, yAxis);
                    XYChart.Series series = new XYChart.Series();
                    for (int i = 1; i < dataPointList.getSize(); i++) {
                        series.getData()
                                .add(new XYChart.Data(
                                        chromatographyInfoValues[i]
                                                .getRetentionTime(),
                                        intensityValues[i]));
                    }

                    // Chart
                    lineChart.getData().addAll(series);
                    lineChart.setLegendVisible(false);
                    lineChart.setCreateSymbols(false);
                    lineChart.setMinSize(0, 0);
                    lineChart.setPrefHeight(75);
                    lineChart.setPrefWidth(100);

                    setGraphic(lineChart);
                }
            }
        };
    }

}
