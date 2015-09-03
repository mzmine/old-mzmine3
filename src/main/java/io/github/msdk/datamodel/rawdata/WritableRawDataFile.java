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

package io.github.msdk.datamodel.rawdata;

import javax.annotation.Nonnull;

/**
 * A writable raw data file.
 * 
 * @see RawDataFile
 */
public interface WritableRawDataFile extends RawDataFile {

    /**
     * Adds a new scan to this file.
     * 
     * @param scan
     *            Scan to add.
     */
    void addScan(@Nonnull MsScan scan);

    /**
     * Removes a scan from this file.
     * 
     * @param scan
     *            Scan to remove.
     */
    void removeScan(@Nonnull MsScan scan);

}
