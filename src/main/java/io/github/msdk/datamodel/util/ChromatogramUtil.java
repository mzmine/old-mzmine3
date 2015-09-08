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

import com.google.common.collect.Range;

import io.github.msdk.datamodel.rawdata.MsScan;

public class ChromatogramUtil {

    /**
     * @return The most representative scan of this feature (with highest signal
     *         intensity), or null if this peak is not connected to any raw
     *         data.
     */
    @Nullable
    public static MsScan getRepresentativeScan() {
        // TODO
        return null;
    }

    /**
     * Returns the number of scan that represents the fragmentation of this peak
     * in MS2 level.
     */
    @Nullable
    public static MsScan getMostIntenseFragmentScan() {
        // TODO
        return null;
    }

    /**
     * Returns the retention time range of all raw data points used to detect
     * this peak
     */
    @Nonnull
    public static Range<Double> getRawDataPointsRTRange() {
        // TODO
        return null;
    }

    /**
     * Returns the range of m/z values of all raw data points used to detect
     * this peak
     */
    @Nonnull
    public static Range<Double> getRawDataPointsMzRange() {
        // TODO
        return null;
    }

    /**
     * Returns the range of intensity values of all raw data points used to
     * detect this peak
     */
    @Nonnull
    public static Range<Double> getRawDataPointsIntensityRange() {
        // TODO
        return null;
    }
}
