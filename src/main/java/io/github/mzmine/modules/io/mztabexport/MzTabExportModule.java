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

package io.github.mzmine.modules.io.mztabexport;

import java.io.File;
import java.util.Collection;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.msdk.datamodel.featuretables.FeatureTable;
import io.github.msdk.io.mztab.MzTabFileExportMethod;
import io.github.mzmine.gui.MZmineGUI;
import io.github.mzmine.modules.MZmineProcessingModule;
import io.github.mzmine.parameters.ParameterSet;
import io.github.mzmine.parameters.parametertypes.selectors.FeatureTablesSelection;
import io.github.mzmine.project.MZmineProject;
import io.github.mzmine.taskcontrol.MSDKTask;
import javafx.concurrent.Task;

/**
 * Feature table import module
 */
public class MzTabExportModule implements MZmineProcessingModule {

  private Logger logger = LoggerFactory.getLogger(this.getClass());
  private String namePattern = "{}";

  @Nonnull
  private static final String MODULE_NAME = "Feature table export";
  @Nonnull
  private static final String MODULE_DESCRIPTION = "This module exports feature tables.";

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

    // Parameters
    final FeatureTablesSelection featureTables =
        parameters.getParameter(MzTabExportParameters.featureTables).getValue();
    final File exportFilePattern =
        parameters.getParameter(MzTabExportParameters.exportFile).getValue();
    final Boolean exportAllFeatures =
        parameters.getParameter(MzTabExportParameters.exportAllFeatures).getValue();

    if (featureTables == null || featureTables.getMatchingFeatureTables().isEmpty()) {
      MZmineGUI
          .displayMessage("Feature table export module started with no feature table selected.");
      logger.warn("Feature table export module started with no feature table selected.");
      return;
    }

    if (exportFilePattern == null) {
      MZmineGUI.displayMessage("The path and name of the mzTab output file cannot be empty.");
      logger.warn("The path and name of the mzTab output file cannot be empty.");
      return;
    }

    // Add a task for each feature table
    for (FeatureTable featureTable : featureTables.getMatchingFeatureTables()) {

      // Multi-CSV export: substitute pattern in name
      String newFilename = exportFilePattern.getPath().replaceAll(Pattern.quote(namePattern),
          featureTable.getName());
      File exportFile = new File(newFilename);

      // New feature filter task
      MzTabFileExportMethod method =
          new MzTabFileExportMethod(featureTable, exportFile, exportAllFeatures);

      MSDKTask newTask = new MSDKTask("Exporting feature table", featureTable.getName(), method);

      // Add the task to the queue
      tasks.add(newTask);

    }

  }

  @Override
  public @Nonnull Class<? extends ParameterSet> getParameterSetClass() {
    return MzTabExportParameters.class;
  }
}
