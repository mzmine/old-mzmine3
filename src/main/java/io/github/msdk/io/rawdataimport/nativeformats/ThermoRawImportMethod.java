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
import java.io.InputStream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.msdk.MSDKException;
import io.github.msdk.MSDKMethod;
import io.github.msdk.datamodel.datapointstore.DataPointStore;
import io.github.msdk.datamodel.impl.MSDKObjectBuilder;
import io.github.msdk.datamodel.rawdata.RawDataFile;
import io.github.msdk.datamodel.rawdata.RawDataFileType;

public class ThermoRawImportMethod implements MSDKMethod<RawDataFile> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final @Nonnull File sourceFile;
    private final @Nonnull RawDataFileType fileType = RawDataFileType.THERMO_RAW;
    private final @Nonnull DataPointStore dataStore;

    private RawDataFile newRawFile;
    private boolean canceled = false;

    private Process dumper = null;
    private RawDumpParser parser = null;

    public ThermoRawImportMethod(@Nonnull File sourceFile,
            @Nonnull DataPointStore dataStore) {
        this.sourceFile = sourceFile;
        this.dataStore = dataStore;
    }

    @SuppressWarnings("null")
    @Override
    public RawDataFile execute() throws MSDKException {

        logger.info("Started parsing file " + sourceFile);

        String fileName = sourceFile.getName();
        newRawFile = MSDKObjectBuilder.getRawDataFile(fileName, sourceFile,
                fileType, dataStore);

        // Path to the rawdump executable
        String rawDumpPath = System.getProperty("user.dir") + File.separator
                + "src" + File.separator + "main" + File.separator
                + "vendor_lib" + File.separator + "thermo" + File.separator
                + "ThermoRawDump.exe";

        if (!new File(rawDumpPath).canExecute())
            throw new MSDKException("Cannot execute program " + rawDumpPath);

        String cmdLine[] = new String[] { rawDumpPath, sourceFile.getPath() };

        String osName = System.getProperty("os.name").toUpperCase();
        if (!osName.contains("WINDOWS")) {
            throw new MSDKException(
                    "Native data format import only works on MS Windows");
        }

        try {

            // Create a separate process and execute RAWdump.exe
            dumper = Runtime.getRuntime().exec(cmdLine);

            // Get the stdout of RAWdump process as InputStream
            InputStream dumpStream = dumper.getInputStream();

            // Read the dump data
            parser = new RawDumpParser();
            parser.readRAWDump(dumpStream, newRawFile, dataStore);

            // Cleanup
            dumpStream.close();
            dumper.destroy();

            if (canceled)
                return null;

        } catch (Throwable e) {
            if (dumper != null)
                dumper.destroy();

            throw new MSDKException(e);
        }

        logger.info("Finished parsing " + sourceFile);

        return newRawFile;

    }

    @Override
    @Nullable
    public RawDataFile getResult() {
        return newRawFile;
    }

    @Override
    public Float getFinishedPercentage() {
        if (parser == null)
            return 0f;
        else
            return parser.getFinishedPercentage();
    }

    @Override
    public void cancel() {
        this.canceled = true;
        if (parser != null) {
            parser.cancel();
        }
    }

}
