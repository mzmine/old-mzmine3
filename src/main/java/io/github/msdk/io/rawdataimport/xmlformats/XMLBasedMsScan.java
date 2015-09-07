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

package io.github.msdk.io.rawdataimport.xmlformats;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Range;

import io.github.msdk.datamodel.datapointstore.DataPointStore;
import io.github.msdk.datamodel.msspectra.MsSpectrumDataPointList;
import io.github.msdk.datamodel.msspectra.MsSpectrumType;
import io.github.msdk.datamodel.rawdata.ChromatographyInfo;
import io.github.msdk.datamodel.rawdata.FragmentationInfo;
import io.github.msdk.datamodel.rawdata.IsolationInfo;
import io.github.msdk.datamodel.rawdata.MsFunction;
import io.github.msdk.datamodel.rawdata.MsScan;
import io.github.msdk.datamodel.rawdata.MsScanType;
import io.github.msdk.datamodel.rawdata.PolarityType;
import io.github.msdk.datamodel.rawdata.RawDataFile;
import uk.ac.ebi.pride.tools.jmzreader.JMzReader;

/**
 * This class reads XML-based mass spec data formats (mzData, mzXML, and mzML)
 * using the jmzreader library.
 */
class XMLBasedMsScan implements MsScan {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final @Nonnull JMzReader jmzreader;
    private final @Nonnull String spectrumId;

    private @Nonnull String name;

    public XMLBasedMsScan(@Nonnull JMzReader jmzreader,
            @Nonnull String spectrumId) {

        this.jmzreader = jmzreader;
        this.spectrumId = spectrumId;
    }

    @Override
    @Nonnull
    public MsSpectrumType getSpectrumType() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setSpectrumType(@Nonnull MsSpectrumType spectrumType) {
        // TODO Auto-generated method stub

    }

    @Override
    @Nonnull
    public MsSpectrumDataPointList getDataPoints() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void getDataPoints(@Nonnull MsSpectrumDataPointList list) {
        // TODO Auto-generated method stub

    }

    @Override
    @Nonnull
    public MsSpectrumDataPointList getDataPointsByMz(
            @Nonnull Range<Double> mzRange) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @Nonnull
    public MsSpectrumDataPointList getDataPointsByIntensity(
            @Nonnull Range<Float> intensityRange) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @Nonnull
    public MsSpectrumDataPointList getDataPointsByMzAndIntensity(
            @Nonnull Range<Double> mzRange,
            @Nonnull Range<Float> intensityRange) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setDataPoints(@Nonnull MsSpectrumDataPointList newDataPoints) {
        // TODO Auto-generated method stub

    }

    @Override
    @Nonnull
    public Float getTIC() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @Nullable
    public RawDataFile getRawDataFile() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setRawDataFile(@Nonnull RawDataFile newDataFile) {
        // TODO Auto-generated method stub

    }

    @Override
    @Nonnull
    public Integer getScanNumber() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setScanNumber(@Nonnull Integer scanNumber) {
        // TODO Auto-generated method stub

    }

    @Override
    @Nonnull
    public MsFunction getMsFunction() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setMsFunction(@Nonnull MsFunction newFunction) {
        // TODO Auto-generated method stub

    }

    @Override
    @Nonnull
    public MsScanType getMsScanType() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setMsScanType(@Nonnull MsScanType newType) {
        // TODO Auto-generated method stub

    }

    @Override
    @Nullable
    public ChromatographyInfo getChromatographyInfo() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setChromatographyInfo(@Nullable ChromatographyInfo chromData) {
        // TODO Auto-generated method stub

    }

    @Override
    @Nullable
    public Range<Double> getScanningRange() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setScanningRange(@Nullable Range<Double> newScanRange) {
        // TODO Auto-generated method stub

    }

    @Override
    @Nonnull
    public PolarityType getPolarity() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setPolarity(@Nonnull PolarityType newPolarity) {
        // TODO Auto-generated method stub

    }

    @Override
    @Nullable
    public FragmentationInfo getSourceInducedFragmentation() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setSourceInducedFragmentation(
            @Nullable FragmentationInfo newFragmentationInfo) {
        // TODO Auto-generated method stub

    }

    @Override
    @Nonnull
    public List<IsolationInfo> getIsolations() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @Nonnull
    public MsScan clone(@Nonnull DataPointStore newStore) {
        // TODO Auto-generated method stub
        return null;
    }

}
