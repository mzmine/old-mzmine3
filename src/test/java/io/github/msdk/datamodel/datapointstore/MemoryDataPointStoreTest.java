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

import org.junit.Test;

import io.github.msdk.datamodel.datapointstore.DataPointStore;
import io.github.msdk.datamodel.datapointstore.DataPointStoreFactory;

/**
 * Tests for MemoryDataPointStore
 */
public class MemoryDataPointStoreTest {

    @Test
    public void testStoreReadDataPoints() {

        DataPointStore store = DataPointStoreFactory.getMemoryDataStore();
        DataPointStoreTest.testStoreAndRetrieveReadDataPoints(store);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveDataPoints() {

        DataPointStore store = DataPointStoreFactory.getMemoryDataStore();
        DataPointStoreTest.testRemoveDataPoints(store);

    }

    @Test(expected = IllegalStateException.class)
    public void testDispose() {

        DataPointStore store = DataPointStoreFactory.getMemoryDataStore();
        DataPointStoreTest.testDispose(store);

    }

}
