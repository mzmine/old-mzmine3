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

import java.io.File;
import java.util.Hashtable;
import java.util.List;

import net.sf.mzmine.datamodel.MZmineProject;
import net.sf.mzmine.datamodel.PeakList;
import net.sf.mzmine.datamodel.RawDataFile;
import net.sf.mzmine.datamodel.Sample;
import net.sf.mzmine.modules.MZmineProcessingStep;
import net.sf.mzmine.parameters.UserParameter;

/**
 * This class represents a MZmine project. That includes raw data files, peak
 * lists and parameters.
 */
public class MZmineProjectImpl implements MZmineProject {

	private Hashtable<UserParameter, Hashtable<RawDataFile, Object>> projectParametersAndValues;

	@Override
	public File getProjectFile() {
	    // TODO Auto-generated method stub
	    return null;
	}

	@Override
	public List<Sample> getSamples() {
	    // TODO Auto-generated method stub
	    return null;
	}

	@Override
	public void addFile(RawDataFile newFile) {
	    // TODO Auto-generated method stub
	    
	}

	@Override
	public void removeFile(RawDataFile file) {
	    // TODO Auto-generated method stub
	    
	}

	@Override
	public List<RawDataFile> getDataFiles() {
	    // TODO Auto-generated method stub
	    return null;
	}

	@Override
	public void addPeakList(PeakList peaklist) {
	    // TODO Auto-generated method stub
	    
	}

	@Override
	public void removePeakList(PeakList peaklist) {
	    // TODO Auto-generated method stub
	    
	}

	@Override
	public List<PeakList> getPeakLists() {
	    // TODO Auto-generated method stub
	    return null;
	}

	@Override
	public List<PeakList> getPeakLists(RawDataFile file) {
	    // TODO Auto-generated method stub
	    return null;
	}

	@Override
	public void performProcessingStep(MZmineProcessingStep<?> step) {
	    // TODO Auto-generated method stub
	    
	}

	@Override
	public List<MZmineProcessingStep<?>> getProcessingHistory() {
	    // TODO Auto-generated method stub
	    return null;
	}

	@Override
	public void notifyObjectChanged(Object object, boolean structureChanged) {
	    // TODO Auto-generated method stub
	    
	}


	
}