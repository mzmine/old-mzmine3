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

package io.github.mzmine.modules.filtering.featurefilter;

import java.util.Collection;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Range;

import io.github.msdk.datamodel.datapointstore.DataPointStore;
import io.github.msdk.datamodel.datapointstore.DataPointStoreFactory;
import io.github.msdk.datamodel.featuretables.FeatureTable;
import io.github.msdk.filtering.featurefilter.FeatureFilterMethod;
import io.github.mzmine.gui.MZmineGUI;
import io.github.mzmine.modules.MZmineProcessingModule;
import io.github.mzmine.parameters.ParameterSet;
import io.github.mzmine.parameters.parametertypes.selectors.FeatureTablesSelection;
import io.github.mzmine.project.MZmineProject;
import io.github.mzmine.taskcontrol.MSDKTask;
import javafx.concurrent.Task;

/**
 * Feature filter module
 */
public class FeatureFilterModule implements MZmineProcessingModule {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String MODULE_NAME = "Feature filter";
    private static final String MODULE_DESCRIPTION = "This module removes certain features based on given restrictions.";

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

        // Boolean values
        final boolean filterByDuration = parameters
                .getParameter(FeatureFilterParameters.duration).getValue();
        final boolean filterByArea = parameters
                .getParameter(FeatureFilterParameters.area).getValue();
        final boolean filterByHeight = parameters
                .getParameter(FeatureFilterParameters.height).getValue();
        final boolean filterByDataPoints = parameters
                .getParameter(FeatureFilterParameters.dataPoints).getValue();
        final boolean filterByFWHM = parameters
                .getParameter(FeatureFilterParameters.fwhm).getValue();
        final boolean filterByTailingFactor = parameters
                .getParameter(FeatureFilterParameters.tailingFactor).getValue();
        final boolean filterByAsymmetryFactor = parameters
                .getParameter(FeatureFilterParameters.asymmetryFactor)
                .getValue();

        // Embedded values
        final Range<Double> durationRange = parameters
                .getParameter(FeatureFilterParameters.duration)
                .getEmbeddedParameter().getValue();
        final Range<Double> areaRange = parameters
                .getParameter(FeatureFilterParameters.area)
                .getEmbeddedParameter().getValue();
        final Range<Double> heightRange = parameters
                .getParameter(FeatureFilterParameters.height)
                .getEmbeddedParameter().getValue();
        final Range<Integer> dataPointsRange = parameters
                .getParameter(FeatureFilterParameters.dataPoints)
                .getEmbeddedParameter().getValue();
        final Range<Double> fwhmRange = parameters
                .getParameter(FeatureFilterParameters.fwhm)
                .getEmbeddedParameter().getValue();
        final Range<Double> tailingFactorRange = parameters
                .getParameter(FeatureFilterParameters.tailingFactor)
                .getEmbeddedParameter().getValue();
        final Range<Double> asymmetryFactorRange = parameters
                .getParameter(FeatureFilterParameters.asymmetryFactor)
                .getEmbeddedParameter().getValue();

        // Other values
        final FeatureTablesSelection featureTables = parameters
                .getParameter(FeatureFilterParameters.featureTables).getValue();
        final boolean removeOldTable = parameters
                .getParameter(FeatureFilterParameters.removeOldTable)
                .getValue();
        final String nameSuffix = parameters
                .getParameter(FeatureFilterParameters.nameSuffix).getValue();

        if (featureTables.getMatchingFeatureTables().isEmpty()) {
            MZmineGUI.displayMessage(
                    "Feature filter module started with no feature table selected.");
            logger.warn(
                    "Feature filter module started with no feature table selected.");
            return;
        }

        // Check if at least one filter is selected
        if (!filterByDuration && !filterByArea && !filterByHeight
                && !filterByDataPoints && !filterByFWHM
                && !filterByTailingFactor && !filterByAsymmetryFactor) {
            MZmineGUI.displayMessage(
                    "Feature filter module started with no filter selected.");
            logger.warn(
                    "Feature filter module started with no filter selected.");
            return;
        }

        // Add a task for each feature table
        for (FeatureTable featureTable : featureTables
                .getMatchingFeatureTables()) {

            // Create the data structures
            DataPointStore dataStore = DataPointStoreFactory
                    .getMemoryDataStore();

            // New feature filter task
            FeatureFilterMethod method = new FeatureFilterMethod(featureTable,
                    dataStore, filterByDuration, filterByArea, filterByHeight,
                    filterByDataPoints, filterByFWHM, filterByTailingFactor,
                    filterByAsymmetryFactor, durationRange, areaRange,
                    heightRange, dataPointsRange, fwhmRange, tailingFactorRange,
                    asymmetryFactorRange, nameSuffix);

            MSDKTask newTask = new MSDKTask("Filtering features in tables",
                    featureTable.getName(), method);

            // Add the feature table to the project
            newTask.setOnSucceeded(e -> {
                FeatureTable newFeatureTable = method.getResult();
                project.addFeatureTable(newFeatureTable);

                // If selected, remove old feature table
                if (removeOldTable) {
                    project.removeFeatureTable(featureTable);
                }
            });

            // Add the task to the queue
            tasks.add(newTask);

        }

    }

    @Override
    public @Nonnull Class<? extends ParameterSet> getParameterSetClass() {
        return FeatureFilterParameters.class;
    }

}
