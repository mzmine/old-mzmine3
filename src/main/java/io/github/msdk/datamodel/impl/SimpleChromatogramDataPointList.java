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

import javax.annotation.Nonnull;

import com.google.common.base.Preconditions;
import com.google.common.collect.Range;

import io.github.msdk.datamodel.rawdata.ChromatogramDataPointList;
import io.github.msdk.datamodel.rawdata.SpectrumDataPointList;

/**
 * Basic implementation of DataPointList.
 * 
 * Important: this class is not thread-safe.
 */
class SimpleChromatogramDataPointList implements ChromatogramDataPointList {

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
    SimpleChromatogramDataPointList() {
        this(100);
    }

    /**
     * Creates a new data point list with given internal array capacity.
     * 
     * @param initialCapacity
     *            Initial size of the m/z and intensity arrays.
     */
    SimpleChromatogramDataPointList(@Nonnull Integer initialCapacity) {
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
    SimpleChromatogramDataPointList(@Nonnull double mzBuffer[],
            @Nonnull float intensityBuffer[], int size) {
        Preconditions.checkArgument(mzBuffer.length >= size);
        Preconditions.checkArgument(intensityBuffer.length >= size);
        this.mzBuffer = mzBuffer;
        this.intensityBuffer = intensityBuffer;
        this.size = size;
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
     */
    @Override
    public int getSize() {
        return size;
    }

    /**
     * Insert into the right position
     */
    public void add(double newMz, float newIntensity) {
        int targetPosition;
        for (targetPosition = 0; targetPosition < size; targetPosition++) {
            if (mzBuffer[targetPosition] > newMz)
                break;
        }
        // TODO this.add(targetPosition, newMz, newIntensity);
    }

    /**
     * The equals() method compares the contents of the two data point lists,
     * and ignores their internal array sizes (capacities).
     */
    @Override
    public boolean equals(Object o) {

        // o must be a non-null DataPointList
        if ((o == null) || (!(o instanceof SpectrumDataPointList)))
            return false;

        // Cast o to DataPointlist
        SpectrumDataPointList otherList = (SpectrumDataPointList) o;

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
    public float[] getRtBuffer() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setBuffers(float[] rtBuffer, float[] intensityBuffer,
            int newSize) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void add(float newRt, float newIntensity) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void copyFrom(ChromatogramDataPointList list) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public ChromatogramDataPointList selectDataPoints(Range<Float> rtRange,
            Range<Float> intensityRange) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Range<Float> getRtRange() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void clear() {
        this.size = 0;
    }
}
