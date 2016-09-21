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

package io.github.mzmine.modules.gapfilling.peakfinder;

import java.util.Collection;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.msdk.datamodel.datastore.DataPointStore;
import io.github.msdk.datamodel.datastore.DataPointStoreFactory;
import io.github.msdk.datamodel.featuretables.FeatureTable;
import io.github.msdk.features.gapfilling.GapFillingMethod;
import io.github.msdk.util.tolerances.MaximumMzTolerance;
import io.github.msdk.util.tolerances.RTTolerance;
import io.github.mzmine.modules.MZmineProcessingModule;
import io.github.mzmine.parameters.ParameterSet;
import io.github.mzmine.parameters.parametertypes.selectors.FeatureTablesSelection;
import io.github.mzmine.project.MZmineProject;
import io.github.mzmine.taskcontrol.MSDKTask;
import javafx.concurrent.Task;

/**
 * Gap filling module
 */
public class PeakFinderModule implements MZmineProcessingModule {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String MODULE_NAME = "Peak Finder";
    private static final String MODULE_DESCRIPTION = "This module fills the missing gaps in the feature list by searching for a peak in the original raw data.";

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

        // Parameters
        final FeatureTablesSelection featureTables = parameters
                .getParameter(PeakFinderParameters.featureTables).getValue();
        final MaximumMzTolerance mzTolerance = parameters
                .getParameter(PeakFinderParameters.mzTolerance).getValue();
        final RTTolerance rtTolerance = parameters
                .getParameter(PeakFinderParameters.rtTolerance).getValue();
        final Boolean isIntensityTolSet = parameters
                .getParameter(PeakFinderParameters.intensityTolerance)
                .getValue();
        Double intensityTolerance = parameters
                .getParameter(PeakFinderParameters.intensityTolerance)
                .getEmbeddedParameter().getValue();
        final Boolean sameRT = parameters
                .getParameter(PeakFinderParameters.sameRT).getValue();
        final Boolean sameMZ = parameters
                .getParameter(PeakFinderParameters.sameMZ).getValue();
        final String nameSuffix = parameters
                .getParameter(PeakFinderParameters.nameSuffix).getValue();
        final Boolean removeOldTable = parameters
                .getParameter(PeakFinderParameters.removeOldTable).getValue();

        if (featureTables == null
                || featureTables.getMatchingFeatureTables().isEmpty()) {
            logger.warn(
                    "Peak finder module started with no feature table selected");
            return;
        }

        // Add a task for each feature table
        for (FeatureTable featureTable : featureTables
                .getMatchingFeatureTables()) {

            // Create the data structures
            DataPointStore dataStore = DataPointStoreFactory
                    .getMemoryDataStore();

            // If intensity tolerance is not active then set the
            // intensityTolerance value to a very high value to avoid any
            // filtering the peak shape
            if (isIntensityTolSet == null || !isIntensityTolSet)
                intensityTolerance = Double.MAX_VALUE;

            // New feature filter task
            GapFillingMethod method = new GapFillingMethod(featureTable,
                    dataStore, mzTolerance, rtTolerance, intensityTolerance,
                    sameRT, sameMZ, nameSuffix);

            MSDKTask newTask = new MSDKTask("Gap filling feature table",
                    featureTable.getName(), method);

            // Add the feature table to the project
            newTask.setOnSucceeded(e -> {
                FeatureTable newFeatureTable = method.getResult();
                project.addFeatureTable(newFeatureTable);

                // If selected, remove old feature table
                if (removeOldTable != null && removeOldTable) {
                    project.removeFeatureTable(featureTable);
                }
            });

            // Add the task to the queue
            tasks.add(newTask);

        }
    }

    @Override
    public @Nonnull Class<? extends ParameterSet> getParameterSetClass() {
        return PeakFinderParameters.class;
    }

}
