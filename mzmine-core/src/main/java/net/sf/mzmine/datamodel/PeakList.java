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
 * WARRANTY; without even the im plied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * MZmine 2; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

package net.sf.mzmine.datamodel;

import java.util.List;

import com.google.common.collect.Range;

/**
 * 
 */
public interface PeakList {

    /**
     * @return Short descriptive name for the peak list
     */
    String getName();

    /**
     * Change the name of this peak list
     */
    void setName(String name);

    /**
     * Returns number of raw data files participating in the peak list
     */
    int getNumberOfRawDataFiles();

    /**
     * Returns all raw data files participating in the peak list
     */
    RawDataFile[] getRawDataFiles();

    /**
     * Returns true if this peak list contains given file
     */
    boolean hasRawDataFile(RawDataFile file);

    /**
     * Returns a raw data file
     * 
     * @param position
     *            Position of the raw data file in the matrix (running numbering
     *            from left 0,1,2,...)
     */
    RawDataFile getRawDataFile(int position);

    /**
     * Returns number of rows in the alignment result
     */
    int getNumberOfRows();


    /**
     * Returns all peak list rows
     */
    List<PeakListRow> getRows();

    /**
     * Add a new row to the peak list
     */
    void addRow(PeakListRow row);

    /**
     * Removes a row from this peak list
     * 
     */
    void removeRow(PeakListRow row);

    /**
     * Remove all data associated to this peak list from the disk.
     */
    void dispose();


}
