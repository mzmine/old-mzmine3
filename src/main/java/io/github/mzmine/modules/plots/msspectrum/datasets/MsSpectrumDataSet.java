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

package io.github.mzmine.modules.plots.msspectrum.datasets;

import java.text.NumberFormat;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.jfree.data.xy.AbstractXYDataset;
import org.jfree.data.xy.XYDataset;

import com.google.common.collect.Range;

import io.github.msdk.datamodel.msspectra.MsSpectrum;
import io.github.msdk.datamodel.msspectra.MsSpectrumType;
import io.github.msdk.datamodel.rawdata.MsScan;
import io.github.msdk.spectra.splash.SplashCalculationAlgorithm;
import io.github.msdk.util.MsSpectrumUtil;
import io.github.mzmine.main.MZmineCore;
import io.github.mzmine.modules.plots.msspectrum.MsSpectrumPlotDataSet;
import io.github.mzmine.util.MsScanUtils;
import javafx.application.Platform;
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
 * MS spectrum data set. Implements IntervalXYDataset for centroid spectra
 * support (rendered by XYBarRenderer).
 */
public class MsSpectrumDataSet extends AbstractXYDataset
        implements MsSpectrumPlotDataSet {

    private static final ScheduledThreadPoolExecutor threadPool = new ScheduledThreadPoolExecutor(
            1);

    private MsSpectrum spectrum;
    private double mzValues[];
    private float intensityValues[];
    private float topIndensity = 0f;
    private int numOfDataPoints = 0;

    private final StringProperty name = new SimpleStringProperty(this, "name",
            "MS spectrum");
    private final DoubleProperty intensityScale = new SimpleDoubleProperty(this,
            "intensityScale", 0.0);
    private final DoubleProperty mzShift = new SimpleDoubleProperty(this,
            "mzShift", 0.0);
    private final IntegerProperty lineThickness = new SimpleIntegerProperty(
            this, "lineThickness", 1);
    private final ObjectProperty<MsSpectrumType> renderingType = new SimpleObjectProperty<>(
            this, "renderingType", MsSpectrumType.CENTROIDED);
    private final ObjectProperty<Color> color = new SimpleObjectProperty<>(this,
            "color", Color.BLUE);
    private final BooleanProperty showDataPoints = new SimpleBooleanProperty(
            this, "showDataPoints", false);

    public MsSpectrumDataSet(MsSpectrum spectrum, String datasetName) {

        // Listen for property changes
        mzShift.addListener(e -> {
            fireDatasetChanged();
        });
        intensityScale.addListener(e -> {
            fireDatasetChanged();
        });
        name.addListener(e -> {
            fireDatasetChanged();
        });

        setSpectrum(spectrum, datasetName);

    }

    public void setSpectrum(MsSpectrum spectrum, String datasetName) {

        // Load the actual data in a separate thread to avoid blocking the GUI
        threadPool.execute(() -> {

            // Turn notify to off, to avoid redrawing the plot after each
            // property change
            setNotify(false);

            // Remember if the current intensity scale was modified
            boolean modifiedIntensityScale = (getIntensityScale() != this.topIndensity);

            this.spectrum = spectrum;
            this.mzValues = spectrum.getMzValues();
            this.intensityValues = spectrum.getIntensityValues();
            this.numOfDataPoints = spectrum.getNumberOfDataPoints();
            this.topIndensity = MsSpectrumUtil.getMaxIntensity(intensityValues,
                    numOfDataPoints);

            // If the intensity scale was not modified by the user, set the new
            // scale to max intensity
            if (!modifiedIntensityScale)
                setIntensityScale((double) topIndensity);

            setName(datasetName);
            renderingType.get();
            setRenderingType(spectrum.getSpectrumType());

            // Finally, update the GUI
            Platform.runLater(() -> {
                setNotify(true);
            });

        });

    }

    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        if (spectrum instanceof MsScan) {
            MsScan scan = (MsScan) spectrum;
            String scanDesc = MsScanUtils.createFullMsScanDescription(scan);
            sb.append(scanDesc);
        }

        NumberFormat intensityFormat = MZmineCore.getConfiguration()
                .getIntensityFormat();
        NumberFormat mzFormat = MZmineCore.getConfiguration().getMZFormat();

        sb.append("Spectrum type: ");
        sb.append(spectrum.getSpectrumType());
        sb.append("\n");
        sb.append("Number of data points: ");
        sb.append(numOfDataPoints);
        sb.append("\n");
        Range<Double> mzRange = spectrum.getMzRange();
        if (mzRange != null) {
            sb.append("m/z range: ");
            sb.append(mzFormat.format(mzRange.lowerEndpoint()));
            sb.append(" - ");
            sb.append(mzFormat.format(mzRange.upperEndpoint()));
            sb.append(" m/z\n");
        }
        sb.append("Base peak intensity: ");
        sb.append(intensityFormat.format(topIndensity));
        sb.append("\n");
        sb.append("SPLASH ID: ");
        String splash = SplashCalculationAlgorithm.calculateSplash(spectrum);
        sb.append(splash);

        return sb.toString();
    }

    public MsSpectrum getSpectrum() {
        return spectrum;
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

    public Double getMzShift() {
        return mzShift.get();
    }

    public void setMzShift(Double newMzShift) {
        mzShift.set(newMzShift);
    }

    public DoubleProperty mzShiftProperty() {
        return mzShift;
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

    public MsSpectrumType getRenderingType() {
        return renderingType.get();
    }

    public void setRenderingType(MsSpectrumType newType) {
        renderingType.set(newType);
    }

    public ObjectProperty<MsSpectrumType> renderingTypeProperty() {
        return renderingType;
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
        return mzValues[index];
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
        final double mz = mzValues[index] - mzShift.doubleValue();
        NumberFormat mzFormat = MZmineCore.getConfiguration().getMZFormat();
        String label = mzFormat.format(mz);
        return label;
    }

    @Override
    public String generateToolTip(XYDataset ds, int series, int index) {
        final double actualMz = mzValues[index];
        final float scaledIntensity = getY(series, index).floatValue();
        final float actualIntensity = intensityValues[index];
        NumberFormat mzFormat = MZmineCore.getConfiguration().getMZFormat();
        NumberFormat intensityFormat = MZmineCore.getConfiguration()
                .getIntensityFormat();
        StringBuilder sb = new StringBuilder();

        if (mzShift.doubleValue() != 0.0) {
            final double displayMz = mzValues[index] - mzShift.doubleValue();
            sb.append("Display m/z: ");
            sb.append(mzFormat.format(displayMz));
            sb.append(" (shift ");
            sb.append(mzFormat.format(mzShift.doubleValue()));
            sb.append(" m/z)\n");
        }
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

    @Override
    public Number getStartX(int series, int item) {
        return getX(series, item);
    }

    @Override
    public double getStartXValue(int series, int item) {
        return getXValue(series, item);
    }

    @Override
    public Number getEndX(int series, int item) {
        return getX(series, item);
    }

    @Override
    public double getEndXValue(int series, int item) {
        return getXValue(series, item);
    }

    @Override
    public Number getStartY(int series, int item) {
        return getY(series, item);
    }

    @Override
    public double getStartYValue(int series, int item) {
        return getYValue(series, item);
    }

    @Override
    public Number getEndY(int series, int item) {
        return getY(series, item);
    }

    @Override
    public double getEndYValue(int series, int item) {
        return getYValue(series, item);
    }

}