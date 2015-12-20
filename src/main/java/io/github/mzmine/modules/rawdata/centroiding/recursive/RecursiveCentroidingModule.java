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

package io.github.mzmine.modules.rawdata.centroiding.recursive;

import java.util.Collection;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Range;

import io.github.msdk.MSDKException;
import io.github.msdk.centroiding.MSDKCentroidingMethod;
import io.github.msdk.centroiding.RecursiveCentroidingAlgorithm;
import io.github.msdk.datamodel.datapointstore.DataPointStore;
import io.github.msdk.datamodel.datapointstore.DataPointStoreFactory;
import io.github.msdk.datamodel.rawdata.RawDataFile;
import io.github.mzmine.modules.MZmineProcessingModule;
import io.github.mzmine.parameters.ParameterSet;
import io.github.mzmine.parameters.parametertypes.selectors.RawDataFilesSelection;
import io.github.mzmine.project.MZmineProject;
import io.github.mzmine.taskcontrol.MSDKTask;
import javafx.concurrent.Task;

/**
 * Recursive centroiding
 */
public class RecursiveCentroidingModule implements MZmineProcessingModule {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String MODULE_NAME = "Recursive centroiding";
    private static final String MODULE_DESCRIPTION = "Recursive centroiding";

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
                .getParameter(RecursiveCentroidingParameters.dataFiles)
                .getValue();
        final Double noiseLevel = parameters
                .getParameter(RecursiveCentroidingParameters.noiseLevel)
                .getValue();
        final Range<Double> mzPeakWidth = RecursiveCentroidingParameters.mzPeakWidth
                .getValue();
        final String suffix = parameters
                .getParameter(RecursiveCentroidingParameters.suffix).getValue();

        if (rawDataFiles.getMatchingRawDataFiles().isEmpty()) {
            logger.warn(
                    "Centroiding module started with no raw data files selected");
            return;
        }

        for (RawDataFile rawDataFile : rawDataFiles.getMatchingRawDataFiles()) {

            // Create the data structures
            DataPointStore dataStore;
            try {
                dataStore = DataPointStoreFactory.getTmpFileDataPointStore();
            } catch (MSDKException e) {
                e.printStackTrace();
                return;
            }

            final String newName = rawDataFile.getName() + " " + suffix;
            RecursiveCentroidingAlgorithm algorithm = new RecursiveCentroidingAlgorithm(
                    dataStore, noiseLevel.floatValue(), mzPeakWidth);

            MSDKCentroidingMethod method = new MSDKCentroidingMethod(
                    rawDataFile, algorithm, dataStore);

            MSDKTask newTask = new MSDKTask("Recursive centroiding method",
                    rawDataFile.getName(), method);

            // Add the feature table to the project
            newTask.setOnSucceeded(e -> {
                RawDataFile newRawFile = method.getResult();
                newRawFile.setName(newName);
                project.addFile(newRawFile);
            });

            // Add the task to the queue
            tasks.add(newTask);

        }

    }

    @Override
    public @Nonnull Class<? extends ParameterSet> getParameterSetClass() {
        return RecursiveCentroidingParameters.class;
    }
}
