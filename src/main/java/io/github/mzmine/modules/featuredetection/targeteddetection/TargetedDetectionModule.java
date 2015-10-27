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

package io.github.mzmine.modules.featuredetection.targeteddetection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.msdk.datamodel.chromatograms.Chromatogram;
import io.github.msdk.datamodel.datapointstore.DataPointStore;
import io.github.msdk.datamodel.datapointstore.DataPointStoreFactory;
import io.github.msdk.datamodel.featuretables.FeatureTable;
import io.github.msdk.datamodel.featuretables.Sample;
import io.github.msdk.datamodel.impl.MSDKObjectBuilder;
import io.github.msdk.datamodel.ionannotations.IonAnnotation;
import io.github.msdk.datamodel.rawdata.RawDataFile;
import io.github.msdk.datamodel.rawdata.SeparationType;
import io.github.msdk.featuredetection.targeteddetection.TargetedDetectionMethod;
import io.github.msdk.featuredetection.chromatogramtofeaturetable.ChromatogramToFeatureTableMethod;
import io.github.msdk.util.MZTolerance;
import io.github.msdk.util.RTTolerance;
import io.github.mzmine.modules.MZmineProcessingModule;
import io.github.mzmine.parameters.ParameterSet;
import io.github.mzmine.parameters.parametertypes.selectors.RawDataFilesSelection;
import io.github.mzmine.project.MZmineProject;
import io.github.mzmine.taskcontrol.MSDKTask;
import javafx.concurrent.Task;

/**
 * Targeted detection module
 */
public class TargetedDetectionModule implements MZmineProcessingModule {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String MODULE_NAME = "Targeted detection";
    private static final String MODULE_DESCRIPTION = "This module searches for specific features in the raw data files.";

    @SuppressWarnings("null")
    @Override
    public @Nonnull String getName() {
        return MODULE_NAME;
    }

    @SuppressWarnings("null")
    @Override
    public @Nonnull String getDescription() {
        return MODULE_DESCRIPTION;
    }

    @Override
    public void runModule(@Nonnull MZmineProject project,
            @Nonnull ParameterSet parameters,
            @Nonnull Collection<Task<?>> tasks) {

        final RawDataFilesSelection rawDataFiles = parameters
                .getParameter(TargetedDetectionParameters.rawDataFiles)
                .getValue();

        final String features = parameters
                .getParameter(TargetedDetectionParameters.features).getValue();

        final String separator = parameters
                .getParameter(TargetedDetectionParameters.separator).getValue();

        final Double intensityTolerance = parameters
                .getParameter(TargetedDetectionParameters.intensityTolerance)
                .getValue();

        // Variables
        final MZTolerance mzTolerance = new MZTolerance(0.003, 5.0);
        final RTTolerance rtTolerance = new RTTolerance(0.2, false);
        final Double noiseLevel = 5000d;

        if (rawDataFiles.getMatchingRawDataFiles().isEmpty()) {
            logger.warn(
                    "Targeted detection module started with no raw data files selected");
            return;
        }

        // Convert the input features into ionAnnotations
        List<IonAnnotation> ionAnnotations = new ArrayList<IonAnnotation>();
        Scanner scanner = new Scanner(features);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line != null) {

                // Only process lines which start with an integer
                char firstChar = line.charAt(0);
                if (Character.digit(firstChar, 10) >= 0) {
                    String[] lineArray = line.split(separator);

                    if (lineArray.length != 3) {
                        logger.warn(
                                "Input feature list is not in the right format. Expected input is: m/z, retention time, name");
                        return;
                    }

                    Double mz = Double.parseDouble(lineArray[0]);
                    Float rt = Float.parseFloat(lineArray[1]) * 60;
                    String name = lineArray[2];

                    IonAnnotation ion = MSDKObjectBuilder
                            .getSimpleIonAnnotation();
                    ion.setExpectedMz(mz);
                    ion.setAnnotationId(name);
                    ion.setChromatographyInfo(
                            MSDKObjectBuilder.getChromatographyInfo1D(
                                    SeparationType.LC, (float) rt));
                    ionAnnotations.add(ion);
                }
            }
        }
        scanner.close();

        for (RawDataFile rawDataFile : rawDataFiles.getMatchingRawDataFiles()) {
            // Create the data structures
            DataPointStore dataStore = DataPointStoreFactory
                    .getMemoryDataStore();
            MSDKTask newTask;

            // Create the detection method
            TargetedDetectionMethod method = new TargetedDetectionMethod(
                    ionAnnotations, rawDataFile, dataStore, mzTolerance,
                    rtTolerance, intensityTolerance, noiseLevel);

            newTask = new MSDKTask("Targeted feature detection",
                    rawDataFile.getName(), method);
            newTask.setOnSucceeded(e -> {
                List<Chromatogram> detectedChromatograms = method.getResult();

                // Create a new feature table
                FeatureTable featureTable = MSDKObjectBuilder
                        .getFeatureTable(rawDataFile.getName(), dataStore);

                // Create a new sample
                Sample sample = MSDKObjectBuilder
                        .getSimpleSample(rawDataFile.getName());

                // Add the chromatograms to the feature table
                ChromatogramToFeatureTableMethod method = new ChromatogramToFeatureTableMethod(
                        detectedChromatograms, featureTable, sample);

                // Add the feature table to the project
                project.addFeatureTable(featureTable);
            });

            tasks.add(newTask);

        }

    }

    @Override
    public @Nonnull Class<? extends ParameterSet> getParameterSetClass() {
        return TargetedDetectionParameters.class;
    }

}
