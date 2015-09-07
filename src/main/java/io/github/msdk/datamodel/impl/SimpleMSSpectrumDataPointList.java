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
import io.github.msdk.datamodel.msspectra.MsSpectrumDataPointList;

/**
 * Basic implementation of DataPointList.
 * 
 * Important: this class is not thread-safe.
 */
class SimpleMSSpectrumDataPointList implements MsSpectrumDataPointList {

    /**
     * Array for m/z values. Its length defines the capacity of this list.
     */
    private @Nonnull double[] mzBuffer;

    /**
     * Array for intensity values. Its length is always the same as the m/z
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
    SimpleMSSpectrumDataPointList() {
        this(100);
    }

    /**
     * Creates a new data point list with given internal array capacity.
     * 
     * @param initialCapacity
     *            Initial size of the m/z and intensity arrays.
     */
    SimpleMSSpectrumDataPointList(@Nonnull Integer initialCapacity) {
        mzBuffer = new double[initialCapacity];
        intensityBuffer = new float[initialCapacity];
        size = 0;
    }

    /**
     * Creates a new data point list backed by given arrays. Arrays are
     * referenced, not cloned.
     * 
     * @param mzBuffer
     *            array of m/z values
     * @param intensityBuffer
     *            array of intensity values
     * @param size
     *            size of the list, must be <= length of both arrays
     * @throws IllegalArgumentException
     *             if the initial array length < size
     */
    SimpleMSSpectrumDataPointList(@Nonnull double mzBuffer[],
            @Nonnull float intensityBuffer[], int size) {
        Preconditions.checkArgument(mzBuffer.length >= size);
        Preconditions.checkArgument(intensityBuffer.length >= size);
        this.mzBuffer = mzBuffer;
        this.intensityBuffer = intensityBuffer;
        this.size = size;
    }

    /**
     * Returns the current m/z array
     */
    @Override
    @Nonnull
    public double[] getMzBuffer() {
        return mzBuffer;
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
     * Replaces the m/z and intensity arrays with new ones
     */
    @Override
    public void setBuffers(@Nonnull double[] mzBuffer,
            @Nonnull float[] intensityBuffer, int newSize) {

        if (mzBuffer.length != intensityBuffer.length) {
            throw new IllegalArgumentException(
                    "The length of the m/z and intensity arrays must be equal");
        }

        // Check if the m/z array is properly sorted
        for (int pos = 1; pos < newSize; pos++) {
            if (mzBuffer[pos] < mzBuffer[pos - 1])
                throw (new MSDKRuntimeException(
                        "The m/z array is not properly sorted. It should be sorted from lowest to highest."));
        }

        // Update arrays
        this.mzBuffer = mzBuffer;
        this.intensityBuffer = intensityBuffer;

        // Update the size
        this.size = newSize;
    }

    /**
     * Copy data from another DataPointList
     */
    @Override
    public void copyFrom(@Nonnull MsSpectrumDataPointList list) {
        if (mzBuffer.length < list.getSize()) {
            mzBuffer = new double[list.getSize()];
            intensityBuffer = new float[list.getSize()];
        }

        // Copy data
        System.arraycopy(list.getMzBuffer(), 0, mzBuffer, 0, list.getSize());
        System.arraycopy(list.getIntensityBuffer(), 0, intensityBuffer, 0,
                list.getSize());

        // Update the size
        this.size = list.getSize();

    }

    /**
     * Returns the m/z range, assuming the m/z array is sorted.
     */
    @Override
    @Nullable
    public Range<Double> getMzRange() {
        if (size == 0)
            return null;
        return Range.closed(mzBuffer[0], mzBuffer[size - 1]);
    }

    /**
     */
    @Override
    public int getSize() {
        return size;
    }

    /**
     * 
     * @param newSize
     */
    public void setSize(int newSize) {
        this.size = newSize;
    }

    /**
     * Insert into the right position
     */
    public void add(double newMz, float newIntensity) {
        int targetPosition = 0;
        if (size != 0) {
            targetPosition = Arrays.binarySearch(mzBuffer, 0, size, newMz);
            targetPosition = Math.abs(targetPosition + 1);
        }
        this.add(targetPosition, newMz, newIntensity);
    }

    /**
     * Insert data into specific position
     */
    public void add(int targetPosition, double newMz, float newIntensity) {
        int thisCapacity = mzBuffer.length;
        if (!(size < mzBuffer.length))
            thisCapacity++;

        double[] mzBufferNew = new double[thisCapacity];
        float[] intensityBufferNew = new float[thisCapacity];

        // Data before new data point
        if (targetPosition > 0) {
            System.arraycopy(getMzBuffer(), 0, mzBufferNew, 0, targetPosition);
            System.arraycopy(getIntensityBuffer(), 0, intensityBufferNew, 0,
                    targetPosition);
        }

        // New data point
        mzBufferNew[targetPosition] = newMz;
        intensityBufferNew[targetPosition] = newIntensity;

        // Data after new data point
        if (targetPosition < thisCapacity) {
            System.arraycopy(getMzBuffer(), targetPosition, mzBufferNew,
                    targetPosition + 1, size - targetPosition);
            System.arraycopy(getIntensityBuffer(), targetPosition,
                    intensityBufferNew, targetPosition + 1,
                    size - targetPosition);
        }

        // Replace arrays with new
        mzBuffer = mzBufferNew;
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
        if ((o == null) || (!(o instanceof MsSpectrumDataPointList)))
            return false;

        // Cast o to DataPointlist
        MsSpectrumDataPointList otherList = (MsSpectrumDataPointList) o;

        // Size must be equal
        if (otherList.getSize() != size)
            return false;

        // Get the arrays of the other list
        final double otherMzBuffer[] = otherList.getMzBuffer();
        final float otherIntensityBuffer[] = otherList.getIntensityBuffer();

        // Check the array contents
        for (int i = 0; i < size; i++) {
            if (mzBuffer[i] != otherMzBuffer[i])
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
            long bits = Double.doubleToLongBits(mzBuffer[i]);
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
            builder.append(mzBuffer[i]);
            builder.append(":");
            builder.append(intensityBuffer[i]);
        }
        builder.append("]");
        return builder.toString();
    }

    @Override
    @Nonnull
    public Float getTIC() {
        float tic = 0f;
        for (int i = 0; i < size; i++) {
            tic += intensityBuffer[i];
        }
        return tic;
    }

    @Override
    @Nonnull
    public MsSpectrumDataPointList selectDataPoints(
            @Nonnull Range<Double> mzRange,
            @Nonnull Range<Float> intensityRange) {

        final MsSpectrumDataPointList newList = MSDKObjectBuilder
                .getMsSpectrumDataPointList();

        for (int i = 0; i < size; i++) {
            if (mzRange.contains(mzBuffer[i])
                    && intensityRange.contains(intensityBuffer[i]))
                newList.add(mzBuffer[i], intensityBuffer[i]);
        }

        return newList;
    }

    @Override
    public void clear() {
        this.size = 0;
    }

    @Override
    public void allocate(int newSize) {
        if (mzBuffer.length >= newSize)
            return;

        double[] mzBufferNew = new double[newSize];
        float[] intensityBufferNew = new float[newSize];

        System.arraycopy(getMzBuffer(), 0, mzBufferNew, 0, size);
        System.arraycopy(getIntensityBuffer(), 0, intensityBufferNew, 0, size);

        mzBuffer = mzBufferNew;
        intensityBuffer = intensityBufferNew;

    }

}
