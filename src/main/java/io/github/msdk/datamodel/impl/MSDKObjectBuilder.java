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

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.github.msdk.datamodel.datapointstore.DataPointStore;
import io.github.msdk.datamodel.peaklists.FeatureDataPointList;
import io.github.msdk.datamodel.peaklists.PeakListColumn;
import io.github.msdk.datamodel.rawdata.ChromatogramDataPointList;
import io.github.msdk.datamodel.rawdata.ChromatographyInfo;
import io.github.msdk.datamodel.rawdata.MsFunction;
import io.github.msdk.datamodel.rawdata.MsScan;
import io.github.msdk.datamodel.rawdata.RawDataFile;
import io.github.msdk.datamodel.rawdata.SeparationType;
import io.github.msdk.datamodel.rawdata.SpectrumDataPointList;

/**
 * Object builder
 */
public class MSDKObjectBuilder {

    /**
     * The number of MS functions used in a project is typically small, but each
     * scan has to be annotated with its MS function. So we take advantage of
     * the immutable nature of MsFunction and recycle the instances using this
     * List.
     */
    private static final List<WeakReference<MsFunction>> msFunctions = new LinkedList<>();

   /**
     * Creates a new MsFunction reference.
     * 
     * @param name
     * @param msLevel
     * @return
     */
    public static final @Nonnull MsFunction getMsFunction(@Nonnull String name,
            @Nullable Integer msLevel) {

        synchronized (msFunctions) {
            Iterator<WeakReference<MsFunction>> iter = msFunctions.iterator();
            while (iter.hasNext()) {
                WeakReference<MsFunction> ref = iter.next();
                MsFunction func = ref.get();
                if (func == null) {
                    iter.remove();
                    continue;
                }
                if (!func.getName().equals(name))
                    continue;
                if ((func.getMsLevel() == null) && (msLevel == null))
                    return func;
                if ((func.getMsLevel() != null)
                        && (func.getMsLevel().equals(msLevel)))
                    return func;
            }
            MsFunction newFunc = new SimpleMsFunction(name, msLevel);
            WeakReference<MsFunction> ref = new WeakReference<>(newFunc);
            msFunctions.add(ref);
            return newFunc;

        }
    }

    /**
     * Creates a new MsFunction reference.
     * 
     * @param name
     * @return
     */
    public static final @Nonnull MsFunction getMsFunction(
            @Nonnull String name) {
        return getMsFunction(name, null);
    }

    /**
     * Creates a new MsFunction reference.
     * 
     * @param msLevel
     * @return
     */
    public static final @Nonnull MsFunction getMsFunction(
            @Nullable Integer msLevel) {
        return getMsFunction(MsFunction.DEFAULT_MS_FUNCTION_NAME, msLevel);
    }

    public static final @Nonnull RawDataFile getRawDataFile() {
        return new SimpleRawDataFile();
    }

    public static final @Nonnull MsScan getMsScan(
            @Nonnull DataPointStore dataPointStore, @Nonnull Integer scanNumber,
            @Nonnull MsFunction msFunction) {
        return new SimpleMsScan(dataPointStore, scanNumber, msFunction);
    }

    public static final @Nonnull ChromatographyInfo getChromatographyInfo1D(
            SeparationType separationType, float rt1) {
        return new SimpleChromatographyInfo(rt1, null, null, separationType);
    }

    public static final @Nonnull ChromatographyInfo getChromatographyInfo2D(
            SeparationType separationType, float rt1, float rt2) {
        if (separationType.getFeatureDimensions() < 2) {
            throw new IllegalArgumentException(
                    "2D ChromatographyInfo requires at least 2 feature dimensions. Provided separation type "
                            + separationType + " has "
                            + separationType.getFeatureDimensions());
        }
        // TODO add further validation
        return new SimpleChromatographyInfo(rt1, rt2, null, separationType);
    }

    public static final @Nonnull ChromatographyInfo getImsInfo(
            SeparationType separationType, float rt1, float ionDriftTime) {
        if (separationType.getFeatureDimensions() < 2) {
            throw new IllegalArgumentException(
                    "2D ChromatographyInfo requires at least 2 feature dimensions. Provided separation type "
                            + separationType + " has "
                            + separationType.getFeatureDimensions());
        }
        if (separationType != SeparationType.IMS) {
            throw new IllegalArgumentException(
                    "2D ChromatographyInfo for IMS separation requires IMS separation type. Provided separation type "
                            + separationType);
        }
        return new SimpleChromatographyInfo(rt1, null, ionDriftTime,
                separationType);
    }

    /**
     * Creates a new SpectrumDataPointList instance with no data points
     * 
     * @return new DataPointList
     */
    public static final @Nonnull SpectrumDataPointList getSpectrumDataPointList() {
        return new SimpleSpectrumDataPointList();
    }

    /**
     * Creates a new DataPointList instance with no data points and initial
     * capacity of 100.
     * 
     * @return new DataPointList
     */
    public static final @Nonnull FeatureDataPointList getFeatureDataPointList() {
        return new SimpleFeatureDataPointList();
    }

    /**
     * Creates a new DataPointList instance with no data points and given
     * initial capacity.
     * 
     * @param capacity
     *            Initial capacity of the list
     * @return new DataPointList
     */
    public static final @Nonnull ChromatogramDataPointList getChromatogramDataPointList() {
        return new SimpleChromatogramDataPointList();
    }

    public static @Nonnull <DataType> PeakListColumn<DataType> getPeakListColumn(
            Class<DataType> dataTypeClass) {
        // TODO
        return null;
    }

    public static @Nonnull PeakListColumn<Double> getMzPeakListColumn() {
        // TODO
        return null;
    }

    public static @Nonnull PeakListColumn<ChromatographyInfo> getChromatographyInfoPeakListColumn() {
        // TODO
        return null;
    }

    public static @Nonnull PeakListColumn<Integer> getIdPeakListColumn() {
        // TODO
        return null;
    }

}
