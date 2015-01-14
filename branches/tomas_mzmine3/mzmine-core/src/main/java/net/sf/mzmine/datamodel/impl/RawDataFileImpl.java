/*
 * Copyright 2006-2015 The MZmine 2 Development Team
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

import net.sf.mzmine.datamodel.MsScan;
import net.sf.mzmine.datamodel.RawDataFile;

import com.google.common.collect.Range;

/**
 * RawDataFile implementation.
 */
class RawDataFileImpl extends DataPointStoreImpl implements RawDataFile {

    private @Nonnull String rawDataFileName;
    private final List<MsScan> scans;

    RawDataFileImpl() {
	rawDataFileName = "New file";
	scans = new ArrayList<MsScan>();
    }

    @Override
    public String getName() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public void setName(String name) {
	// TODO Auto-generated method stub
	
    }

    @Override
    public void addScan(MsScan scan) {
	// TODO Auto-generated method stub
	
    }

    @Override
    public void removeScan(MsScan scan) {
	// TODO Auto-generated method stub
	
    }

    @Override
    public List<MsScan> getScans() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public List<MsScan> getScans(Integer msLevel, Range<Double> rtRange) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public List<Integer> getMSLevels() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public MsScan getScan(int scanNumber) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public Range<Double> getRawDataMZRange() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public Range<Double> getRawDataScanRange() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public Range<Double> getRawDataRTRange() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public Range<Double> getRawDataMZRange(Integer msLevel) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public Range<Double> getRawDataScanRange(Integer msLevel) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public Range<Double> getRawDataRTRange(Integer msLevel) {
	// TODO Auto-generated method stub
	return null;
    }



}