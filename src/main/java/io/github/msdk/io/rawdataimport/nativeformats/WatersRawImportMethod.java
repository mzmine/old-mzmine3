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

package io.github.msdk.io.rawdataimport.nativeformats;

import java.io.File;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.msdk.MSDKException;
import io.github.msdk.MSDKMethod;
import io.github.msdk.datamodel.datapointstore.DataPointStore;
import io.github.msdk.datamodel.impl.MSDKObjectBuilder;
import io.github.msdk.datamodel.msspectra.MsSpectrumDataPointList;
import io.github.msdk.datamodel.rawdata.RawDataFile;
import io.github.msdk.datamodel.rawdata.RawDataFileType;

public class WatersRawImportMethod implements MSDKMethod<RawDataFile> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private int parsedScans, totalScans = 0;

    private final @Nonnull File sourceFile;
    private final @Nonnull RawDataFileType fileType = RawDataFileType.WATERS_RAW;
    private final @Nonnull DataPointStore dataStore;

    private RawDataFile newRawFile;
    private boolean canceled = false;

    private MsSpectrumDataPointList dataPoints = MSDKObjectBuilder
            .getMsSpectrumDataPointList();

    public WatersRawImportMethod(@Nonnull File sourceFile,
            @Nonnull DataPointStore dataStore) {
        this.sourceFile = sourceFile;
        this.dataStore = dataStore;
    }

    @Override
    public RawDataFile execute() throws MSDKException {

        logger.info("Started parsing file " + sourceFile);

        String fileName = sourceFile.getName();
        newRawFile = MSDKObjectBuilder.getRawDataFile(fileName, sourceFile,
                fileType, dataStore);

        logger.info("Finished parsing " + sourceFile + ", parsed " + parsedScans
                + " scans");

        return newRawFile;

    }

    @Override
    @Nullable
    public RawDataFile getResult() {
        return newRawFile;
    }

    @Override
    public Float getFinishedPercentage() {
        return totalScans == 0 ? null : (float) parsedScans / totalScans;
    }

    @Override
    public void cancel() {
        this.canceled = true;
    }

}
