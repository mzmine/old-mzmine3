/*
 * Copyright 2006-2015 The MZmine 3 Development Team
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

package io.github.mzmine.modules.rawdataimport;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nonnull;

import com.google.common.base.Strings;

import io.github.msdk.MSDKException;
import io.github.msdk.datamodel.datapointstore.DataPointStore;
import io.github.msdk.datamodel.datapointstore.DataPointStoreFactory;
import io.github.msdk.datamodel.rawdata.RawDataFile;
import io.github.msdk.io.rawdataimport.RawDataFileImportMethod;
import io.github.mzmine.gui.MZmineGUI;
import io.github.mzmine.modules.MZmineModuleCategory;
import io.github.mzmine.modules.MZmineProcessingModule;
import io.github.mzmine.parameters.ParameterSet;
import io.github.mzmine.project.MZmineProject;
import io.github.mzmine.taskcontrol.MSDKTask;
import javafx.concurrent.Task;

/**
 * Raw data import module
 */
public class RawDataImportModule implements MZmineProcessingModule {

    private Logger logger = Logger.getLogger(this.getClass().getName());
    private static final String MODULE_NAME = "Raw data import";
    private static final String MODULE_DESCRIPTION = "This module imports raw data into the project.";

    private final RawDataImportParameters parameters = new RawDataImportParameters();

    @SuppressWarnings("null")
    @Override
    public @Nonnull String getName() {
        return MODULE_NAME;
    }

    @SuppressWarnings("null")
    @Override
    public @Nonnull String getDescription() {
        return MODULE_DESCRIPTION;
    }

    @SuppressWarnings("null")
    @Override
    public void runModule(@Nonnull MZmineProject project,
            @Nonnull ParameterSet parameters,
            @Nonnull Collection<Task<?>> tasks) {

        final List<File> fileNames = parameters
                .getParameter(RawDataImportParameters.fileNames).getValue();
        final String removePrefix = parameters
                .getParameter(RawDataImportParameters.removePrefix).getValue();
        final String removeSuffix = parameters
                .getParameter(RawDataImportParameters.removeSuffix).getValue();

        if (fileNames == null) {
            logger.warning("Raw data import module started with no filenames");
            return;
        }

        for (File fileName : fileNames) {

            if ((!fileName.exists()) || (!fileName.canRead())) {
                MZmineGUI.displayMessage("Cannot read file " + fileName);
                logger.warning("Cannot read file " + fileName);
                continue;
            }

            DataPointStore dataStore;
            MSDKTask newTask = null;
            try {
                dataStore = DataPointStoreFactory.getTmpFileDataPointStore();
                RawDataFileImportMethod method = new RawDataFileImportMethod(
                        fileName, dataStore);
                newTask = new MSDKTask("Importing raw data file",
                        fileName.getName(), method);
                newTask.setOnSucceeded(e -> {
                    RawDataFile rawDataFile = method.getResult();
                    if (rawDataFile == null)
                        return;

                    // Remove common prefix
                    if (!Strings.isNullOrEmpty(removePrefix)) {
                        String name = rawDataFile.getName();
                        if (name.startsWith(removePrefix))
                            name = name.substring(removePrefix.length());
                        rawDataFile.setName(name);
                    }

                    // Remove common suffix
                    if (!Strings.isNullOrEmpty(removeSuffix)) {
                        String name = rawDataFile.getName();
                        if (name.endsWith(removeSuffix))
                            name = name.substring(0,
                                    name.length() - removeSuffix.length());
                        rawDataFile.setName(name);
                    }

                    project.addFile(rawDataFile);
                });
                tasks.add(newTask);

            } catch (MSDKException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public @Nonnull MZmineModuleCategory getModuleCategory() {
        return MZmineModuleCategory.RAWDATA;
    }

    @SuppressWarnings("null")
    public @Nonnull ParameterSet getParameters() {
        return parameters;
    }
}
