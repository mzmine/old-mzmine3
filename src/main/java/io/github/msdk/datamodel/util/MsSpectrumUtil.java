/* 
 * (C) Copyright 2015 by MSDK Development Team
 *
 * This software is dual-licensed under either
 *
 * (a) the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation
 *
 * or (per the licensee's choosing)
 *
 * (b) the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */

package io.github.msdk.datamodel.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
import com.google.common.collect.Range;

import io.github.msdk.datamodel.msspectra.MsSpectrumDataPointList;

public class MsSpectrumUtil {

    /**
     * Returns the m/z range of given data points. Can return null if the data
     * point list is empty.
     */
    @Nullable
    public static Range<Double> getMzRange(
            @Nonnull MsSpectrumDataPointList dataPoints) {
        Preconditions.checkNotNull(dataPoints);
        if (dataPoints.getSize() == 0)
            return null;

        double mzValues[] = dataPoints.getMzBuffer();
        double min = mzValues[0];
        double max = mzValues[dataPoints.getSize() - 1];
        return Range.closed(min, max);
    }

    /**
     * Calculates the total ion current (=sum of all intensity values)
     */
    @Nonnull
    public static Float getTIC(@Nonnull MsSpectrumDataPointList dataPoints) {
        Preconditions.checkNotNull(dataPoints);
        float tic = 0f;
        float intValues[] = dataPoints.getIntensityBuffer();
        for (int i = 0; i < dataPoints.getSize(); i++) {
            tic += intValues[i];
        }
        return tic;
    }

    /**
     * Returns the highest intensity value. Returns 0 if the list has no data
     * points.
     */
    @Nonnull
    public static Float getMaxIntensity(
            @Nonnull MsSpectrumDataPointList dataPoints) {
        Preconditions.checkNotNull(dataPoints);
        float intValues[] = dataPoints.getIntensityBuffer();
        float maxInt = 0f;
        for (int i = 0; i < dataPoints.getSize(); i++) {
            if (intValues[i] > maxInt)
                maxInt = intValues[i];
        }
        return maxInt;
    }

}
