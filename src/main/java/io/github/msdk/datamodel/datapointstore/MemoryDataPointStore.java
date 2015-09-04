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

package io.github.msdk.datamodel.datapointstore;

import io.github.msdk.datamodel.datapointstore.DataPointStore;
import io.github.msdk.datamodel.impl.MSDKObjectBuilder;
import io.github.msdk.datamodel.rawdata.SpectrumDataPointList;

import java.util.HashMap;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A DataPointStore implementation that stores the data points in memory. Use
 * with caution. When DataPointLists are stored or retrieved, they are not
 * referenced but copied, so the original list can be used for other purpose.
 * 
 * The methods of this class are synchronized, therefore it can be safely used
 * by multiple threads.
 */
class MemoryDataPointStore implements DataPointStore {

    private HashMap<Object, Object> storageMap = new HashMap<>();

    private int lastStorageId = 0;

    /**
     * Stores new array of data points.
     * 
     * @return Storage ID for the newly stored data.
     */
    @Override
    public @Nonnull Integer storeDataPoints(
            @Nonnull SpectrumDataPointList dataPoints) {
        // Clone the given list for storage
        final SpectrumDataPointList newList = MSDKObjectBuilder
                .getDataPointList(dataPoints);     
        return storeObject(newList);
    }

    private @Nonnull Integer storeObject(
            @Nonnull Object obj) {

        if (storageMap == null)
            throw new IllegalStateException("This object has been disposed");

        // Clone the given list for storage
        final SpectrumDataPointList newList = MSDKObjectBuilder
                .getDataPointList(dataPoints);


        // Save the reference to the new list
        synchronized (storageMap) {
            // Increase the storage ID
            lastStorageId++;
            storageMap.put(lastStorageId, newList);
        }

        return lastStorageId;
        
    }

    /**
     * Reads the data points associated with given ID.
     */
    @Override synchronized public @Nonnull SpectrumDataPointList readDataPoints(
            @Nonnull Object ID) {

        if (dataPointLists == null)
            throw new IllegalStateException("This object has been disposed");

        if (!dataPointLists.containsKey(ID))
            throw new IllegalArgumentException(
                    "ID " + ID + " not found in storage");

        // Get the stored DataPointList
        final SpectrumDataPointList storedList = dataPointLists.get(ID);

        // Clone the stored DataPointList
        final SpectrumDataPointList newList = MSDKObjectBuilder
                .getDataPointList(storedList);

        return newList;
    }

    /**
     * Reads the data points associated with given ID.
     */
    @Override
    synchronized public void readDataPoints(@Nonnull Object ID,
            @Nonnull SpectrumDataPointList list) {

        if (storageMap == null)
            throw new IllegalStateException("This object has been disposed");

        if (!storageMap.containsKey(ID))
            throw new IllegalArgumentException(
                    "ID " + ID + " not found in storage");

        // Get the stored DataPointList
        final SpectrumDataPointList storedList = storageMap.get(ID);

        // Copy data
        list.copyFrom(storedList);

    }

    /**
     * Remove data associated with given storage ID.
     */
    @Override
    synchronized public void removeDataPoints(@Nonnull Object ID) {

        if (storageMap == null)
            throw new IllegalStateException("This object has been disposed");

        storageMap.remove(ID);
    }

    @Override
    synchronized public void dispose() {
        storageMap = null;
    }

}
