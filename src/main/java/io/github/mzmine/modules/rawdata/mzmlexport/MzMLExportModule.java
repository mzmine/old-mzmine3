/*
 * Copyright 2006-2016 The MZmine 3 Development Team
 * 
 * This file is part of MZmine 3.
 * 
 * MZmine 3 is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * MZmine 3 is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * MZmine 3; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

package io.github.mzmine.modules.rawdata.mzmlexport;

import java.io.File;
import java.util.Collection;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.msdk.datamodel.rawdata.RawDataFile;
import io.github.msdk.io.mzml.MzMLFileExportMethod;
import io.github.mzmine.modules.MZmineProcessingModule;
import io.github.mzmine.parameters.ParameterSet;
import io.github.mzmine.parameters.parametertypes.selectors.RawDataFilesSelection;
import io.github.mzmine.project.MZmineProject;
import io.github.mzmine.taskcontrol.MSDKTask;
import javafx.concurrent.Task;

/**
 * Raw data import module
 */
public class MzMLExportModule implements MZmineProcessingModule {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String MODULE_NAME = "mzML export";
    private static final String MODULE_DESCRIPTION = "This module exports raw data into mzML";

    @Override
    public @Nonnull String getName() {
        return MODULE_NAME;
    }

    @Override
    public @Nonnull String getDescription() {
        return MODULE_DESCRIPTION;
    }

    @SuppressWarnings("null")
    @Override
    public void runModule(@Nonnull MZmineProject project,
            @Nonnull ParameterSet parameters,
            @Nonnull Collection<Task<?>> tasks) {

        final RawDataFilesSelection rawDataFiles = parameters
                .getParameter(MzMLExportParameters.dataFiles).getValue();
        final File fileName = parameters
                .getParameter(MzMLExportParameters.fileName).getValue();

        if (rawDataFiles.getMatchingRawDataFiles().isEmpty()) {
            logger.warn(
                    "Centroiding module started with no raw data files selected");
            return;
        }

        if (rawDataFiles.getMatchingRawDataFiles().isEmpty()) {
            logger.warn(
                    "mzML export module started with no raw data files selected");
            return;
        }

        RawDataFile rawDataFile = rawDataFiles.getMatchingRawDataFiles().get(0);

        MzMLFileExportMethod method = new MzMLFileExportMethod(rawDataFile,
                fileName);

        MSDKTask newTask = new MSDKTask("mzML export", rawDataFile.getName(),
                method);

        // Add the task to the queue
        tasks.add(newTask);

    }

    @Override
    public @Nonnull Class<? extends ParameterSet> getParameterSetClass() {
        return MzMLExportParameters.class;
    }
}
