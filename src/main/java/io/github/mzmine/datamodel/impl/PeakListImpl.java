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

package io.github.mzmine.datamodel.impl;

import java.util.List;

import io.github.mzmine.datamodel.PeakList;
import io.github.mzmine.datamodel.PeakListRow;
import io.github.mzmine.datamodel.RawDataFile;

/**
 * Simple implementation of the PeakList interface.
 */
public class PeakListImpl extends DataPointStoreImpl implements PeakList {

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
    public int getNumberOfRawDataFiles() {
	// TODO Auto-generated method stub
	return 0;
    }

    @Override
    public RawDataFile[] getRawDataFiles() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public boolean hasRawDataFile(RawDataFile file) {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public RawDataFile getRawDataFile(int position) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public int getNumberOfRows() {
	// TODO Auto-generated method stub
	return 0;
    }

    @Override
    public List<PeakListRow> getRows() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public void addRow(PeakListRow row) {
	// TODO Auto-generated method stub
	
    }

    @Override
    public void removeRow(PeakListRow row) {
	// TODO Auto-generated method stub
	
    }

   
}
