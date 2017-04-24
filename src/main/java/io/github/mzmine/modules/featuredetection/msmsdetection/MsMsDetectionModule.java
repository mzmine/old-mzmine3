/*
 * Copyright 2006-2016 The MZmine 3 Development Team
 * 
 * This file is part of MZmine 3.
 * 
 * MZmine 3 is free software; you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * MZmine 3 is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with MZmine 3; if not,
 * write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301
 * USA
 */

package io.github.mzmine.modules.featuredetection.msmsdetection;

import java.util.Collection;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.msdk.datamodel.datastore.DataPointStore;
import io.github.msdk.datamodel.datastore.DataPointStoreFactory;
import io.github.msdk.datamodel.featuretables.FeatureTable;
import io.github.msdk.datamodel.rawdata.RawDataFile;
import io.github.msdk.util.tolerances.MaximumMzTolerance;
import io.github.msdk.util.tolerances.RTTolerance;
import io.github.mzmine.modules.MZmineProcessingModule;
import io.github.mzmine.parameters.ParameterSet;
import io.github.mzmine.parameters.parametertypes.selectors.RawDataFilesSelection;
import io.github.mzmine.parameters.parametertypes.selectors.ScanSelection;
import io.github.mzmine.project.MZmineProject;
import javafx.concurrent.Task;

/**
 * Targeted detection module
 */
public class MsMsDetectionModule implements MZmineProcessingModule {

  private Logger logger = LoggerFactory.getLogger(this.getClass());

  private static final String MODULE_NAME = "MS/MS feature detection";
  private static final String MODULE_DESCRIPTION =
      "This module searches for features from MS/MS scans in the raw data files.";

  @Override
  public @Nonnull String getName() {
    return MODULE_NAME;
  }

  @Override
  public @Nonnull String getDescription() {
    return MODULE_DESCRIPTION;
  }

  @Override
  public void runModule(@Nonnull MZmineProject project, @Nonnull ParameterSet parameters,
      @Nonnull Collection<Task<?>> tasks) {

    final RawDataFilesSelection rawDataFiles =
        parameters.getParameter(MsMsDetectionParameters.rawDataFiles).getValue();

    final ScanSelection scanSelection =
        parameters.getParameter(MsMsDetectionParameters.scanSelection).getValue();

    final MaximumMzTolerance mzTolerance =
        parameters.getParameter(MsMsDetectionParameters.mzTolerance).getValue();

    final RTTolerance rtTolerance =
        parameters.getParameter(MsMsDetectionParameters.rtTolerance).getValue();

    final Double intensityTolerance =
        parameters.getParameter(MsMsDetectionParameters.intensityTolerance).getValue();

    final String nameSuffix =
        parameters.getParameter(MsMsDetectionParameters.nameSuffix).getValue();

    if (rawDataFiles == null || rawDataFiles.getMatchingRawDataFiles().isEmpty()) {
      logger.warn("MS/MS detection module started with no raw data files selected");
      return;
    }

    if (scanSelection == null || scanSelection.getMsLevel() == null
        || !scanSelection.getMsLevel().equals(2)) {
      logger.warn(
          "Only MS level 2 can be processed using this module. Please set it under the scan filter parameter");
      return;
    }

    for (RawDataFile rawDataFile : rawDataFiles.getMatchingRawDataFiles()) {

      // Create the data structures
      DataPointStore dataStore = DataPointStoreFactory.getMemoryDataStore();

      // New MS/MS detection task which runs the following three methods:
      // 1. MsMsDetectionMethod
      // 2. TargetedDetectionMethod
      // 3. ChromatogramToFeatureTableMethod
      MsMsDetectionTask newTask =
          new MsMsDetectionTask("MS/MS feature detection", rawDataFile.getName(), rawDataFile,
              scanSelection, dataStore, mzTolerance, rtTolerance, intensityTolerance, nameSuffix);

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
    return MsMsDetectionParameters.class;
  }

}
