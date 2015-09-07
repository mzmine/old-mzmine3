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

import org.junit.Assert;
import org.junit.Test;

import io.github.msdk.MSDKException;
import io.github.msdk.datamodel.chromatograms.ChromatogramDataPointList;

/**
 * Tests for SimpleChromatogramDataPointListTest
 */
public class SimpleChromatogramDataPointListTest {

    @Test
    public void testAddDataPoint() throws MSDKException {
        ChromatogramDataPointList dataPoints = MSDKObjectBuilder
                .getChromatogramDataPointList();
        dataPoints.add(2.0f, 222);
        dataPoints.add(1.0f, 111);
        dataPoints.add(3.0f, 333);
        dataPoints.add(5.0f, 555);
        dataPoints.add(4.0f, 444);
        dataPoints.add(3.5f, 350);

        float[] rtBuffer = dataPoints.getRtBuffer();
        for (int pos = 1; pos < dataPoints.getSize(); pos++) {
            Assert.assertTrue(rtBuffer[pos - 1] < rtBuffer[pos]);
        }
    }

}
