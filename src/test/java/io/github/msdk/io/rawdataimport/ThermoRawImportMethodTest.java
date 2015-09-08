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

package io.github.msdk.io.rawdataimport;

import static org.junit.Assume.assumeTrue;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import io.github.msdk.datamodel.datapointstore.DataPointStore;
import io.github.msdk.datamodel.datapointstore.DataPointStoreFactory;
import io.github.msdk.datamodel.impl.MSDKObjectBuilder;
import io.github.msdk.datamodel.msspectra.MsSpectrumDataPointList;
import io.github.msdk.datamodel.rawdata.RawDataFile;
import io.github.msdk.io.rawdataimport.thermo.ThermoRawImportMethod;

public class ThermoRawImportMethodTest {

    private static final String TEST_DATA_PATH = "src/test/resources/rawdataimport/thermo/";

    @SuppressWarnings("null")
    @Test
    public void testRP240K01() throws Exception {

        // Run this test only on Windows
        assumeTrue(System.getProperty("os.name").startsWith("Windows"));

        // Create the data structures
        DataPointStore dataStore = DataPointStoreFactory
                .getTmpFileDataPointStore();
        MsSpectrumDataPointList dataPoints = MSDKObjectBuilder
                .getMsSpectrumDataPointList();

        // Import the file
        File inputFile = new File(TEST_DATA_PATH + "RP240K_01.raw");
        Assert.assertTrue(inputFile.canRead());
        ThermoRawImportMethod importer = new ThermoRawImportMethod(inputFile,
                dataStore);
        RawDataFile rawFile = importer.execute();
        Assert.assertNotNull(rawFile);
        Assert.assertEquals(1.0, importer.getFinishedPercentage(), 0.0001);

    }

}
