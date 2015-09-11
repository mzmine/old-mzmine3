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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.github.msdk.datamodel.rawdata.ChromatographyInfo;

public class DataPointSorter {

    /**
     * Sort the given data points by m/z order
     */
    public static void sortDataPoints(final double mzBuffer[],
            final float intensityBuffer[], final int size) {

        // Use Collections.sort to obtain index mapping from old arrays to the
        // new sorted array
        final List<Integer> idx = new ArrayList<>(size);
        for (int i = 0; i < size; i++)
            idx.add(i);
        Collections.sort(idx, new Comparator<Integer>() {
            public int compare(Integer i1, Integer i2) {
                return Double.compare(mzBuffer[i1], mzBuffer[i2]);
            }
        });

        // Remap the values according to the index map idx
        for (int i = 0; i < size; i++) {
            final int newIndex = idx.get(i);
            if (newIndex == i)
                continue;

            final double tmpMz = mzBuffer[i];
            final float tmpInt = intensityBuffer[i];

            mzBuffer[i] = mzBuffer[newIndex];
            intensityBuffer[i] = intensityBuffer[newIndex];

            mzBuffer[newIndex] = tmpMz;
            intensityBuffer[newIndex] = tmpInt;

            final int swapIndex = idx.indexOf(i);
            idx.set(swapIndex, newIndex);

        }

    }

    /**
     * Sort the given data points by RT order
     */
    public static void sortDataPoints(final ChromatographyInfo rtBuffer[],
            final float intensityBuffer[], final int size) {

        // Use Collections.sort to obtain index mapping from old arrays to the
        // new sorted array
        final List<Integer> idx = new ArrayList<>(size);
        for (int i = 0; i < size; i++)
            idx.add(i);
        Collections.sort(idx, new Comparator<Integer>() {
            public int compare(Integer i1, Integer i2) {
                return rtBuffer[i1].compareTo(rtBuffer[i2]);
            }
        });

        // Remap the values according to the index map idx
        for (int i = 0; i < size; i++) {
            final int newIndex = idx.get(i);
            if (newIndex == i)
                continue;

            final ChromatographyInfo tmpRt = rtBuffer[i];
            final float tmpInt = intensityBuffer[i];

            rtBuffer[i] = rtBuffer[newIndex];
            intensityBuffer[i] = intensityBuffer[newIndex];

            rtBuffer[newIndex] = tmpRt;
            intensityBuffer[newIndex] = tmpInt;

            final int swapIndex = idx.indexOf(i);
            idx.set(swapIndex, newIndex);

        }
    }

}
