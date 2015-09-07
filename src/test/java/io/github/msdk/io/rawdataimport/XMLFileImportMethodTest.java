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

import java.io.File;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import io.github.msdk.datamodel.impl.MSDKObjectBuilder;
import io.github.msdk.datamodel.msspectra.MsSpectrumDataPointList;
import io.github.msdk.datamodel.msspectra.MsSpectrumType;
import io.github.msdk.datamodel.rawdata.MsScan;
import io.github.msdk.datamodel.rawdata.RawDataFile;
import io.github.msdk.datamodel.rawdata.RawDataFileType;
import io.github.msdk.io.rawdataimport.xmlformats.XMLFileImportMethod;

public class XMLFileImportMethodTest {

    private static final String TEST_DATA_PATH = "src/test/resources/rawdataimport/xml";

    @Test
    public void testXMLFileImport() throws Exception {

        File inputFile = new File(TEST_DATA_PATH + "/5peptideFT.mzML");

        Assert.assertTrue(inputFile.canRead());

        XMLFileImportMethod importer = new XMLFileImportMethod(inputFile,
                RawDataFileType.MZML);

        RawDataFile rawFile = importer.execute();

        Assert.assertNotNull(rawFile);

        Assert.assertEquals(1.0, importer.getFinishedPercentage(), 0.0001);

        List<MsScan> scans = rawFile.getScans();

        Assert.assertNotNull(scans);

        // The file has 7 scans
        Assert.assertEquals(scans.size(), 7);

        // Scan #2
        MsScan profileMS1Scan = scans.get(1);

        Assert.assertEquals(profileMS1Scan.getSpectrumType(),
                MsSpectrumType.PROFILE);
        Assert.assertEquals(profileMS1Scan.getMsFunction().getMsLevel(),
                new Integer(1));

        // Scan #2 data points
        MsSpectrumDataPointList profileMS1ScanDataPoints = MSDKObjectBuilder
                .getMsSpectrumDataPointList();
        profileMS1Scan.getDataPoints(profileMS1ScanDataPoints);
        Assert.assertTrue(profileMS1ScanDataPoints.getSize() > 100);

        // Scan #5
        MsScan centroidMS2Scan = scans.get(4);
        Assert.assertEquals(centroidMS2Scan.getSpectrumType(),
                MsSpectrumType.CENTROIDED);
        Assert.assertEquals(centroidMS2Scan.getMsFunction().getMsLevel(),
                new Integer(2));

        // Scan #5 data points
        MsSpectrumDataPointList centroidMS2ScanDataPoints = MSDKObjectBuilder
                .getMsSpectrumDataPointList();
        centroidMS2Scan.getDataPoints(centroidMS2ScanDataPoints);
        Assert.assertTrue(centroidMS2ScanDataPoints.getSize() > 100);

        rawFile.dispose();

    }
}
