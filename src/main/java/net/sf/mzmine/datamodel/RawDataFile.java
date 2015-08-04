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

package net.sf.mzmine.datamodel;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Range;

/**
 * Raw data file
 */
public interface RawDataFile {

    /**
     * Returns the name of this data file (can be a descriptive name, not
     * necessarily the original file name)
     */
    @Nonnull
    String getName();

    /**
     * Change the name of this data file
     */
    void setName(@Nonnull String name);

    void addScan(@Nonnull MsScan scan);

    void removeScan(@Nonnull MsScan scan);

    /**
     * Returns an immutable list of all scans. The list can be safely iterated
     * on, as it cannot be modified by another thread.
     */
    @Nonnull
    List<MsScan> getScans();

    /**
     * Returns an immutable list of all scans. The list can be safely iterated
     * on, as it cannot be modified by another thread.
     */
    @Nonnull
    List<MsScan> getScans(@Nonnull Integer msLevel,
	    @Nonnull Range<Double> rtRange);

    /**
     * Returns immutable list of MS levels of scans in this file. Items in the
     * list are sorted in ascending order.
     */
    @Nonnull
    List<Integer> getMSLevels();

    /**
     * 
     * @param scan
     *            Desired scan number
     * @return Desired scan, or null if no scan exists with that number
     */
    @Nullable
    MsScan getScan(int scanNumber);

    @Nonnull
    Range<Double> getRawDataMZRange();

    @Nonnull
    Range<Double> getRawDataScanRange();

    @Nonnull
    Range<Double> getRawDataRTRange();

    @Nonnull
    Range<Double> getRawDataMZRange(@Nonnull Integer msLevel);

    @Nonnull
    Range<Double> getRawDataScanRange(@Nonnull Integer msLevel);

    @Nonnull
    Range<Double> getRawDataRTRange(@Nonnull Integer msLevel);

    /**
     * Returns the original file that was imported into MZmine. After import,
     * the file returns itself. Once processed by filtration, centroiding etc.,
     * newly created files return the link to the original file.
     */
    @Nonnull
    RawDataFile getOriginalRawDataFile();

    /**
     * Remove all data associated to this file from the disk.
     */
    void dispose();
}
