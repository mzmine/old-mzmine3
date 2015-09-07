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

import java.util.ArrayList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
import com.google.common.collect.Range;

import io.github.msdk.MSDKRuntimeException;
import io.github.msdk.datamodel.datapointstore.DataPointStore;
import io.github.msdk.datamodel.msspectra.MsSpectrum;
import io.github.msdk.datamodel.msspectra.MsSpectrumDataPointList;
import io.github.msdk.datamodel.msspectra.MsSpectrumType;

/**
 * Simple implementation of the MassSpectrum interface, which stores its data in
 * a data point store.
 */
abstract class AbstractSpectrum implements MsSpectrum {

    private final @Nonnull DataPointStore dataPointStore;

    private Object dataStoreId = null;

    private @Nullable Range<Double> mzRange;
    private @Nonnull Float totalIonCurrent;

    private @Nonnull MsSpectrumType spectrumType;

    AbstractSpectrum(@Nonnull DataPointStore dataPointStore) {
        Preconditions.checkNotNull(dataPointStore);
        this.dataPointStore = dataPointStore;
        totalIonCurrent = 0f;
        spectrumType = MsSpectrumType.CENTROIDED;
    }

    @Override
    public @Nonnull MsSpectrumDataPointList getDataPoints() {
        final Object dataStoreIdCopy = dataStoreId;
        if (dataStoreIdCopy == null)
            throw (new MSDKRuntimeException("Missing data store ID")); 
        Preconditions.checkNotNull(dataPointStore);
        MsSpectrumDataPointList dataPointList = new SimpleMSSpectrumDataPointList();
        dataPointStore.readDataPoints(dataStoreIdCopy, dataPointList);
        return dataPointList;
    }

    @Override
    public void getDataPoints(@Nonnull MsSpectrumDataPointList dataPointList) {
        final Object dataStoreIdCopy = dataStoreId;
        if (dataStoreIdCopy == null)
            throw (new MSDKRuntimeException("Missing data store ID")); 
        Preconditions.checkNotNull(dataPointList);
        dataPointStore.readDataPoints(dataStoreIdCopy, dataPointList);
    }

    @Override
    @Nonnull
    public MsSpectrumDataPointList getDataPointsByMz(
            @Nonnull Range<Double> mzRange) {
        Preconditions.checkNotNull(dataStoreId);
        MsSpectrumDataPointList storedData = dataPointStore
                .readDataPoints(dataStoreId);
        final Range<Float> all = Range.all();
        return storedData.selectDataPoints(mzRange, all);
    }

    @Nonnull
    public MsSpectrumDataPointList getDataPointsByIntensity(
            @Nonnull Range<Float> intensityRange) {
        Preconditions.checkNotNull(dataStoreId);
        MsSpectrumDataPointList storedData = dataPointStore
                .readDataPoints(dataStoreId);
        final Range<Double> all = Range.all();
        return storedData.selectDataPoints(all, intensityRange);
    }

    @Nonnull
    public MsSpectrumDataPointList getDataPointsByMzAndIntensity(
            @Nonnull Range<Double> mzRange,
            @Nonnull Range<Float> intensityRange) {
        Preconditions.checkNotNull(dataStoreId);
        MsSpectrumDataPointList storedData = dataPointStore
                .readDataPoints(dataStoreId);
        return storedData.selectDataPoints(mzRange, intensityRange);
    }

    synchronized public void setDataPoints(
            @Nonnull MsSpectrumDataPointList newDataPoints) {
        Preconditions.checkNotNull(newDataPoints);
        if (dataStoreId != null)
            dataPointStore.removeDataPoints(dataStoreId);
        dataStoreId = dataPointStore.storeDataPoints(newDataPoints);
        mzRange = newDataPoints.getMzRange();
        totalIonCurrent = newDataPoints.getTIC();
    }

    @Override
    public @Nonnull MsSpectrumType getSpectrumType() {
        return spectrumType;
    }

    @Override
    public void setSpectrumType(@Nonnull MsSpectrumType spectrumType) {
        this.spectrumType = spectrumType;
    }

    @Nonnull
    public Float getTIC() {
        return totalIonCurrent;
    }

    public Range<Double> getMzRange() {
        return null;
    }
}
