/* 
 * (C) Copyright 2015 by MSDK Development Team
 *
 * This software is dual-licensed under either
 *
 * (a) the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation
 *
 * or (per the licensee's choosing)
 *
 * (b) the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */

package io.github.msdk.datamodel.peaklists;

import java.util.List;

import javax.annotation.Nonnull;

/**
 * This data structure is not thread-safe.
 */
public interface PeakList {

    /**
     * @return Short descriptive name for the peak list
     */
    @Nonnull
    String getName();

    /**
     * Change the name of this peak list
     */
    void setName(@Nonnull String name);

    /**
     * Returns an immutable list of columns
     */
    @Nonnull
    List<PeakListColumn<?>> getColumns();

    /**
     * Add a new column to the peak list
     */
    void addColumn(@Nonnull PeakListColumn<?> col);

    /**
     * Removes a column from this peak list
     * 
     */
    void removeColumn(@Nonnull PeakListColumn<?> col);

    /**
     * Returns an immutable list of rows
     */
    @Nonnull
    List<PeakListRow> getRows();

    /**
     * Add a new row to the peak list
     */
    void addRow(@Nonnull PeakListRow row);

    /**
     * Removes a row from this peak list
     * 
     */
    void removeRow(@Nonnull PeakListRow row);

    /**
     * Shortcut to return an immutable list of samples found in this peak list.
     */
    @Nonnull
    List<Sample> getSamples();

    /**
     * Remove all data associated to this peak list from the disk.
     */
    void dispose();

}
