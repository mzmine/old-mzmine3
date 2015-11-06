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

import io.github.mzmine.modules.MZmineProcessingModule;
import io.github.mzmine.parameters.ParameterSet;
import io.github.mzmine.parameters.parametertypes.selectors.FeatureTablesSelection;
import io.github.mzmine.project.MZmineProject;
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

        final FeatureTablesSelection featureTables = parameters
                .getParameter(FeatureFilterParameters.featureTables).getValue();

        final String nameSuffix = parameters
                .getParameter(FeatureFilterParameters.nameSuffix).getValue();

        if (featureTables.getMatchingFeatureTables().isEmpty()) {
            logger.warn(
                    "Feature filter module started with no feature table selected");
            return;
        }

        System.out.println("nameSuffix: " + nameSuffix);
    }

    @Override
    public @Nonnull Class<? extends ParameterSet> getParameterSetClass() {
        return FeatureFilterParameters.class;
    }

}
