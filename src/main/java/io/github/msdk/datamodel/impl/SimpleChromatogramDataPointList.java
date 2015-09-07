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

import java.util.Arrays;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
import com.google.common.collect.Range;

import io.github.msdk.MSDKRuntimeException;
import io.github.msdk.datamodel.chromatograms.ChromatogramDataPointList;

/**
 * Basic implementation of DataPointList.
 * 
 * Important: this class is not thread-safe.
 */
class SimpleChromatogramDataPointList implements ChromatogramDataPointList {

    /**
     * Array for RT values. Its length defines the capacity of this list.
     */
    private @Nonnull float[] rtBuffer;

    /**
     * Array for intensity values. Its length is always the same as the RT
     * buffer length.
     */
    private @Nonnull float[] intensityBuffer;

    /**
     * Current size of the list
     */
    private int size;

    /**
     * Creates a new data point list with internal array capacity of 100.
     */
    SimpleChromatogramDataPointList() {
        this(100);
    }

    /**
     * Creates a new data point list with given internal array capacity.
     * 
     * @param initialCapacity
     *            Initial size of the RT and intensity arrays.
     */
    SimpleChromatogramDataPointList(@Nonnull Integer initialCapacity) {
        rtBuffer = new float[initialCapacity];
        intensityBuffer = new float[initialCapacity];
        size = 0;
    }

    /**
     * Creates a new data point list backed by given arrays. Arrays are
     * referenced, not cloned.
     * 
     * @param rtBuffer
     *            array of RT values
     * @param intensityBuffer
     *            array of intensity values
     * @param size
     *            size of the list, must be <= length of both arrays
     * @throws IllegalArgumentException
     *             if the initial array length < size
     */
    SimpleChromatogramDataPointList(@Nonnull float rtBuffer[],
            @Nonnull float intensityBuffer[], int size) {
        Preconditions.checkArgument(rtBuffer.length >= size);
        Preconditions.checkArgument(intensityBuffer.length >= size);
        this.rtBuffer = rtBuffer;
        this.intensityBuffer = intensityBuffer;
        this.size = size;
    }

    /**
     * Returns the current RT array
     */
    @Override
    @Nonnull
    public float[] getRtBuffer() {
        return rtBuffer;
    }

    /**
     * Returns the current intensity array
     */
    @Override
    @Nonnull
    public float[] getIntensityBuffer() {
        return intensityBuffer;
    }

    /**
     * Replaces theRT and intensity arrays with new ones
     */
    @Override
    public void setBuffers(@Nonnull float[] rtBuffer,
            @Nonnull float[] intensityBuffer, int newSize) {

        if (rtBuffer.length != intensityBuffer.length) {
            throw new IllegalArgumentException(
                    "The length of the rt and intensity arrays must be equal");
        }

        // Check if the RT array is properly sorted
        for (int pos = 1; pos < newSize; pos++) {
            if (rtBuffer[pos] < rtBuffer[pos - 1])
                throw (new MSDKRuntimeException(
                        "The RT array is not properly sorted. It should be sorted from lowest to highest."));
        }

        // Update arrays
        this.rtBuffer = rtBuffer;
        this.intensityBuffer = intensityBuffer;

        // Update the size
        this.size = newSize;
    }

    /**
     * Copy data from another DataPointList
     */
    @Override
    public void copyFrom(@Nonnull ChromatogramDataPointList list) {
        if (rtBuffer.length < list.getSize()) {
            rtBuffer = new float[list.getSize()];
            intensityBuffer = new float[list.getSize()];
        }

        // Copy data
        System.arraycopy(list.getRtBuffer(), 0, rtBuffer, 0, list.getSize());
        System.arraycopy(list.getIntensityBuffer(), 0, intensityBuffer, 0,
                list.getSize());

        // Update the size
        this.size = list.getSize();
    }

    /**
     * Returns the RT range, assuming the RT array is sorted.
     */
    @Override
    @Nullable
    public Range<Float> getRtRange() {
        if (size == 0)
            return null;
        return Range.closed(rtBuffer[0], rtBuffer[size - 1]);
    }

    /**
     * Returns the current size of the array
     */
    @Override
    public int getSize() {
        return size;
    }

    /**
     * Sets the current size of the array
     * 
     * @param newSize
     */
    public void setSize(int newSize) {
        this.size = newSize;
    }

