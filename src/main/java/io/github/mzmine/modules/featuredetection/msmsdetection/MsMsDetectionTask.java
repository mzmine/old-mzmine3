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

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.msdk.MSDKMethod;
import io.github.msdk.datamodel.chromatograms.Chromatogram;
import io.github.msdk.datamodel.datastore.DataPointStore;
import io.github.msdk.datamodel.featuretables.FeatureTable;
import io.github.msdk.datamodel.featuretables.Sample;
import io.github.msdk.datamodel.ionannotations.IonAnnotation;
import io.github.msdk.datamodel.rawdata.MsScan;
import io.github.msdk.datamodel.rawdata.RawDataFile;
import io.github.msdk.featdet.chromatogramtofeaturetable.ChromatogramToFeatureTableMethod;
import io.github.msdk.featdet.msmsdetection.MsMsDetectionMethod;
import io.github.msdk.featdet.targeteddetection.TargetedDetectionMethod;
import io.github.msdk.util.tolerances.MaximumMzTolerance;
import io.github.msdk.util.tolerances.RTTolerance;
import io.github.mzmine.datamodel.MSDKObjectBuilder;
import io.github.mzmine.gui.MZmineGUI;
import io.github.mzmine.parameters.parametertypes.selectors.ScanSelection;
import io.github.mzmine.taskcontrol.MZmineTask;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;

public class MsMsDetectionTask extends Task<Object> implements MZmineTask {

  private Logger logger = LoggerFactory.getLogger(this.getClass());

  private final @Nonnull RawDataFile rawDataFile;
  private final @Nonnull DataPointStore dataStore;
  private final @Nonnull MaximumMzTolerance mzTolerance;
  private final @Nonnull RTTolerance rtTolerance;
  private final @Nonnull Double intensityTolerance;
  private final @Nonnull Double minHeight = 0d;
  private final @Nullable String nameSuffix;
  private String title, message;
  private FeatureTable featureTable;

  private MSDKMethod<List<IonAnnotation>> msMsDetectionMethod;
  private MSDKMethod<List<Chromatogram>> targetedDetectionMethod;
  private MSDKMethod<?> chromatogramToFeatureTableMethod;

  public MsMsDetectionTask(String title, @Nullable String message, @Nonnull RawDataFile rawDataFile,
      @Nonnull ScanSelection scanSelection, @Nonnull DataPointStore dataStore,
      @Nonnull MaximumMzTolerance mzTolerance, @Nonnull RTTolerance rtTolerance,
      @Nonnull Double intensityTolerance, @Nullable String nameSuffix) {
    this.rawDataFile = rawDataFile;
    this.dataStore = dataStore;
    this.mzTolerance = mzTolerance;
    this.rtTolerance = rtTolerance;
    this.intensityTolerance = intensityTolerance;
    this.nameSuffix = nameSuffix;
    this.title = title;
    this.message = message;

    List<MsScan> msScans = scanSelection.getMatchingScans(rawDataFile);

    // New feature filter task
    msMsDetectionMethod = new MsMsDetectionMethod(rawDataFile, msScans, dataStore, mzTolerance,
        rtTolerance, intensityTolerance);

    refreshStatus();

    EventHandler<WorkerStateEvent> cancelEvent = new EventHandler<WorkerStateEvent>() {
      @Override
      public void handle(WorkerStateEvent workerEvent) {
        msMsDetectionMethod.cancel();
        targetedDetectionMethod.cancel();
        chromatogramToFeatureTableMethod.cancel();
      }
    };

    setOnCancelled(cancelEvent);
  }

  @Override
  public void refreshStatus() {

    // Progress
    Float finishedPercent = 0f;

    final Float method1Percent = msMsDetectionMethod.getFinishedPercentage();
    if (method1Percent != null)
      finishedPercent = method1Percent * 0.1f;

    if (targetedDetectionMethod != null) {
      final Float method2Percent = targetedDetectionMethod.getFinishedPercentage();
      if (method2Percent != null)
        finishedPercent = finishedPercent + method2Percent * 0.8f;
    }

    if (chromatogramToFeatureTableMethod != null) {
      final Float method3Percent = chromatogramToFeatureTableMethod.getFinishedPercentage();
      if (method3Percent != null)
        finishedPercent = finishedPercent + method3Percent * 0.1f;
    }

    updateProgress(finishedPercent.doubleValue(), 1.0);

    // Title and message
    updateTitle(title);
    updateMessage(message);
  }

  @Override
  protected Object call() throws Exception {
    try {
      List<IonAnnotation> ionAnnotations = msMsDetectionMethod.execute();

      // Run the targeted feature detection module
      this.targetedDetectionMethod = new TargetedDetectionMethod(ionAnnotations, rawDataFile,
          dataStore, mzTolerance, rtTolerance, intensityTolerance, minHeight);

      // Run method
      try {
        List<Chromatogram> detectedChromatograms = targetedDetectionMethod.execute();

        // Create a new feature table
        featureTable =
            MSDKObjectBuilder.getFeatureTable(rawDataFile.getName() + nameSuffix, dataStore);

        // Create a new sample
        Sample sample = MSDKObjectBuilder.getSample(rawDataFile.getName());

        // Add the chromatograms to the feature table
        this.chromatogramToFeatureTableMethod =
            new ChromatogramToFeatureTableMethod(detectedChromatograms, featureTable, sample);

        // Run method
        try {
          chromatogramToFeatureTableMethod.execute();
        } catch (Throwable e) {
          final String msg = "Error executing task " + title + ": " + e.getMessage();
          logger.error(msg, e);
          MZmineGUI.displayMessage(msg);
        }

      } catch (Throwable e) {
        final String msg = "Error executing task " + title + ": " + e.getMessage();
        logger.error(msg, e);
        MZmineGUI.displayMessage(msg);
      }
    } catch (Throwable e) {
      final String msg = "Error executing task " + title + ": " + e.getMessage();
      logger.error(msg, e);
      MZmineGUI.displayMessage(msg);
    }

    return featureTable;
  }

  public FeatureTable getResult() {
    return featureTable;
  }

}
