/*
 * Copyright 2006-2014 The MZmine 2 Development Team
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

package net.sf.mzmine.datamodel.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import net.sf.mzmine.datamodel.ChromatographyData;
import net.sf.mzmine.datamodel.MassSpectrumType;
import net.sf.mzmine.datamodel.MsLevel;
import net.sf.mzmine.datamodel.MsMsScan;
import net.sf.mzmine.datamodel.MsScan;
import net.sf.mzmine.datamodel.PolarityType;
import net.sf.mzmine.datamodel.RawDataFile;
import net.sf.mzmine.util.ScanUtils;

import com.google.common.collect.Range;

/**
 * Simple implementation of the Scan interface.
 */
public class MsScanImpl extends SpectrumImpl implements MsScan {

    private final RawDataFile dataFile;
    private int scanNumber;
    private MsLevel msLevel;
    private double retentionTime;
    private final List<MsMsScan> fragmentScans;

    public MsScanImpl(@Nonnull RawDataFile dataFile) {
	super((RawDataFileImpl) dataFile);
	this.dataFile = dataFile;
	this.fragmentScans = new ArrayList<MsMsScan>();
    }

    /**
     * @see net.sf.mzmine.datamodel.MsScan#getScanNumber()
     */
    @Override
    public int getScanNumber() {
	return scanNumber;
    }

    /**
     * @param scanNumber
     *            The scanNumber to set.
     */
    public void setScanNumber(int scanNumber) {
	this.scanNumber = scanNumber;
    }

    @Override
    public @Nonnull MsLevel getMSLevel() {
	return msLevel;
    }

    @Override
    public void setMSLevel(@Nonnull MsLevel msLevel) {
	this.msLevel = msLevel;
    }

    /**
     * @see net.sf.mzmine.datamodel.MsScan#getFragmentScanNumbers()
     */
    @Override
    public @Nonnull List<MsMsScan> getFragmentScans() {
	return fragmentScans;
    }

    @Override
    public String toString() {
	return ScanUtils.scanToString(this);
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
    public MassSpectrumType getSpectrumType() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public ChromatographyData getChromatographyData() {
	// TODO Auto-generated method stub
	return null;
    }

}
