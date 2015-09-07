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
import io.github.msdk.datamodel.msspectra.MsSpectrumDataPointList;

/**
 * Tests for SimpleMSSpectrumDataPointListTest
 */
public class SimpleMSSpectrumDataPointListTest {

    @Test
    public void testAddDataPoint() throws MSDKException {
        MsSpectrumDataPointList dataPoints = MSDKObjectBuilder
                .getMsSpectrumDataPointList();
        dataPoints.add(2.0, 222);
        dataPoints.add(1.0, 111);
        dataPoints.add(3.0, 333);
        dataPoints.add(5.0, 555);
        dataPoints.add(4.0, 444);
        dataPoints.add(3.5, 350);

        double[] mzBuffer = dataPoints.getMzBuffer();
        for (int pos = 1; pos < dataPoints.getSize(); pos++) {
            Assert.assertTrue(mzBuffer[pos - 1] < mzBuffer[pos]);
        }
    }

}
