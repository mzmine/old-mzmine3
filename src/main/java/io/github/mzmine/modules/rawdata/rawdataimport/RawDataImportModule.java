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

package io.github.mzmine.modules.rawdata.rawdataimport;

import java.io.File;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

import io.github.msdk.datamodel.datastore.DataPointStore;
import io.github.msdk.datamodel.datastore.DataPointStoreFactory;
import io.github.msdk.datamodel.rawdata.RawDataFile;
import io.github.msdk.io.RawDataFileImportMethod;
import io.github.mzmine.gui.MZmineGUI;
import io.github.mzmine.modules.MZmineProcessingModule;
import io.github.mzmine.parameters.ParameterSet;
import io.github.mzmine.project.MZmineProject;
import io.github.mzmine.taskcontrol.MSDKTask;
import javafx.concurrent.Task;

/**
 * Raw data import module
 */
public class RawDataImportModule implements MZmineProcessingModule {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String MODULE_NAME = "Raw data import";
    private static final String MODULE_DESCRIPTION = "This module imports raw data into the project.";

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
            logger.warn("Raw data import module started with no filenames");
            return;
        }
        for (File fileName : fileNames) {

            if ((!fileName.exists()) || (!fileName.canRead())) {
                MZmineGUI.displayMessage("Cannot read file " + fileName);
                logger.warn("Cannot read file " + fileName);
                continue;
            }

            DataPointStore dataStore = DataPointStoreFactory
                    .getTmpFileDataStore();

            RawDataFileImportMethod method = new RawDataFileImportMethod(
                    fileName, dataStore);
            MSDKTask newTask = new MSDKTask("Importing raw data file",
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
                    String fileExtension = FilenameUtils
                            .getExtension(fileName.getAbsolutePath());
                    String suffix = removeSuffix;
                    if (suffix.equals(".*"))
                        suffix = "." + fileExtension;
                    String name = rawDataFile.getName();
                    if (name.endsWith(suffix))
                        name = name.substring(0,
                                name.length() - suffix.length());
                    rawDataFile.setName(name);
                }

                project.addFile(rawDataFile);
            });
            tasks.add(newTask);

        }

    }

    @Override
    public @Nonnull Class<? extends ParameterSet> getParameterSetClass() {
        return RawDataImportParameters.class;
    }
}
