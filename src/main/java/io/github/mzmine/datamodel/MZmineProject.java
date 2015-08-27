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
 * MZmine 2; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

package io.github.mzmine.datamodel;

import java.io.File;
import java.util.List;

import javax.annotation.Nullable;

import io.github.mzmine.modules.MZmineProcessingStep;
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
     * Return the filename of the project file
     */
    @Nullable File getProjectFile();

    /**
     * Returns an immutable list of all raw data files in the project. 
     * 
     */
    List<Sample> getSamples();
    
    
    /**
     * Adds a new RawDataFile to the project.
     */
    void addFile(RawDataFile newFile);

    /**
     * Removes a RawDataFile from the project.
     */
    void removeFile(RawDataFile file);

    /**
     * Returns an immutable list of all raw data files in the project. 
     * 
     */
    List<RawDataFile> getDataFiles();

    /**
     * Adds a peak list to the project
     */
    void addPeakList(PeakList peaklist);

    /**
     * Removes a peak list from the project
     */
    void removePeakList(PeakList peaklist);

    /**
     * Returns an immutable list of all peak lists in the project.
     */
    List<PeakList> getPeakLists();

    /**
     * Returns all peak lists which contain given data file
     */
    List<PeakList> getPeakLists(RawDataFile file);
    
    void performProcessingStep(MZmineProcessingStep<?> step);
    
    List<MZmineProcessingStep<?>> getProcessingHistory();

    void notifyObjectChanged(Object object, boolean structureChanged);

}
