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

package io.github.mzmine.modules.featuredetection.srmdetection;

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
import io.github.msdk.datamodel.impl.MSDKObjectBuilder;
import io.github.msdk.datamodel.rawdata.RawDataFile;
import io.github.msdk.featdet.chromatogramtofeaturetable.ChromatogramToFeatureTableMethod;
import io.github.msdk.featdet.srmdetection.SrmDetectionMethod;
import io.github.mzmine.gui.MZmineGUI;
import io.github.mzmine.taskcontrol.MZmineTask;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;

public class SrmDetectionTask extends Task<Object> implements MZmineTask {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final @Nonnull RawDataFile rawDataFile;
    private final @Nonnull DataPointStore dataStore;
    private final @Nullable String nameSuffix;
    private String title, message;
    private FeatureTable featureTable;

    private MSDKMethod<List<Chromatogram>> srmDetectionMethod;
    private MSDKMethod<FeatureTable> chromatogramToFeatureTableMethod;

    public SrmDetectionTask(String title, @Nullable String message,
            @Nonnull RawDataFile rawDataFile, @Nonnull DataPointStore dataStore,
            @Nullable String nameSuffix) {
        this.rawDataFile = rawDataFile;
        this.dataStore = dataStore;
        this.nameSuffix = nameSuffix;
        this.title = title;
        this.message = message;

        // SRM detection method
        srmDetectionMethod = new SrmDetectionMethod(rawDataFile, dataStore);

        refreshStatus();

        EventHandler<WorkerStateEvent> cancelEvent = new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerEvent) {
                srmDetectionMethod.cancel();
            }
        };

        setOnCancelled(cancelEvent);
    }

    @Override
    public void refreshStatus() {

        // Progress
        Float finishedPercent = 0f;

        final Float method1Percent = srmDetectionMethod.getFinishedPercentage();
        if (method1Percent != null)
            finishedPercent = method1Percent * 0.95f;

        if (chromatogramToFeatureTableMethod != null) {
            final Float method2Percent = chromatogramToFeatureTableMethod
                    .getFinishedPercentage();
            if (method2Percent != null)
                finishedPercent = finishedPercent + method2Percent * 0.05f;
        }

        updateProgress(finishedPercent.doubleValue(), 1.0);

        // Title and message
        updateTitle(title);
        updateMessage(message);
    }

    @Override
    protected Object call() throws Exception {
        try {
            List<Chromatogram> detectedChromatograms = (List<Chromatogram>) srmDetectionMethod
                    .execute();

            // Create a new feature table
            featureTable = MSDKObjectBuilder.getFeatureTable(
                    rawDataFile.getName() + nameSuffix, dataStore);

            // Create a new sample
            Sample sample = MSDKObjectBuilder
                    .getSimpleSample(rawDataFile.getName());

            // Add the chromatograms to the feature table
            this.chromatogramToFeatureTableMethod = new ChromatogramToFeatureTableMethod(
                    detectedChromatograms, featureTable, sample);

            // Run method
            try {
                chromatogramToFeatureTableMethod.execute();
            } catch (Throwable e) {
                final String msg = "Error executing task " + title + ": "
                        + e.getMessage();
                logger.error(msg, e);
                MZmineGUI.displayMessage(msg);
            }
        } catch (Throwable e) {
            final String msg = "Error executing task " + title + ": "
                    + e.getMessage();
            logger.error(msg, e);
            MZmineGUI.displayMessage(msg);
        }

        return featureTable;
    }

    public FeatureTable getResult() {
        return featureTable;
    }

}
