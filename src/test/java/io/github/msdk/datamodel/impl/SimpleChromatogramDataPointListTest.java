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
import io.github.msdk.datamodel.rawdata.ChromatographyInfo;
import io.github.msdk.datamodel.rawdata.SeparationType;

/**
 * Tests for SimpleChromatogramDataPointListTest
 */
public class SimpleChromatogramDataPointListTest {

    @Test
    public void testAddDataPoint() throws MSDKException {
        ChromatogramDataPointList dataPoints = MSDKObjectBuilder
                .getChromatogramDataPointList();

        dataPoints.add(new SimpleChromatographyInfo(2.0f, null, null,
                SeparationType.LC), 222);
        dataPoints.add(new SimpleChromatographyInfo(1.0f, null, null,
                SeparationType.LC), 111);
        dataPoints.add(new SimpleChromatographyInfo(3.0f, null, null,
                SeparationType.LC), 333);
        dataPoints.add(new SimpleChromatographyInfo(5.0f, null, null,
                SeparationType.LC), 555);
        dataPoints.add(new SimpleChromatographyInfo(4.0f, null, null,
                SeparationType.LC), 444);
        dataPoints.add(new SimpleChromatographyInfo(3.5f, null, null,
                SeparationType.LC), 350);

        ChromatographyInfo[] rtBuffer = dataPoints.getRtBuffer();
        for (int pos = 1; pos < dataPoints.getSize(); pos++) {
            Assert.assertTrue(rtBuffer[pos].compareTo(rtBuffer[pos-1]) >= 0);
        }
    }

}
