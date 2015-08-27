/*
 * Copyright 2006-2015 The MZmine 2 Development Team
 * 
 * This file is part of MZmine 2.
 * 
 * MZmine 2 is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * MZmine 2 is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * MZmine 2; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

package io.github.mzmine.datamodel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Range;

/**
 * Represents any kind of mass spectrum. For example, a single scan in raw MS
 * data, a predicted isotope pattern etc.
 */
public interface MassSpectrum {

    /**
     * Returns the m/z range of this Scan. Never returns null.
     * 
     * @return m/z range of this Scan
     */
    @Nonnull
    Range<Double> getMzRange();

    /**
     * Returns the top intensity data point. May return null if there are no
     * data points in this spectrum.
     * 
     * @return Base peak
     */
    @Nullable
    DataPoint getHighestDataPoint();

    /**
     * 
     * @return True if the spectrum is centroided
     */
    @Nonnull
    MassSpectrumType getSpectrumType();

    /**
     * 
     * @return True if the spectrum is centroided
     */
    void setSpectrumType(@Nonnull MassSpectrumType spectrumType);

    /**
     * @return Number of m/z and intensity data points.
     */
    int getNumberOfDataPoints();

    /**
     * Returns data points of this spectrum, always sorted in m/z order.
     * 
     * This method may need to read data from disk, therefore it may be quite
     * slow. Modules should be aware of that and cache the data points if
     * necessary.
     * 
     * @return Data points (m/z and intensity pairs) of this scan
     */
    @Nonnull
    DataPoint[] getDataPoints();

    void setDataPoints(@Nonnull DataPoint newDataPoints[]);

    /**
     * Returns data points in given m/z range, sorted in m/z order.
     * 
     * This method may need to read data from disk, therefore it may be quite
     * slow. Modules should be aware of that and cache the data points if
     * necessary.
     * 
     * @return Data points (m/z and intensity pairs) of this spectrum
     */
    @Nonnull
    DataPoint[] getDataPointsByMass(@Nonnull Range<Double> mzRange);

    /**
     * Returns data points over given intensity, sorted in m/z order.
     * 
     * This method may need to read data from disk, therefore it may be quite
     * slow. Modules should be aware of that and cache the data points if
     * necessary.
     * 
     * @return Data points (m/z and intensity pairs) of this Spectrum
     */
    @Nonnull
    DataPoint[] getDataPointsOverIntensity(double intensity);

}
