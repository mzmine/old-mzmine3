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

import org.junit.Assert;
import org.junit.Test;

import io.github.msdk.MSDKException;
import io.github.msdk.datamodel.rawdata.RawDataFileType;
import io.github.msdk.io.filetypedetection.FileTypeDetectionMethod;

public class FileTypeDetectionMethodTest {

    private static final String TEST_DATA_PATH = "src/test/resources/rawdataimport/";

    @SuppressWarnings("null")
    @Test
    public void testNetCDF() throws MSDKException {

        File testPath = new File(TEST_DATA_PATH + "netcdf");
        File inputFiles[] = testPath.listFiles();

        Assert.assertNotNull(inputFiles);
        Assert.assertNotEquals(0, inputFiles.length);

        for (File fileName : inputFiles) {
            FileTypeDetectionMethod method = new FileTypeDetectionMethod(
                    fileName);
            RawDataFileType fileType = method.execute();
            Assert.assertEquals(RawDataFileType.NETCDF, fileType);
        }
    }

    @SuppressWarnings("null")
    @Test
    public void testMzXML() throws MSDKException {

        File testPath = new File(TEST_DATA_PATH + "mzxml");
        File inputFiles[] = testPath.listFiles();

        Assert.assertNotNull(inputFiles);
        Assert.assertNotEquals(0, inputFiles.length);

        for (File fileName : inputFiles) {
            FileTypeDetectionMethod method = new FileTypeDetectionMethod(
                    fileName);
            RawDataFileType fileType = method.execute();
            Assert.assertEquals(RawDataFileType.MZXML, fileType);
        }
    }

    @SuppressWarnings("null")
    @Test
    public void testMzML() throws MSDKException {

        File testPath = new File(TEST_DATA_PATH + "mzml");
        File inputFiles[] = testPath.listFiles();

        Assert.assertNotNull(inputFiles);
        Assert.assertNotEquals(0, inputFiles.length);

        for (File fileName : inputFiles) {
            FileTypeDetectionMethod method = new FileTypeDetectionMethod(
                    fileName);
            RawDataFileType fileType = method.execute();
            Assert.assertEquals(RawDataFileType.MZML, fileType);
        }
    }

    @SuppressWarnings("null")
    @Test
    public void testMzData() throws MSDKException {

        File testPath = new File(TEST_DATA_PATH + "mzdata");
        File inputFiles[] = testPath.listFiles();

        Assert.assertNotNull(inputFiles);
        Assert.assertNotEquals(0, inputFiles.length);

        for (File fileName : inputFiles) {
            FileTypeDetectionMethod method = new FileTypeDetectionMethod(
                    fileName);
            RawDataFileType fileType = method.execute();
            Assert.assertEquals(RawDataFileType.MZDATA, fileType);
        }
    }

    @SuppressWarnings("null")
    @Test
    public void testThermoRaw() throws MSDKException {

        File testPath = new File(TEST_DATA_PATH + "thermo");
        File inputFiles[] = testPath.listFiles();

        Assert.assertNotNull(inputFiles);
        Assert.assertNotEquals(0, inputFiles.length);

        for (File fileName : inputFiles) {
            FileTypeDetectionMethod method = new FileTypeDetectionMethod(
                    fileName);
            RawDataFileType fileType = method.execute();
            Assert.assertEquals(RawDataFileType.THERMO_RAW, fileType);
        }
    }

    @SuppressWarnings("null")
    @Test
    public void testWatersRaw() throws MSDKException {

        File testPath = new File(TEST_DATA_PATH + "waters");
        File inputFiles[] = testPath.listFiles();

        Assert.assertNotNull(inputFiles);
        Assert.assertNotEquals(0, inputFiles.length);

        for (File fileName : inputFiles) {
            FileTypeDetectionMethod method = new FileTypeDetectionMethod(
                    fileName);
            RawDataFileType fileType = method.execute();
            Assert.assertEquals(RawDataFileType.WATERS_RAW, fileType);
        }
    }

}
