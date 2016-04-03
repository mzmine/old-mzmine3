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

package io.github.mzmine.modules.plots.chromatogram.datasets;

import java.text.NumberFormat;

import org.jfree.data.xy.AbstractXYDataset;
import org.jfree.data.xy.XYDataset;

import io.github.msdk.datamodel.chromatograms.Chromatogram;
import io.github.msdk.datamodel.rawdata.ChromatographyInfo;
import io.github.msdk.datamodel.rawdata.MsScan;
import io.github.msdk.util.ChromatogramUtil;
import io.github.mzmine.main.MZmineCore;
import io.github.mzmine.modules.plots.chromatogram.ChromatogramPlotDataSet;
import io.github.mzmine.util.MsScanUtils;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;

/**
 * Chromatogram data set, based on MSDK Chromatogram.
 */
public class ChromatogramDataSet extends AbstractXYDataset
        implements ChromatogramPlotDataSet {

    private Chromatogram chromatogram;
    private double mzValues[];
    private ChromatographyInfo rtValues[];
    private float intensityValues[];
    private float topIndensity = 0f;
    private int numOfDataPoints = 0;

    private final StringProperty name = new SimpleStringProperty(this, "name",
            "MS chromatogram");
    private final DoubleProperty intensityScale = new SimpleDoubleProperty(this,
            "intensityScale", 0.0);
    private final IntegerProperty lineThickness = new SimpleIntegerProperty(
            this, "lineThickness", 1);
    private final ObjectProperty<Color> color = new SimpleObjectProperty<>(this,
            "color", Color.BLUE);
    private final BooleanProperty showDataPoints = new SimpleBooleanProperty(
            this, "showDataPoints", false);

    public ChromatogramDataSet(Chromatogram chromatogram, String datasetName) {

        // Turn notify to off, to avoid redrawing the plot after each
        // property change
        setNotify(false);

        // Listen for property changes
        intensityScale.addListener(e -> {
            fireDatasetChanged();
        });
        name.addListener(e -> {
            fireDatasetChanged();
        });

        // Remember if the current intensity scale was modified
        boolean modifiedIntensityScale = (getIntensityScale() != this.topIndensity);

        this.chromatogram = chromatogram;
        this.mzValues = chromatogram.getMzValues();
        this.rtValues = chromatogram.getRetentionTimes();
        this.intensityValues = chromatogram.getIntensityValues();
        this.numOfDataPoints = chromatogram.getNumberOfDataPoints();
        this.topIndensity = ChromatogramUtil.getMaxHeight(intensityValues,
                numOfDataPoints);

        // If the intensity scale was not modified by the user, set the new
        // scale to max intensity
        if (!modifiedIntensityScale)
            setIntensityScale((double) topIndensity);

        setName(datasetName);

        // Finally, update the GUI
        setNotify(true);

    }

    public String getDescription() {

        if (chromatogram == null)
            return null;

        StringBuilder sb = new StringBuilder();
        if (chromatogram instanceof MsScan) {
            MsScan scan = (MsScan) chromatogram;
            String scanDesc = MsScanUtils.createFullMsScanDescription(scan);
            sb.append(scanDesc);
        }

        NumberFormat intensityFormat = MZmineCore.getConfiguration()
                .getIntensityFormat();
        NumberFormat mzFormat = MZmineCore.getConfiguration().getMZFormat();

        sb.append("Chromatogram type: ");
        sb.append(chromatogram.getChromatogramType());
        sb.append("\n");
        sb.append("Number of data points: ");
        sb.append(numOfDataPoints);
        sb.append("\n");
        Double mz = chromatogram.getMz();
        if (mz != null) {
            sb.append("m/z: ");
            sb.append(mzFormat.format(mz));
            sb.append(" m/z\n");
        }
        sb.append("Base peak intensity: ");
        sb.append(intensityFormat.format(topIndensity));
        sb.append("\n");

        return sb.toString();
    }

    public Chromatogram getChromatogram() {
        return chromatogram;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String newName) {
        name.set(newName);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public Double getIntensityScale() {
        return intensityScale.get();
    }

    public void setIntensityScale(Double newIntensityScale) {
        intensityScale.set(newIntensityScale);
    }

    public DoubleProperty intensityScaleProperty() {
        return intensityScale;
    }

    public void resetIntensityScale() {
        setIntensityScale((double) topIndensity);
    }

    public Integer getLineThickness() {
        return lineThickness.get();
    }

    public void setLineThickness(Integer newLineThickness) {
        lineThickness.set(newLineThickness);
    }

    public IntegerProperty lineThicknessProperty() {
        return lineThickness;
    }

    public Boolean getShowDataPoints() {
        return showDataPoints.get();
    }

    public void setShowDataPoints(Boolean newShowDataPoints) {
        showDataPoints.set(newShowDataPoints);
    }

    public BooleanProperty showDataPointsProperty() {
        return showDataPoints;
    }

    public Color getColor() {
        return color.get();
    }

    public void setColor(Color newColor) {
        color.set(newColor);
    }

    public ObjectProperty<Color> colorProperty() {
        return color;
    }

    @Override
    public int getItemCount(int series) {
        return numOfDataPoints;
    }

    @Override
    public Number getX(int series, int index) {
        ChromatographyInfo rt = rtValues[index];
        if (rt == null)
            return null;
        return rt.getRetentionTime() / 60f;
    }

    @Override
    public Number getY(int series, int index) {
        return intensityValues[index] * (getIntensityScale() / topIndensity);
    }

    @Override
    public int getSeriesCount() {
        return 1;
    }

    @Override
    public Comparable getSeriesKey(int series) {
        return getName();
    }

    @Override
    public String generateLabel(XYDataset ds, int series, int index) {
        final double mz = mzValues[index];
        NumberFormat mzFormat = MZmineCore.getConfiguration().getMZFormat();
        String label = mzFormat.format(mz);
        return label;
    }

    @Override
    public String generateToolTip(XYDataset ds, int series, int index) {
        final double actualMz = mzValues[index];
        final Float actualRt = rtValues[index].getRetentionTime();
        final float scaledIntensity = getY(series, index).floatValue();
        final float actualIntensity = intensityValues[index];
        NumberFormat rtFormat = MZmineCore.getConfiguration().getRTFormat();
        NumberFormat mzFormat = MZmineCore.getConfiguration().getMZFormat();
        NumberFormat intensityFormat = MZmineCore.getConfiguration()
                .getIntensityFormat();
        StringBuilder sb = new StringBuilder();

        sb.append("Data point RT: ");
        sb.append(rtFormat.format(actualRt));
        sb.append("\n");

        sb.append("Data point m/z: ");
        sb.append(mzFormat.format(actualMz));
        sb.append("\n");

        if (intensityScale.get() != topIndensity) {
            sb.append("Scaled intensity: ");
            sb.append(intensityFormat.format(scaledIntensity));
            sb.append("\n");
        }

        sb.append("Data point intensity: ");
        sb.append(intensityFormat.format(actualIntensity));
        return sb.toString();

    }

}