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

import io.github.msdk.datamodel.datapointstore.DataPointStore;
import io.github.msdk.datamodel.rawdata.ChromatographyInfo;
import io.github.msdk.datamodel.rawdata.FragmentationInfo;
import io.github.msdk.datamodel.rawdata.IsolationInfo;
import io.github.msdk.datamodel.rawdata.MsFunction;
import io.github.msdk.datamodel.rawdata.MsScan;
import io.github.msdk.datamodel.rawdata.MsScanType;
import io.github.msdk.datamodel.rawdata.PolarityType;
import io.github.msdk.datamodel.rawdata.RawDataFile;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
import com.google.common.collect.Range;

/**
 * Simple implementation of the Scan interface.
 */
class SimpleMsScan extends AbstractSpectrum implements MsScan {

    private @Nullable RawDataFile dataFile;
    private @Nonnull Integer scanNumber;
    private @Nonnull MsFunction msFunction;
    private @Nonnull PolarityType polarity = PolarityType.UNKNOWN;
    private @Nonnull MsScanType msScanType = MsScanType.UNKNOWN;
    private @Nullable Range<Double> scanningRange;
    private @Nullable ChromatographyInfo chromInfo;
    private @Nullable FragmentationInfo sourceInducedFragInfo;

    private final @Nonnull List<IsolationInfo> isolations = new LinkedList<>();

    public SimpleMsScan(@Nonnull DataPointStore dataPointStore,
            @Nonnull Integer scanNumber, @Nonnull MsFunction msFunction) {
        super(dataPointStore);
        Preconditions.checkNotNull(scanNumber);
        Preconditions.checkNotNull(msFunction);
        this.scanNumber = scanNumber;
        this.msFunction = msFunction;
    }

    @Override
    @Nullable
    public RawDataFile getRawDataFile() {
        return dataFile;
    }

    @Override
    public void setRawDataFile(@Nonnull RawDataFile newRawDataFile) {
        this.dataFile = newRawDataFile;
    }

    @Override
    @Nonnull
    public Integer getScanNumber() {
        return scanNumber;
    }

    @Override
    public void setScanNumber(@Nonnull Integer scanNumber) {
        Preconditions.checkNotNull(scanNumber);
        this.scanNumber = scanNumber;
    }

    @Override
    @Nonnull
    public MsFunction getMsFunction() {
        return msFunction;
    }

    @Override
    public void setMsFunction(@Nonnull MsFunction newFunction) {
        Preconditions.checkNotNull(newFunction);
        this.msFunction = newFunction;
    }

    @Override
    @Nullable
    public Range<Double> getScanningRange() {
        return scanningRange;
    }

    @Override
    public void setScanningRange(@Nullable Range<Double> newScanRange) {
        this.scanningRange = newScanRange;
    }

    @Override
    @Nonnull
    public PolarityType getPolarity() {
        return polarity;
    }

    @Override
    public void setPolarity(@Nonnull PolarityType newPolarity) {
        Preconditions.checkNotNull(newPolarity);
        this.polarity = newPolarity;
    }

    @Override
    @Nonnull
    public MsScanType getMsScanType() {
        return msScanType;
    }

    @Override
    public void setMsScanType(@Nonnull MsScanType newMsScanType) {
        Preconditions.checkNotNull(newMsScanType);
        this.msScanType = newMsScanType;
    }

    @Override
    @Nullable
    public ChromatographyInfo getChromatographyInfo() {
        return chromInfo;
    }

    @Override
    public void setChromatographyInfo(@Nullable ChromatographyInfo chromatographyInfo) {
        this.chromInfo = chromatographyInfo;
    }

    @Override
    @Nullable
    public FragmentationInfo getSourceInducedFragmentation() {
        return sourceInducedFragInfo;
    }

    @Override
    public void setSourceInducedFragmentation(
            @Nullable FragmentationInfo newFragmentationInfo) {
        this.sourceInducedFragInfo = newFragmentationInfo;
    }

    @Override
    @Nonnull
    public List<IsolationInfo> getIsolations() {
        return isolations;
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("Scan ");
        final RawDataFile rawDataFile2 = dataFile;
        if (rawDataFile2 != null) {
            buf.append(rawDataFile2.getName());
            buf.append(" ");
        }
        buf.append(msFunction.getName());
        buf.append(" #");
        buf.append(getScanNumber());
        return buf.toString();
    }

}
