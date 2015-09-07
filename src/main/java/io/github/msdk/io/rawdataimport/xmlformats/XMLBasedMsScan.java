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
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Range;

import io.github.msdk.MSDKRuntimeException;
import io.github.msdk.datamodel.impl.MSDKObjectBuilder;
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
import uk.ac.ebi.pride.tools.jmzreader.JMzReaderException;
import uk.ac.ebi.pride.tools.jmzreader.model.Spectrum;

/**
 * This class reads XML-based mass spec data formats (mzData, mzXML, and mzML)
 * using the jmzreader library.
 */
class XMLBasedMsScan implements MsScan {

    private final @Nonnull XMLBasedRawDataFile dataFile;
    private final @Nonnull String spectrumId;

    private final @Nonnull MsSpectrumType spectrumType;
    private final @Nonnull MsFunction msFunction;
    private final @Nonnull ChromatographyInfo chromatographyInfo;
    private final @Nonnull MsScanType scanType;
    private final @Nullable Range<Double> mzRange, scanningRange;
    private final @Nonnull Integer scanNumber;
    private final @Nonnull Float tic;
    private final @Nonnull PolarityType polarity;
    private final @Nullable FragmentationInfo sourceFragmentation;
    private final @Nonnull List<IsolationInfo>  isolations;

    XMLBasedMsScan(@Nonnull XMLBasedRawDataFile dataFile, @Nonnull String spectrumId,
            @Nonnull MsSpectrumType spectrumType, @Nonnull MsFunction msFunction,
            @Nonnull ChromatographyInfo chromatographyInfo, @Nonnull MsScanType scanType,
            @Nullable Range<Double> mzRange, @Nullable Range<Double> scanningRange,
            @Nonnull  Integer scanNumber, @Nonnull Float tic,@Nonnull  PolarityType polarity,
            @Nullable FragmentationInfo sourceFragmentation,
            @Nonnull List<IsolationInfo> isolations) {
        this.dataFile = dataFile;
        this.spectrumId = spectrumId;
        this.spectrumType = spectrumType;
        this.msFunction = msFunction;
        this.chromatographyInfo = chromatographyInfo;
        this.scanType = scanType;
        this.mzRange = mzRange;
        this.scanningRange = scanningRange;
        this.scanNumber = scanNumber;
        this.tic = tic;
        this.polarity = polarity;
        this.sourceFragmentation = sourceFragmentation;
        this.isolations = isolations;
    }

    @Override
    @Nonnull
    public MsSpectrumType getSpectrumType() {
        return spectrumType;
    }



    @Override
    @Nonnull
    public MsSpectrumDataPointList getDataPoints() {
        MsSpectrumDataPointList dpList = MSDKObjectBuilder.getSpectrumDataPointList();
        getDataPoints(dpList);
        return dpList;
    }

    @Override
    public void getDataPoints(@Nonnull MsSpectrumDataPointList list) {
        try {
            JMzReader jmzReader = dataFile.getJmzReader();
            Spectrum jmzSpectrum = jmzReader.getSpectrumById(spectrumId);
            Map <Double, Double> mzValues = jmzSpectrum.getPeakList();
            list.clear();
            list.allocate(mzValues.size());
            for (Double mz : mzValues.keySet()) {
                Double intensity = mzValues.get(mz);
                Preconditions.checkNotNull(mz);
                Preconditions.checkNotNull(intensity);
                list.add(mz.doubleValue(), intensity.floatValue());
            }
        } catch (JMzReaderException e) {
            throw(new MSDKRuntimeException(e));
        }
    }

    @Override
    @Nonnull
    public MsSpectrumDataPointList getDataPointsByMz(
            @Nonnull Range<Double> mzRange) {
        try {
            JMzReader jmzReader = dataFile.getJmzReader();
            Spectrum jmzSpectrum = jmzReader.getSpectrumById(spectrumId);
            Map <Double, Double> mzValues = jmzSpectrum.getPeakList();
            list.clear();
            list.allocate(mzValues.size());
            for (Double mz : mzValues.keySet()) {
                Double intensity = mzValues.get(mz);
                Preconditions.checkNotNull(mz);
                Preconditions.checkNotNull(intensity);
                list.add(mz.doubleValue(), intensity.floatValue());
            }
        } catch (JMzReaderException e) {
            throw(new MSDKRuntimeException(e));
        }
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
    @Nullable
    public Range<Double> getMzRange() {
        return mzRange;
    }
    
    @Override
    @Nonnull
    public Float getTIC() {
        return tic;
    }

    @Override
    @Nullable
    public RawDataFile getRawDataFile() {
        return dataFile;
    }

    @Override
    @Nonnull
    public Integer getScanNumber() {
        return scanNumber;
    }



    @Override
    @Nonnull
    public MsFunction getMsFunction() {
        return msFunction;
    }

    @Override
    @Nonnull
    public MsScanType getMsScanType() {
        return scanType;
    }
    
    @Override
    @Nullable
    public ChromatographyInfo getChromatographyInfo() {
        return chromatographyInfo;
    }
    
    @Override
    @Nullable
    public Range<Double> getScanningRange() {
        return scanningRange;
    }

    @Override
    @Nonnull
    public PolarityType getPolarity() {
        return polarity;
    }


    @Override
    @Nullable
    public FragmentationInfo getSourceInducedFragmentation() {
        return sourceFragmentation;
    }
    
    @Override
    @Nonnull
    public List<IsolationInfo> getIsolations() {
        return isolations;
    }
    
    /* 
     * Unsupported set-operations 
     */
    
    
    @Override
    public void setDataPoints(@Nonnull MsSpectrumDataPointList newDataPoints) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void setSpectrumType(@Nonnull MsSpectrumType spectrumType) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void setRawDataFile(@Nonnull RawDataFile newDataFile) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void setScanNumber(@Nonnull Integer scanNumber) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setMsFunction(@Nonnull MsFunction newFunction) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void setMsScanType(@Nonnull MsScanType newType) {
        throw new UnsupportedOperationException();
    }


    @Override
    public void setChromatographyInfo(@Nullable ChromatographyInfo chromData) {
        throw new UnsupportedOperationException();
    }


    @Override
    public void setScanningRange(@Nullable Range<Double> newScanRange) {
        throw new UnsupportedOperationException();
    }


    @Override
    public void setPolarity(@Nonnull PolarityType newPolarity) {
        throw new UnsupportedOperationException();
    }


    @Override
    public void setSourceInducedFragmentation(
            @Nullable FragmentationInfo newFragmentationInfo) {
        throw new UnsupportedOperationException();
    }

}

