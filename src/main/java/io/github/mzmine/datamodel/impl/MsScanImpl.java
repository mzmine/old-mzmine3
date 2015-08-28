/*
 * Copyright 2006-2015 The MZmine 3 Development Team
 * 
 * This file is part of MZmine 2.
 * 
 * MZmine 2 is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * MZmine 2 is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * MZmine 2; if not, write to the Free Software Foundation, Inc., 51 Franklin
 * St, Fifth Floor, Boston, MA 02110-1301 USA
 */

package io.github.mzmine.datamodel.impl;

import java.text.Format;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.collect.Range;

import io.github.mzmine.datamodel.ChromatographyData;
import io.github.mzmine.datamodel.MsMsScan;
import io.github.mzmine.datamodel.MsScan;
import io.github.mzmine.datamodel.PolarityType;
import io.github.mzmine.datamodel.RawDataFile;
import io.github.mzmine.main.MZmineCore;

/**
 * Simple implementation of the Scan interface.
 */
public class MsScanImpl extends SpectrumImpl implements MsScan {

    private final RawDataFile dataFile;
    private Integer scanNumber, msLevel;
    private ChromatographyData chromData;
    private final List<MsMsScan> fragmentScans;

    public MsScanImpl(@Nonnull RawDataFile dataFile) {
        super((RawDataFileImpl) dataFile);
        this.dataFile = dataFile;
        this.fragmentScans = new ArrayList<MsMsScan>();
    }

    /**
     * @see io.github.mzmine.datamodel.MsScan#getScanNumber()
     */
    @Override
    public Integer getScanNumber() {
        return scanNumber;
    }

    @Override
    public void setScanNumber(Integer scanNumber) {
        this.scanNumber = scanNumber;
    }

    /**
     * @param scanNumber
     *            The scanNumber to set.
     */
    public void setScanNumber(int scanNumber) {
        this.scanNumber = scanNumber;
    }

    @Override
    public @Nonnull Integer getMSLevel() {
        return msLevel;
    }

    @Override
    public void setMSLevel(@Nonnull Integer msLevel) {
        this.msLevel = msLevel;
    }

    /**
     * @see io.github.mzmine.datamodel.MsScan#getFragmentScanNumbers()
     */
    @Override
    public @Nonnull List<MsMsScan> getFragmentScans() {
        return fragmentScans;
    }

    @Override
    public @Nonnull RawDataFile getDataFile() {
        return dataFile;
    }

    @Override
    public @Nonnull PolarityType getPolarity() {
        return PolarityType.UNKNOWN;
    }

    @Override
    public Range<Double> getScanRange() {
        return null;
    }

    @Override
    public double getTIC() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public ChromatographyData getChromatographyData() {
        return chromData;
    }

    @Override
    public void setChromatographyData(ChromatographyData chromData) {
        this.chromData = chromData;
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        Format rtFormat = MZmineCore.getConfiguration().getRTFormat();
        Format mzFormat = MZmineCore.getConfiguration().getMZFormat();
        buf.append("#");
        buf.append(getScanNumber());
        buf.append(" @");
        // buf.append(rtFormat.format(getRetentionTime()));
        buf.append(" MS");
        buf.append(getMSLevel());
        switch (getSpectrumType()) {
        case CENTROIDED:
            buf.append(" c");
            break;
        case PROFILE:
            buf.append(" p");
            break;
        case THRESHOLDED:
            buf.append(" t");
            break;
        }

        return buf.toString();
    }
}
