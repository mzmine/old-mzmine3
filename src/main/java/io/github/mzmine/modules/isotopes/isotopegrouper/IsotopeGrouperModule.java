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

package io.github.mzmine.modules.isotopes.isotopegrouper;

import java.util.Collection;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.msdk.datamodel.datastore.DataPointStore;
import io.github.msdk.datamodel.datastore.DataPointStoreFactory;
import io.github.msdk.datamodel.featuretables.FeatureTable;
import io.github.msdk.features.isotopegrouper.IsotopeGrouperMethod;
import io.github.msdk.util.MZTolerance;
import io.github.msdk.util.RTTolerance;
import io.github.mzmine.modules.MZmineProcessingModule;
import io.github.mzmine.modules.filtering.featurefilter.FeatureFilterParameters;
import io.github.mzmine.parameters.ParameterSet;
import io.github.mzmine.parameters.parametertypes.selectors.FeatureTablesSelection;
import io.github.mzmine.project.MZmineProject;
import io.github.mzmine.taskcontrol.MSDKTask;
import javafx.concurrent.Task;

/**
 * Isotope grouper module
 */
public class IsotopeGrouperModule implements MZmineProcessingModule {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String MODULE_NAME = "Isotope grouper";
    private static final String MODULE_DESCRIPTION = "This module detects isotopic peaks and groups them together into isotpe patterns.";

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
                .getParameter(FeatureFilterParameters.featureTables).getValue();

        final MZTolerance mzTolerance = parameters
                .getParameter(IsotopeGrouperParameters.mzTolerance).getValue();

        final RTTolerance rtTolerance = parameters
                .getParameter(IsotopeGrouperParameters.rtTolerance).getValue();

        final Integer maximumCharge = parameters
                .getParameter(IsotopeGrouperParameters.maximumCharge)
                .getValue();

        final RepresentativeIsotope representativeIsotope = parameters
                .getParameter(IsotopeGrouperParameters.representativeIsotope)
                .getValue();

        final Boolean monotonicShape = parameters
                .getParameter(IsotopeGrouperParameters.monotonicShape)
                .getValue();

        final String nameSuffix = parameters
                .getParameter(IsotopeGrouperParameters.nameSuffix).getValue();

        final Boolean removeOldTable = parameters
                .getParameter(IsotopeGrouperParameters.removeOldTable)
                .getValue();

        if (featureTables == null
                || featureTables.getMatchingFeatureTables().isEmpty()) {
            logger.warn(
                    "Isotopic grouper module started with no feature table selected");
            return;
        }

        // Add a task for each feature table
        for (FeatureTable featureTable : featureTables
                .getMatchingFeatureTables()) {

            // Create the data structures
            DataPointStore dataStore = DataPointStoreFactory
                    .getMemoryDataStore();

            // Feature table name
            String featureTableName = featureTable.getName();
            if (nameSuffix != null)
                featureTableName += nameSuffix;

            // New isotope grouper method
            IsotopeGrouperMethod method = new IsotopeGrouperMethod(featureTable,
                    dataStore, mzTolerance, rtTolerance, maximumCharge,
                    monotonicShape, featureTableName);

            MSDKTask newTask = new MSDKTask(
                    "Isotope grouping features in table",
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
        return IsotopeGrouperParameters.class;
    }

}
