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

package io.github.mzmine.modules.plots.msspectrum;

import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYDataset;

import io.github.msdk.datamodel.MsSpectrumType;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;

/**
 * MS spectrum data set. Implements IntervalXYDataset for centroid spectra support (rendered by
 * XYBarRenderer).
 */
public interface MsSpectrumPlotDataSet
    extends XYDataset, XYItemLabelGenerator, XYToolTipGenerator, IntervalXYDataset {

  String getDescription();

  String getName();

  void setName(String newName);

  StringProperty nameProperty();

  Double getIntensityScale();

  void setIntensityScale(Double newIntensityScale);

  DoubleProperty intensityScaleProperty();

  void resetIntensityScale();

  Integer getLineThickness();

  void setLineThickness(Integer newLineThickness);

  IntegerProperty lineThicknessProperty();

  Boolean getShowDataPoints();

  void setShowDataPoints(Boolean newShowDataPoints);

  BooleanProperty showDataPointsProperty();

  MsSpectrumType getRenderingType();

  void setRenderingType(MsSpectrumType newType);

  ObjectProperty<MsSpectrumType> renderingTypeProperty();

  Color getColor();

  void setColor(Color newColor);

  ObjectProperty<Color> colorProperty();

}
