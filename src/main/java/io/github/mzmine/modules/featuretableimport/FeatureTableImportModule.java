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

package io.github.mzmine.modules.featuretableimport;

import java.io.File;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

import io.github.msdk.MSDKMethod;
import io.github.msdk.datamodel.datapointstore.DataPointStore;
import io.github.msdk.datamodel.datapointstore.DataPointStoreFactory;
import io.github.msdk.datamodel.featuretables.FeatureTable;
import io.github.msdk.io.csv.CsvFileImportMethod;
import io.github.msdk.io.mztab.MzTabFileImportMethod;
import io.github.mzmine.gui.MZmineGUI;
import io.github.mzmine.modules.MZmineProcessingModule;
import io.github.mzmine.modules.rawdata.rawdataimport.RawDataImportParameters;
import io.github.mzmine.parameters.ParameterSet;
import io.github.mzmine.project.MZmineProject;
import io.github.mzmine.taskcontrol.MSDKTask;
import javafx.concurrent.Task;

/**
 * Feature table import module
 */
public class FeatureTableImportModule implements MZmineProcessingModule {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Nonnull
    private static final String MODULE_NAME = "Feature table import";
    @Nonnull
    private static final String MODULE_DESCRIPTION = "This module imports feature tables into the project.";

    @Override
    public @Nonnull String getName() {
        return MODULE_NAME;
    }

    @Override
    public @Nonnull String getDescription() {
        return MODULE_DESCRIPTION;
    }

    @Override
    public void runModule(@Nonnull MZmineProject project,
            @Nonnull ParameterSet parameters,
            @Nonnull Collection<Task<?>> tasks) {

        final List<File> fileNames = parameters
                .getParameter(FeatureTableImportParameters.fileNames)
                .getValue();
        final String removePrefix = parameters
                .getParameter(RawDataImportParameters.removePrefix).getValue();
        final String removeSuffix = parameters
                .getParameter(RawDataImportParameters.removeSuffix).getValue();

        if (fileNames == null) {
            logger.warn(
                    "Feature table import module started with no filenames");
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

            // Find file extension and initiate corresponding import method
            String fileExtension = FilenameUtils
                    .getExtension(fileName.getAbsolutePath()).toUpperCase();
            MSDKMethod<?> method = null;
            switch (fileExtension) {
            case "CSV":
                method = new CsvFileImportMethod(fileName, dataStore);
                break;
            case "MZTAB":
                method = new MzTabFileImportMethod(fileName, dataStore);
                break;
            }
            final MSDKMethod<?> finalMethod = method;

            MSDKTask newTask = new MSDKTask("Importing feature table file",
                    fileName.getName(), finalMethod);
            newTask.setOnSucceeded(e -> {
                FeatureTable featureTable = (FeatureTable) finalMethod
                        .getResult();
                if (featureTable == null)
                    return;

                // Remove common prefix
                if (!Strings.isNullOrEmpty(removePrefix)) {
                    String name = featureTable.getName();
                    if (name.startsWith(removePrefix))
                        name = name.substring(removePrefix.length());
                    featureTable.setName(name);
                }

                // Remove common suffix
                if (!Strings.isNullOrEmpty(removeSuffix)) {
                    String name = featureTable.getName();
                    if (name.endsWith(removeSuffix))
                        name = name.substring(0,
                                name.length() - removeSuffix.length());
                    featureTable.setName(name);
                }

                project.addFeatureTable(featureTable);
            });

            tasks.add(newTask);

        }

    }

    @Override
    public @Nonnull Class<? extends ParameterSet> getParameterSetClass() {
        return FeatureTableImportParameters.class;
    }
}
