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

package io.github.mzmine.datamodel;

import java.io.File;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.github.msdk.datamodel.peaklists.PeakList;
import io.github.msdk.datamodel.peaklists.Sample;
import io.github.msdk.datamodel.rawdata.RawDataFile;
import io.github.mzmine.parameters.UserParameter;

/**
 * 
 * MZmineProject collects all items user has opened or created during an MZmine
 * session. This includes
 * <ul>
 * <li>Experimental parameters and their values for each RawDataFile.
 * Experimental parameters are available for defining any properties of the
 * sample, for instance concentration or a class label.
 * <li>Opened RawDataFiles
 * <li>PeakLists of each RawDataFile. A peak list represents results of peak
 * detection on a single RawDataFile or a processed version of a preceding
 * PeakList.
 * <li>PeakLists of multiple aligned PeakLists. An aligned peak list represent
 * results of aligning multiple PeakLists of individual runs or a processed
 * version of a preceding aligned PeakList.
 * </ul>
 * 
 * @see UserParameter
 * @see ParameterValue
 * @see RawDataFile
 * @see PeakList
 * 
 */
public interface MZmineProject {

    /**
     * Returns the name and path of the project file or null if the project has
     * not been saved.
     * 
     * @return Project file name and path
     */
    @Nullable
    File getProjectFile();

    /**
     * Returns an immutable list of all samples in the project. The list can be
     * safely iterated over, as it cannot be modified by another thread.
     * 
     * @return A list of all samples.
     */
    @Nonnull
    List<Sample> getSamples();

    /**
     * Adds a new raw data file to this project.
     * 
     * @param rawDataFile
     *            Raw data file to add.
     */
    void addFile(RawDataFile rawDataFile);

    /**
     * Removes a raw data file to this project.
     * 
     * @param rawDataFile
     *            Raw data file to remove.
     */
    void removeFile(RawDataFile rawDataFile);

    /**
     * Returns an immutable list of all raw data files in the project. The list
     * can be safely iterated over, as it cannot be modified by another thread.
     * 
     * @return A list of all raw data files.
     */
    @Nonnull
    List<RawDataFile> getRawDataFiles();

    /**
     * Adds a new peak list to this project.
     * 
     * @param peakList
     *            Peak list to add.
     */
    void addPeakList(PeakList peakList);

    /**
     * Removes a peak list from this project.
     * 
     * @param peakList
     *            Peak list to remove.
     */
    void removePeakList(PeakList peakList);

    /**
     * Returns an immutable list of all peak lists in the project. The list can
     * be safely iterated over, as it cannot be modified by another thread.
     * 
     * @return A list of all peak lists.
     */
    @Nonnull
    List<PeakList> getPeakLists();

    /**
     * Returns an immutable list of all peak lists in the project which contain
     * a given raw data file. The list can be safely iterated over, as it cannot
     * be modified by another thread.
     * 
     * @param rawDataFile
     *            Raw data file to look for.
     * @return A list of all peak lists.
     */
    @Nonnull
    List<PeakList> getPeakLists(RawDataFile rawDataFile);

}