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

package io.github.msdk.datamodel.peaklists;

import io.github.msdk.datamodel.rawdata.ChromatographyInfo;
import io.github.msdk.datamodel.rawdata.SpectrumDataPoint;
import io.github.msdk.datamodel.rawdata.MsScan;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/* 
 * WARNING: the interfaces in this package are still under construction
 */

/**
 * 
 */
public interface FeatureDataPoint extends SpectrumDataPoint {

    /**
     * 
     * @return
     */
    @Nullable
    Integer getScanNumber();

    /**
     * 
     * @return
     */
    @Nullable
    ChromatographyInfo getChromatographyInfo();

}
