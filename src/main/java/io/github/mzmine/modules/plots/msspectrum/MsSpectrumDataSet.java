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

package io.github.mzmine.modules.plots.msspectrum;

import java.text.NumberFormat;

import io.github.msdk.datamodel.msspectra.MsSpectrum;
import io.github.msdk.datamodel.msspectra.MsSpectrumType;
import io.github.msdk.datamodel.rawdata.MsScan;
import io.github.msdk.datamodel.rawdata.RawDataFile;
import io.github.mzmine.main.MZmineCore;
import io.github.mzmine.util.charts.ChartDataSet;
import io.github.mzmine.util.charts.ChartType;

/**
 * MS spectrum data set
 */
public class MsSpectrumDataSet implements ChartDataSet {

    private final String name;
    private final ChartType type;
    private final double mzValues[];
    private final float intensityValues[];
    private final int numOfDataPoints;

    MsSpectrumDataSet(MsSpectrum spectrum) {

        String spectrumTitle = "MS spectrum";
        if (spectrum instanceof MsScan) {
            MsScan scan = (MsScan) spectrum;
            RawDataFile dataFile = scan.getRawDataFile();
            if (dataFile != null)
                spectrumTitle += " " + dataFile.getName();
            spectrumTitle += "#" + scan.getScanNumber();
        }
        this.name = spectrumTitle;

        if (spectrum.getSpectrumType() == MsSpectrumType.CENTROIDED)
            this.type = ChartType.BAR;
        else
            this.type = ChartType.LINE;

        this.mzValues = spectrum.getMzValues();
        this.intensityValues = spectrum.getIntensityValues();
        this.numOfDataPoints = spectrum.getNumberOfDataPoints();
    }

    @Override
    public ChartType getType() {
        return type;
    }

    @Override
    public double getX(int index) {
        return mzValues[index];
    }

    @Override
    public double getY(int index) {
        return intensityValues[index];
    }

    @Override
    public int getNumOfDataPoints() {
        return numOfDataPoints;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getLabel(int index) {
        NumberFormat mzFormat = MZmineCore.getConfiguration().getMZFormat();
        final double mz = mzValues[index];
        String label = mzFormat.format(mz);
        return label;
    }

    @Override
    public String getToolTip(int index) {
        NumberFormat mzFormat = MZmineCore.getConfiguration().getMZFormat();
        final double mz = mzValues[index];
        String label = mzFormat.format(mz);
        return label;
    }

    @Override
    public String getTitle() {
        return "MS spectrum";
    }

    @Override
    public String getXAxisName() {
        return "m/z";
    }

    @Override
    public String getYAxisName() {
        return "Intensity";
    }

}