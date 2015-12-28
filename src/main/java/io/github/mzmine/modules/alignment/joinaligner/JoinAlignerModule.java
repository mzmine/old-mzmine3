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

package io.github.mzmine.modules.alignment.joinaligner;

import java.util.Collection;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.msdk.datamodel.datapointstore.DataPointStore;
import io.github.msdk.datamodel.datapointstore.DataPointStoreFactory;
import io.github.msdk.datamodel.featuretables.FeatureTable;
import io.github.msdk.features.joinaligner.JoinAlignerMethod;
import io.github.msdk.util.MZTolerance;
import io.github.msdk.util.RTTolerance;
import io.github.mzmine.modules.MZmineProcessingModule;
import io.github.mzmine.parameters.ParameterSet;
import io.github.mzmine.parameters.parametertypes.selectors.FeatureTablesSelection;
import io.github.mzmine.project.MZmineProject;
import io.github.mzmine.taskcontrol.MSDKTask;
import javafx.concurrent.Task;

/**
 * Join aligner module
 */
public class JoinAlignerModule implements MZmineProcessingModule {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String MODULE_NAME = "Match Aligner";
    private static final String MODULE_DESCRIPTION = "This module aligns features using a match score calculated based on the mass and retention time of each feature.";

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

        final FeatureTablesSelection featureTables = parameters
                .getParameter(JoinAlignerParameters.featureTables).getValue();

        final MZTolerance mzTolerance = parameters
                .getParameter(JoinAlignerParameters.mzTolerance).getValue();

        final RTTolerance rtTolerance = parameters
                .getParameter(JoinAlignerParameters.rtTolerance).getValue();

        final int mzWeight = parameters
                .getParameter(JoinAlignerParameters.mzWeight).getValue();

        final int rtWeight = parameters
                .getParameter(JoinAlignerParameters.rtWeight).getValue();

        final Boolean requireSameAnnotation = parameters
                .getParameter(JoinAlignerParameters.requireSameAnnotation)
                .getValue();

        final Boolean requireSameCharge = parameters
                .getParameter(JoinAlignerParameters.requireSameCharge)
                .getValue();

        final String featureTableName = parameters
                .getParameter(JoinAlignerParameters.featureTableName)
                .getValue();

        final Boolean removeOldTable = parameters
                .getParameter(JoinAlignerParameters.removeOldTable).getValue();

        if (featureTables.getMatchingFeatureTables().isEmpty()) {
            logger.warn(
                    "Match aligner module started with no feature table selected");
            return;
        }
        if (featureTables.getMatchingFeatureTables().size() == 1) {
            logger.warn(
                    "Match aligner module started with less than two feature table");
            return;
        }

        // Create the data structures
        DataPointStore dataStore = DataPointStoreFactory.getMemoryDataStore();

        // New row filter method
        JoinAlignerMethod method = new JoinAlignerMethod(
                featureTables.getMatchingFeatureTables(), dataStore,
                mzTolerance, rtTolerance, mzWeight, rtWeight, requireSameCharge,
                requireSameAnnotation, featureTableName);

        MSDKTask newTask = new MSDKTask("Aligning feature tables",
                featureTableName, method);

        // Add the feature table to the project
        newTask.setOnSucceeded(e -> {
            FeatureTable newFeatureTable = method.getResult();
            project.addFeatureTable(newFeatureTable);

            // If selected, remove old feature table
            if (removeOldTable) {
                for (FeatureTable featureTable : featureTables
                        .getMatchingFeatureTables()) {
                    project.removeFeatureTable(featureTable);
                }
            }
        });

        // Add the task to the queue
        tasks.add(newTask);
    }

    @Override
    public @Nonnull Class<? extends ParameterSet> getParameterSetClass() {
        return JoinAlignerParameters.class;
    }

}