    /**
     * Insert into the right position
     */
    public void add(float newRt, float newIntensity) {
        int targetPosition = 0;
        if (size != 0) {
            targetPosition = Arrays.binarySearch(rtBuffer, 0, size, newRt);
            targetPosition = Math.abs(targetPosition + 1);
        }
        this.add(targetPosition, newRt, newIntensity);
    }

    /**
     * Insert data into specific position
     */
    public void add(int targetPosition, float newRt, float newIntensity) {
        int thisCapacity = rtBuffer.length;
        if (!(size < rtBuffer.length))
            thisCapacity++;

        float[] rtBufferNew = new float[thisCapacity];
        float[] intensityBufferNew = new float[thisCapacity];

        // Data before new data point
        if (targetPosition > 0) {
            System.arraycopy(getRtBuffer(), 0, rtBufferNew, 0, targetPosition);
            System.arraycopy(getIntensityBuffer(), 0, intensityBufferNew, 0,
                    targetPosition);
        }

        // New data point
        rtBufferNew[targetPosition] = newRt;
        intensityBufferNew[targetPosition] = newIntensity;

        // Data after new data point
        if (targetPosition < thisCapacity) {
            System.arraycopy(getRtBuffer(), targetPosition, rtBufferNew,
                    targetPosition + 1, size - targetPosition);
            System.arraycopy(getIntensityBuffer(), targetPosition,
                    intensityBufferNew, targetPosition + 1,
                    size - targetPosition);
        }

        // Replace arrays with new
        rtBuffer = rtBufferNew;
        intensityBuffer = intensityBufferNew;

        // Update the size
        this.size = size + 1;
    }

    /**
     * The equals() method compares the contents of the two data point lists,
     * and ignores their internal array sizes (capacities).
     */
    @Override
    public boolean equals(Object o) {

        // o must be a non-null DataPointList
        if ((o == null) || (!(o instanceof ChromatogramDataPointList)))
            return false;

        // Cast o to DataPointlist
        ChromatogramDataPointList otherList = (ChromatogramDataPointList) o;

        // Size must be equal
        if (otherList.getSize() != size)
            return false;

        // Get the arrays of the other list
        final float otherRtBuffer[] = otherList.getRtBuffer();
        final float otherIntensityBuffer[] = otherList.getIntensityBuffer();

        // Check the array contents
        for (int i = 0; i < size; i++) {
            if (rtBuffer[i] != otherRtBuffer[i])
                return false;
            if (intensityBuffer[i] != otherIntensityBuffer[i])
                return false;
        }

        // No difference found, return true
        return true;
    }

    /**
     * The hashCode() code is inspired by Arrays.hashCode(double[] or float[])
     */
    @Override
    public int hashCode() {
        int result = 1;
        for (int i = 0; i < size; i++) {
            long bits = Double.doubleToLongBits(rtBuffer[i]);
            result = 31 * result + (int) (bits ^ (bits >>> 32));
            result = 31 * result + Float.floatToIntBits(intensityBuffer[i]);
        }
        return result;
    }

    /**
     * toString() method
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (int i = 0; i < size; i++) {
            if (i > 0)
                builder.append(", ");
            builder.append(rtBuffer[i]);
            builder.append(":");
            builder.append(intensityBuffer[i]);
        }
        builder.append("]");
        return builder.toString();
    }

    @Override
    @Nonnull
    public ChromatogramDataPointList selectDataPoints(
            @Nonnull Range<Float> rtRange,
            @Nonnull Range<Float> intensityRange) {

        final ChromatogramDataPointList newList = MSDKObjectBuilder
                .getChromatogramDataPointList();

        for (int i = 0; i < size; i++) {
            if (rtRange.contains(rtBuffer[i])
                    && intensityRange.contains(intensityBuffer[i]))
                newList.add(rtBuffer[i], intensityBuffer[i]);
        }

        return newList;
    }

    @Override
    public void clear() {
        this.size = 0;
    }

    @Override
    public void allocate(int newSize) {
        if (rtBuffer.length >= newSize)
            return;

        float[] rtBufferNew = new float[newSize];
        float[] intensityBufferNew = new float[newSize];

        System.arraycopy(getRtBuffer(), 0, rtBufferNew, 0, size);
        System.arraycopy(getIntensityBuffer(), 0, intensityBufferNew, 0, size);

        rtBuffer = rtBufferNew;
        intensityBuffer = intensityBufferNew;

    }

}
