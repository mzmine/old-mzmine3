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

package io.github.mzmine.modules.identification.ms.localdatabasesearch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

import javax.annotation.Nonnull;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.msdk.datamodel.featuretables.FeatureTable;
import io.github.msdk.datamodel.impl.MSDKObjectBuilder;
import io.github.msdk.datamodel.ionannotations.IonAnnotation;
import io.github.msdk.datamodel.ionannotations.IonType;
import io.github.msdk.datamodel.rawdata.SeparationType;
import io.github.msdk.identification.localdatabasesearch.LocalDatabaseSearchMethod;
import io.github.msdk.util.IonTypeUtil;
import io.github.msdk.util.MZTolerance;
import io.github.msdk.util.RTTolerance;
import io.github.mzmine.modules.MZmineProcessingModule;
import io.github.mzmine.parameters.ParameterSet;
import io.github.mzmine.parameters.parametertypes.selectors.FeatureTablesSelection;
import io.github.mzmine.project.MZmineProject;
import io.github.mzmine.taskcontrol.MSDKTask;
import javafx.concurrent.Task;

/**
 * Targeted detection module
 */
public class LocalDatabaseSearchModule implements MZmineProcessingModule {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String MODULE_NAME = "Local database search";
    private static final String MODULE_DESCRIPTION = "This module searches a local database (CSV or TXT file) using m/z and retention time values.";

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
                .getParameter(LocalDatabaseSearchParameters.featureTables)
                .getValue();

        final String annotations = parameters
                .getParameter(LocalDatabaseSearchParameters.annotations)
                .getValue();

        final String separator = parameters
                .getParameter(LocalDatabaseSearchParameters.separator)
                .getValue();

        final RTTolerance rtTolerance = parameters
                .getParameter(LocalDatabaseSearchParameters.rtTolerance)
                .getValue();

        final MZTolerance mzTolerance = parameters
                .getParameter(LocalDatabaseSearchParameters.mzTolerance)
                .getValue();

        if (featureTables.getMatchingFeatureTables().isEmpty()) {
            logger.warn(
                    "Local database search module started with no feature tables selected");
            return;
        }

        // Convert the input annotations into ionAnnotations
        List<IonAnnotation> ionAnnotations = new ArrayList<IonAnnotation>();
        Scanner scanner = new Scanner(annotations);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line != null) {

                // Only process lines which start with an integer
                char firstChar = line.charAt(0);
                if (Character.digit(firstChar, 10) >= 0) {
                    String[] lineArray = line.split(separator);

                    String annotationId = lineArray[0];
                    Double mz = Double.parseDouble(lineArray[1]);
                    Float rt = Float.parseFloat(lineArray[2]) * 60;
                    String name = lineArray[3];

                    // If formula column is present then add chemical structure
                    IAtomContainer chemicalStructure = null;
                    if (lineArray.length > 4) {
                        String formula = lineArray[4];
                        // chemicalStructure
                        /*
                         * TODO
                         */
                    }

                    // If adduct column is present then add ion type
                    IonType ionType = null;

                    if (lineArray.length > 5) {
                        // Expected string format: [M+2H]2+
                        ionType = IonTypeUtil.createIonType(lineArray[5]);
                    }

                    IonAnnotation ion = MSDKObjectBuilder
                            .getSimpleIonAnnotation();
                    ion.setAnnotationId(annotationId);
                    ion.setExpectedMz(mz);
                    ion.setDescription(name);
                    ion.setChromatographyInfo(
                            MSDKObjectBuilder.getChromatographyInfo1D(
                                    SeparationType.LC, (float) rt));
                    if (ionType != null)
                        ion.setIonType(ionType);
                    if (chemicalStructure != null)
                        ion.setChemicalStructure(chemicalStructure);
                    ionAnnotations.add(ion);

                }

            }
        }
        scanner.close();

        // Run LocalDatabaseSearchMethod
        MSDKTask newTask = null;
        for (FeatureTable featureTable : featureTables
                .getMatchingFeatureTables()) {
            LocalDatabaseSearchMethod method = new LocalDatabaseSearchMethod(
                    featureTable, ionAnnotations, mzTolerance, rtTolerance);

            newTask = new MSDKTask("Importing feature table file",
                    featureTable.getName(), method);

            tasks.add(newTask);
        }

    }

    @Override
    public @Nonnull Class<? extends ParameterSet> getParameterSetClass() {
        return LocalDatabaseSearchParameters.class;
    }

}
