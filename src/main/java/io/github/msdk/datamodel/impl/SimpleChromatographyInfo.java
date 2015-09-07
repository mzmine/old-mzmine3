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

package io.github.msdk.datamodel.impl;

import javax.annotation.Nullable;

import io.github.msdk.datamodel.rawdata.ChromatographyInfo;
import io.github.msdk.datamodel.rawdata.SeparationType;

class SimpleChromatographyInfo implements ChromatographyInfo {

    private Float retentionTime, secondaryRetentionTime, ionDriftTime;

    private @Nullable SeparationType separationType;

    SimpleChromatographyInfo(Float retentionTime, Float secondaryRetentionTime,
            Float ionDriftTime, SeparationType separationType) {
        this.retentionTime = retentionTime;
        this.secondaryRetentionTime = secondaryRetentionTime;
        this.ionDriftTime = ionDriftTime;
        this.separationType = separationType;
    }

    @Override
    public Float getRetentionTime() {
        return retentionTime;
    }

    @Override
    public Float getSecondaryRetentionTime() {
        return secondaryRetentionTime;
    }

    @Override
    public Float getIonDriftTime() {
        return ionDriftTime;
    }

    @Override
    public SeparationType getSeparationType() {
        return separationType;
    }

    @Override
    public int compareTo(ChromatographyInfo o) {
        int returnValue;

        // 1. Compare retention time
        returnValue = this.retentionTime.compareTo(o.getRetentionTime());

        // 2. Compare secondary retention time
        if (returnValue == 0) {
            returnValue = this.secondaryRetentionTime.compareTo(o.getSecondaryRetentionTime());
        }

        // 3. Compare ion drift time
        if (returnValue == 0) {
            returnValue = this.ionDriftTime.compareTo(o.getIonDriftTime());
        }

        return returnValue;
    }

}
