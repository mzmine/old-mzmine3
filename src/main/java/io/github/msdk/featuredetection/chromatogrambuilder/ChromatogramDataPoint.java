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

package io.github.msdk.featuredetection.chromatogrambuilder;

import javax.annotation.concurrent.Immutable;

import io.github.msdk.datamodel.rawdata.MsScan;

@Immutable
class ChromatogramDataPoint {

    private final MsScan scan;
    private final double mz;
    private final float intensity;

    ChromatogramDataPoint(MsScan scan, double mz, float intensity) {
        this.scan = scan;
        this.mz = mz;
        this.intensity = intensity;
    }

    MsScan getScan() {
        return scan;
    }

    double getMz() {
        return mz;
    }

    float getIntensity() {
        return intensity;
    }

}
