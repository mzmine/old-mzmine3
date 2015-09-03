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

package io.github.msdk.datamodel.rawdata;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Range;

import io.github.msdk.datamodel.datapointstore.DataPointList;

/**
 * This interface provides a convenient data structure for storing large amount
 * of data points in memory. Internally, it is implemented by two arrays, one
 * for rt values (double[]) and one for intensity values (float[]). The arrays
 * are resized dynamically, as in ArrayList.
 * 
 * DataPointList provides all List methods, therefore it supports iteration.
 * Furthermore, it provides direct access to the underlying buffer arrays, for
 * more efficient data handling operations. The access through these arrays is
 * always preferred, as iteration via the List interface has to create a new
 * DataPoint instance for each visited data point.
 * 
 * DataPointList methods always keep the data points sorted in the rt order,
 * and this requirement must be maintained when the internal rt and intensity
 * arrays are modified directly.
 * 
 * The equals() method compares the contents of the two data point lists, and
 * ignores their internal array sizes (capacities).
 * 
 * This data structure is not thread-safe.
 */
public interface ChromatogramDataPointList extends DataPointList, List<ChromatogramDataPoint> {

    /**
     * Returns the current rt buffer array. The size of the array might be
     * larger than the actual size of this DataPointList, therefore data
     * operations should always use the size returned by the size() method and
     * not the length of the returned array. The returned array reflects only
     * the current state of this list - if more data points are added, the
     * internal buffer might be replaced with a larger array.
     * 
     * @return current rt buffer
     */
    @Nonnull
    double[] getRtBuffer();

    /**
     * Returns the current intensity buffer array. The size of the array might
     * be larger than the actual size of this DataPointList, therefore data
     * operations should always use the size returned by the size() method and
     * not the length of the returned array. The returned array reflects only
     * the current state of this list - if more data points are added, the
     * internal buffer might be replaced with a larger array.
     * 
     * @return current intensity buffer
     */
    @Nonnull
    float[] getIntensityBuffer();

    /**
     * Sets the internal buffers to given arrays. The arrays will be referenced
     * directly without cloning. The rt buffer contents must be sorted in
     * ascending order.
     * 
     * @param mzBuffer
     *            new rt buffer
     * @param intensityBuffer
     *            new intensity buffer
     * @param newSize
     *            number of data point items in the buffers; this might be
     *            smaller than the actual length of the buffer arrays
     * @throws IllegalArgumentException
     *             if the size is larger than the length of the rt array
     * @throws IllegalStateException
     *             if the rt array is not sorted in ascending order
     */
    void setBuffers(@Nonnull double[] rtBuffer,
            @Nonnull float[] intensityBuffer, int newSize);

    /**
     * Add a new data point into the right position of the list, maintaining the
     * rt order. If the internal arrays are full, they are replaced with new
     * arrays of twice the length.
     * 
     * @param newRt
     *            rt value of the new data point
     * @param newIntensity
     *            intensity value of the new data point
     */
    void add(double newRt, float newIntensity);

    /**
     * Add a new data point into a specified position of the list. If the
     * internal arrays are full, they are replaced with new arrays of twice the
     * length.
     * 
     * @param index
     *            index at which the specified data point is to be inserted
     * @param newRt
     *            rt value of the new data point
     * @param newIntensity
     *            intensity value of the new data point
     */
    void add(int index, double newRt, float newIntensity);

    /**
     * Updates the size of the list, assuming the rt and intensity arrays have
     * already been updated accordingly. This method also checks whether the rt
     * array is sorted in ascending order.
     * 
     * @param newSize
     *            new size of the list. Must be <= length of the rt array
     * @throws IllegalArgumentException
     *             if the size is larger than the length of the rt array
     * @throws IllegalStateException
     *             if the rt array is not sorted in ascending order
     */
    void setSize(int newSize);

    /**
     * Copies the contents of another data point list into this list. The
     * capacity of this list might stay the same or it might change, depending
     * on needs.
     * 
     * @param list
     *            source list to copy from.
     */
    void copyFrom(@Nonnull ChromatogramDataPointList list);

    /**
     * Copies the contents of this data point list into another list. The
     * capacity of the target list might stay the same or it might change,
     * depending on needs.
     * 
     * @param list
     *            target list to copy to.
     */
    void copyTo(@Nonnull ChromatogramDataPointList list);

    /**
     * Creates a new DataPointList that contains only those data points that fit
     * within given rt and intensity boundaries.
     * 
     * @param rtRange
     *            rt range to select
     * @param intensityRange
     *            intensity range to select
     * @return new DataPointList
     */
    ChromatogramDataPointList selectDataPoints(@Nonnull Range<Double> rtRange,
            @Nonnull Range<Float> intensityRange);

    /**
     * Returns the range of rt values in this DataPointList, or null if the
     * list is empty.
     * 
     * @return range of rt values in this DataPointList, or null
     */
    @Nullable
    Range<Double> getRtRange();

}
