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

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.github.msdk.datamodel.chromatograms.Chromatogram;
import io.github.msdk.datamodel.chromatograms.ChromatogramDataPointList;
import io.github.msdk.datamodel.chromatograms.ChromatogramType;
import io.github.msdk.datamodel.datapointstore.DataPointStore;
import io.github.msdk.datamodel.rawdata.IsolationInfo;
import io.github.msdk.datamodel.rawdata.MsScanType;
import io.github.msdk.datamodel.rawdata.RawDataFile;
import io.github.msdk.datamodel.rawdata.SeparationType;

/**
 * Simple implementation of the Chromatogram interface.
 */
class SimpleChromatogram implements Chromatogram {

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
    public Integer getChromatogramNumber() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setChromatogramNumber(@Nonnull Integer chromatogramNumber) {
        // TODO Auto-generated method stub
        
    }

    @Override
    @Nonnull
    public MsScanType getChromatogramType() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setChromatogramType(
            @Nonnull ChromatogramType newChromatogramType) {
        // TODO Auto-generated method stub
        
    }

    @Override
    @Nonnull
    public ChromatogramDataPointList getDataPoints() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void getDataPoints(@Nonnull ChromatogramDataPointList list) {
        // TODO Auto-generated method stub
        
    }

    @Override
    @Nonnull
    public List<IsolationInfo> getIsolations() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SeparationType getSeparationType() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setSeparationType(SeparationType separationType) {
        // TODO Auto-generated method stub
        
    }

    @Override
    @Nonnull
    public Chromatogram clone(@Nonnull DataPointStore newStore) {
        // TODO Auto-generated method stub
        return null;
    }

}
