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

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.github.msdk.datamodel.datapointstore.DataPointStore;

/**
 * Represents a single MS scan in a raw data file. This interface extends
 * IMassSpectrum, therefore the actual data points can be accessed through the
 * inherited methods of IMassSpectrum.
 * 
 * If the scan is not added to any file, its data points are stored in memory.
 * However, once the scan is added into a raw data file by calling
 * setRawDataFile(), its data points will be stored in a temporary file that
 * belongs to that RawDataFile. When RawDataFile.dispose() is called, the data
 * points are discarded so the MsScan instance cannot be used anymore.
 */
public interface Chromatogram extends Cloneable {

    /**
     * Returns the raw data file that contains this chromatogram. This might
     * return null when the chromatogram is created, but once the chromatogram
     * is added to the raw data file by calling RawDataFile.addChromatogram(),
     * the RawDataFile automatically calls the Chromatogram.setRawDataFile()
     * method to update this reference.
     * 
     * @return RawDataFile containing this chromatogram, or null.
     * @see RawDataFile
     */
    @Nullable
    RawDataFile getRawDataFile();

    /**
     * Updates the raw data file reference. This method can be called only once.
     * Any subsequent calls will throw the IllegalOperationException.
     * 
     * @param newDataFile
     *            New RawDataFile reference.
     * @throws IllegalOperationException
     *             If the reference to the raw data file has already been set.
     */
    void setRawDataFile(@Nonnull RawDataFile newDataFile);

    /**
     * Returns the number of this chromatogram, represented by an integer,
     * typically positive. Typically, the chromatogram number will be unique
     * within the file. However, the data model does not guarantee that, and in
     * some cases multiple chromatogram with the same number may be present in
     * the file.
     * 
     * @return Chromatogram number
     */
    @Nonnull
    Integer getChromatogramNumber();

    /**
     * Updates the chromatogram number.
     * 
     * @param chromatogramNumber
     *            New chromatogram number.
     */
    void setChromatogramNumber(@Nonnull Integer chromatogramNumber);

    /**
     * Returns data points of this chromatogram. Importantly, a new instance of
     * DataPointList is created by each call to this method.
     * 
     * Note: this method may need to read data from disk, therefore it may be
     * quite slow.
     * 
     * @return data points (rt and intensity pairs) of this chromatogram
     */
    @Nonnull
    ChromatogramDataPointList getDataPoints();

    /**
     * Loads the data points of this chromatogram into the given DataPointList. If
     * the DataPointList is not empty, it is cleared first. This method allows
     * the internal arrays of the DataPointList to be reused for loading
     * multiple spectra.
     * 
     * Note: this method may need to read data from disk, therefore it may be
     * quite slow.
     * 
     * @param list
     *            DataPointList into which the data points should be loaded
     */
    void getDataPoints(@Nonnull ChromatogramDataPointList list);

    /**
     * Returns a list of isolations performed for this chromatogram. These
     * isolations may also include fragmentations (tandem MS).
     * 
     * @return A mutable list of isolations. New isolation items can be added to
     *         this list.
     */
    @Nonnull
    List<IsolationInfo> getIsolations();

    /**
     * Returns the separation type used for separation of molecules.
     * 
     * @return the seperation type. Returns {@link SeparationType#UNKNOWN} for
     *         unknown separations.
     */
    SeparationType getSeparationType();

    /**
     * Sets the separation type used for separation of molecules.
     * 
     * @param separationType
     *            New seperation type.
     */
    void setSeparationType(SeparationType separationType);

    /**
     * Returns a deep clone of this object.
     * 
     * @param newStore
     *            data points of the newly created MsScan will be stored in this
     *            store
     * @return A clone of this MsScan.
     */
    @Nonnull
    Chromatogram clone(@Nonnull DataPointStore newStore);

}
