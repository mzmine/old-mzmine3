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

package io.github.msdk.io.featuretableimport;

import java.io.File;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import io.github.msdk.MSDKException;
import io.github.msdk.datamodel.datapointstore.DataPointStore;
import io.github.msdk.datamodel.datapointstore.DataPointStoreFactory;
import io.github.msdk.datamodel.featuretables.FeatureTable;
import io.github.msdk.datamodel.featuretables.Sample;
import io.github.msdk.io.featuretableimport.mztab.MzTabFileImportMethod;

public class MzTabFileImportMethodTest {

    private static final String TEST_DATA_PATH = "src/test/resources/rawdataimport/mztab/";

    @Test
    public void testMzTab() throws MSDKException {

        // Create the data structures
        DataPointStore dataStore = DataPointStoreFactory
                .getTmpFileDataPointStore();

        // Import the file
        File inputFile = new File(TEST_DATA_PATH + "Sample-2.3.mzTab");
        Assert.assertTrue(inputFile.canRead());
        MzTabFileImportMethod importer = new MzTabFileImportMethod(inputFile, dataStore);
        FeatureTable featureTable = importer.execute();
        Assert.assertNotNull(featureTable);
        Assert.assertEquals(1.0, importer.getFinishedPercentage(), 0.0001);

        // The table has 7 samples
        List<Sample> samples = featureTable.getSamples();
        Assert.assertNotNull(samples);      
        Assert.assertEquals(7, samples.size());

        // The table has columns
        Assert.assertFalse(featureTable.getColumns().isEmpty());

        featureTable.dispose();
    }
}
