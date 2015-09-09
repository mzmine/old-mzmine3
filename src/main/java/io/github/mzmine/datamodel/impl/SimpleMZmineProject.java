/*
 * Copyright 2006-2015 The MZmine 3 Development Team
 * 
 * This file is part of MZmine 3.
 * 
 * MZmine 3 is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * MZmine 3 is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * MZmine 3; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

package io.github.mzmine.datamodel.impl;

import java.io.File;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.github.msdk.datamodel.peaklists.PeakList;
import io.github.msdk.datamodel.peaklists.Sample;
import io.github.msdk.datamodel.rawdata.RawDataFile;
import io.github.mzmine.datamodel.MZmineProject;

/**
 * Simple implementation of the MZmineProject interface.
 */
public class SimpleMZmineProject implements MZmineProject {

    @Override
    @Nullable
    public File getProjectFile() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @Nonnull
    public List<Sample> getSamples() {
        // TODO Auto-generated method stub
        return Collections.emptyList();
    }

    @Override
    public void addFile(RawDataFile rawDataFile) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeFile(RawDataFile rawDataFile) {
        // TODO Auto-generated method stub

    }

    @Override
    @Nonnull
    public List<RawDataFile> getRawDataFiles() {
        // TODO Auto-generated method stub
        return Collections.emptyList();
    }

    @Override
    public void addPeakList(PeakList peakList) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removePeakList(PeakList peakList) {
        // TODO Auto-generated method stub

    }

    @Override
    @Nonnull
    public List<PeakList> getPeakLists() {
        // TODO Auto-generated method stub
        return Collections.emptyList();
    }

    @Override
    @Nonnull
    public List<PeakList> getPeakLists(RawDataFile rawDataFile) {
        // TODO Auto-generated method stub
        return Collections.emptyList();
    }

}