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

import com.google.common.collect.Range;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;

import io.github.msdk.datamodel.msspectra.MsSpectrum;
import io.github.msdk.datamodel.msspectra.MsSpectrumType;
import io.github.msdk.datamodel.rawdata.MsScan;
import io.github.msdk.datamodel.rawdata.RawDataFile;
import io.github.msdk.util.MsSpectrumUtil;
import javafx.scene.layout.BorderPane;
import waterloo.fx.plot.Chart;
import waterloo.fx.plot.LinePlot;

/**
 * Chart using WaterlooFX library
 */
public class ChartNodeWaterlooFX extends BorderPane implements ChartNode {

    private final Chart chart;

    ChartNodeWaterlooFX() {
        chart = new Chart();

        chart.setLeftAxisTitle("Intensity");
        chart.setBottomAxisTitle("m/z");

        setCenter(chart);
    }

    public void addSpectrum(MsSpectrum spectrum) {

        String spectrumTitle = "MS spectrum";
        if (spectrum instanceof MsScan) {
            MsScan scan = (MsScan) spectrum;
            RawDataFile dataFile = scan.getRawDataFile();
            if (dataFile != null)
                spectrumTitle += " " + dataFile.getName();
            spectrumTitle += "#" + scan.getScanNumber();
        }

        final double mzValues[] = spectrum.getMzValues();
        final float intensityValues[] = spectrum.getIntensityValues();
        final int numOfDataPoints = spectrum.getNumberOfDataPoints();
        final boolean centroided = spectrum
                .getSpectrumType() == MsSpectrumType.CENTROIDED;
        final String label = spectrumTitle;

        LinePlot p = new LinePlot();

        p.getDataModel().setXData(mzValues);
        p.getDataModel()
                .setYData(Doubles.toArray(Floats.asList(intensityValues)));

        Range<Double> mzRange = MsSpectrumUtil.getMzRange(mzValues,
                numOfDataPoints);
        float maxInt = MsSpectrumUtil.getMaxIntensity(intensityValues,
                numOfDataPoints);

        chart.setXLeft(mzRange.lowerEndpoint());
        chart.setXRight(mzRange.upperEndpoint());
        chart.setYBottom(0.0);
        chart.setYTop((double) maxInt);

        chart.getChildren().add(p);

    }

}