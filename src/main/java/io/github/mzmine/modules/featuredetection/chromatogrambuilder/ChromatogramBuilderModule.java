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

package io.github.mzmine.modules.featuredetection.chromatogrambuilder;

import java.util.Collection;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.msdk.datamodel.datapointstore.DataPointStore;
import io.github.msdk.datamodel.datapointstore.DataPointStoreFactory;
import io.github.msdk.datamodel.featuretables.FeatureTable;
import io.github.msdk.datamodel.rawdata.RawDataFile;
import io.github.msdk.util.MZTolerance;
import io.github.mzmine.modules.MZmineProcessingModule;
import io.github.mzmine.parameters.ParameterSet;
import io.github.mzmine.parameters.parametertypes.selectors.RawDataFilesSelection;
import io.github.mzmine.project.MZmineProject;
import javafx.concurrent.Task;

/**
 * Targeted detection module
 */
public class ChromatogramBuilderModule implements MZmineProcessingModule {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String MODULE_NAME = "Chromatogram builder";
    private static final String MODULE_DESCRIPTION = "This module connects data points from mass spectra and builds chromatograms.";

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

        final RawDataFilesSelection rawDataFiles = parameters
                .getParameter(ChromatogramBuilderParameters.rawDataFiles)
                .getValue();

        final MZTolerance mzTolerance = parameters
                .getParameter(ChromatogramBuilderParameters.mzTolerance)
                .getValue();

        final Double minDuration = parameters
                .getParameter(ChromatogramBuilderParameters.minDuration)
                .getValue();

        final Double minHeight = parameters
                .getParameter(ChromatogramBuilderParameters.minHeight)
                .getValue();

        final String nameSuffix = parameters
                .getParameter(ChromatogramBuilderParameters.nameSuffix)
                .getValue();

        if (rawDataFiles.getMatchingRawDataFiles().isEmpty()) {
            logger.warn(
                    "Chromatogram builder module started with no raw data files selected");
            return;
        }

        for (RawDataFile rawDataFile : rawDataFiles.getMatchingRawDataFiles()) {

            // Create the data structures
            DataPointStore dataStore = DataPointStoreFactory
                    .getMemoryDataStore();

            // New chromatogram builder task which runs the following two
            // methods:
            // 1. ChromatogramBuilderMethod
            // 2. ChromatogramToFeatureTableMethod
            ChromatogramBuilderTask newTask = new ChromatogramBuilderTask(
                    "Chromatogram builder", rawDataFile.getName(), rawDataFile,
                    dataStore, mzTolerance, minDuration, minHeight, nameSuffix);

            // Add the feature table to the project
            newTask.setOnSucceeded(e -> {
                FeatureTable featureTable = newTask.getResult();
                project.addFeatureTable(featureTable);
            });

            // Add the task to the queue
            tasks.add(newTask);

        }

    }

    @Override
    public @Nonnull Class<? extends ParameterSet> getParameterSetClass() {
        return ChromatogramBuilderParameters.class;
    }

}
