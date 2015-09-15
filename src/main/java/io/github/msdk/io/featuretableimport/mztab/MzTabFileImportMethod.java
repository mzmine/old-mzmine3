/* 
 * (C) Copyright 2015 by MSDK Development Team
 *
 * This software is dual-licensed under either
 *
 * (a) the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation
 *
 * or (per the licensee's choosing)
 *
 * (b) the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */

package io.github.msdk.io.featuretableimport.mztab;

import java.io.File;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.SortedMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteStreams;
import com.google.common.math.DoubleMath;

import io.github.msdk.MSDKException;
import io.github.msdk.MSDKMethod;
import io.github.msdk.datamodel.datapointstore.DataPointStore;
import io.github.msdk.datamodel.featuretables.ColumnName;
import io.github.msdk.datamodel.featuretables.FeatureTable;
import io.github.msdk.datamodel.featuretables.FeatureTableColumn;
import io.github.msdk.datamodel.featuretables.FeatureTableRow;
import io.github.msdk.datamodel.featuretables.Sample;
import io.github.msdk.datamodel.impl.MSDKObjectBuilder;
import io.github.msdk.datamodel.ionannotations.IonAnnotation;
import io.github.msdk.datamodel.rawdata.ChromatographyInfo;
import io.github.msdk.datamodel.rawdata.SeparationType;
import uk.ac.ebi.pride.jmztab.model.Assay;
import uk.ac.ebi.pride.jmztab.model.MZTabFile;
import uk.ac.ebi.pride.jmztab.model.Modification;
import uk.ac.ebi.pride.jmztab.model.MsRun;
import uk.ac.ebi.pride.jmztab.model.SmallMolecule;
import uk.ac.ebi.pride.jmztab.model.SplitList;
import uk.ac.ebi.pride.jmztab.utils.MZTabFileParser;

public class MzTabFileImportMethod implements MSDKMethod<FeatureTable> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private int parsedRows, totalRows = 0, samples;

    private final @Nonnull File sourceFile;
    private final @Nonnull DataPointStore dataStore;

    private FeatureTable newFeatureTable;
    private boolean canceled = false;

    public MzTabFileImportMethod(@Nonnull File sourceFile,
            @Nonnull DataPointStore dataStore) {
        this.sourceFile = sourceFile;
        this.dataStore = dataStore;
    }

    @SuppressWarnings("null")
    @Override
    public FeatureTable execute() throws MSDKException {

        logger.info("Started parsing file " + sourceFile);

        // Check if the file is readable
        if (!sourceFile.canRead()) {
            throw new MSDKException("Cannot read file " + sourceFile);
        }

        String fileName = sourceFile.getName();
        newFeatureTable = MSDKObjectBuilder.getFeatureTable(fileName,
                dataStore);

        try {
            // Prevent MZTabFileParser from writing to console
            OutputStream logStream = ByteStreams.nullOutputStream();

            // Load mzTab file
            MZTabFileParser mzTabFileParser = new MZTabFileParser(sourceFile,
                    logStream);

            MZTabFile mzTabFile = mzTabFileParser.getMZTabFile();

            // Let's say the initial parsing took 10% of the time
            totalRows = mzTabFile.getSmallMolecules().size();
            samples = mzTabFile.getMetadata().getMsRunMap().size();

            // Check if cancel is requested
            if (canceled) {
                return null;
            }

            // Add the common columns to the table
            addColumns(newFeatureTable, mzTabFile);

            // Check if cancel is requested
            if (canceled) {
                return null;
            }

            // Import small molecules (=rows)
            importSmallMolecules(newFeatureTable, mzTabFile);

            // Check if cancel is requested
            if (canceled) {
                return null;
            }

        } catch (Exception e) {
            throw new MSDKException(e);
        }

        logger.info("Finished parsing " + sourceFile + ", parsed " + samples
                + " samples and " + totalRows + " features.");

        return newFeatureTable;

    }

    @Override
    @Nullable
    public FeatureTable getResult() {
        return newFeatureTable;
    }

    @Override
    public Float getFinishedPercentage() {
        return totalRows == 0 ? null : (float) parsedRows / totalRows;
    }

    @Override
    public void cancel() {
        this.canceled = true;
    }

    @SuppressWarnings("null")
    private void addColumns(@Nonnull FeatureTable featureTable,
            @Nonnull MZTabFile mzTabFile) {
        // Common columns
        FeatureTableColumn<Integer> idColumn = MSDKObjectBuilder
                .getIdFeatureTableColumn();
        FeatureTableColumn<Double> mzColumn = MSDKObjectBuilder
                .getMzFeatureTableColumn();
        FeatureTableColumn<ChromatographyInfo> chromatographyInfoColumn = MSDKObjectBuilder
                .getChromatographyInfoFeatureTableColumn();
        FeatureTableColumn<IonAnnotation> ionAnnotationColumn = MSDKObjectBuilder
                .getIonAnnotationFeatureTableColumn();
        newFeatureTable.addColumn(idColumn);
        newFeatureTable.addColumn(mzColumn);
        newFeatureTable.addColumn(chromatographyInfoColumn);
        newFeatureTable.addColumn(ionAnnotationColumn);

        // Sample specific columns
        File file;
        String fileName;
        Sample sample;
        SortedMap<Integer, MsRun> msrun = mzTabFile.getMetadata().getMsRunMap();
        for (Entry<Integer, MsRun> entry : msrun.entrySet()) {
            file = new File(entry.getValue().getLocation().getPath());
            fileName = file.getName();
            sample = MSDKObjectBuilder.getSimpleSample(fileName);

            // Add all sample specific columns
            for (ColumnName columnName : ColumnName.values()) {
                newFeatureTable.addColumn(MSDKObjectBuilder
                        .getFeatureTableColumn(columnName, sample));
            }

            // Check if cancel is requested
            if (canceled) {
                return;
            }

        }
    }

    private void importSmallMolecules(@Nonnull FeatureTable featureTable,
            @Nonnull MZTabFile mzTabFile) {
        SortedMap<Integer, Assay> assayMap = mzTabFile.getMetadata()
                .getAssayMap();
        Collection<SmallMolecule> smallMolecules = mzTabFile
                .getSmallMolecules();

        // Loop through SML data
        String formula, smile, inchiKey, description, database,
                dbVersion, reliability, url;
        Double mzExp = null, mzCalc = null, abundance, peak_mz, peak_rt, peak_height;
        Float rtAverageValue = null;
        Integer charge;

        for (SmallMolecule smallMolecule : smallMolecules) {
            parsedRows++;

            // Check if cancel is requested
            if (canceled) {
                return;
            }

            formula = smallMolecule.getChemicalFormula();
            smile = smallMolecule.getSmiles().toString();
            inchiKey = smallMolecule.getInchiKey().toString();
            description = smallMolecule.getDescription();
            database = smallMolecule.getDatabase();
            dbVersion = smallMolecule.getDatabaseVersion();
            String identifier = smallMolecule.getIdentifier().toString();
            SplitList<Double> rt = smallMolecule.getRetentionTime();

            if (smallMolecule.getReliability() != null)
                reliability = smallMolecule.getReliability().toString();
            if (smallMolecule.getURI() != null)
                url = smallMolecule.getURI().toString();
            if (smallMolecule.getExpMassToCharge() != null)
                mzExp = smallMolecule.getExpMassToCharge();
            if (smallMolecule.getCalcMassToCharge() != null)
                mzCalc = smallMolecule.getCalcMassToCharge();
            if (smallMolecule.getCharge() != null)
                charge = smallMolecule.getCharge();

            // Calculate average RT if multiple values are available
            if (rt != null && !rt.isEmpty())
                rtAverageValue = (float) DoubleMath.mean(rt);

            // Chromatography Info
            ChromatographyInfo chromatographyInfo = MSDKObjectBuilder
                    .getChromatographyInfo1D(SeparationType.UNKNOWN,
                            rtAverageValue);

            /*
             * TODO: Add chemical structure with:
             *       Formula, Smile and INCHIkey
             */
            IonAnnotation ionAnnotation = MSDKObjectBuilder
                    .getSimpleIonAnnotation();
            ionAnnotation.setDataBaseId(database);
            ionAnnotation.setDescription(description);
            ionAnnotation.setExpectedMz(mzCalc);

            // Add common data to columns
            FeatureTableColumn column;
            FeatureTableRow currentRow = MSDKObjectBuilder
                    .getFeatureTableRow(featureTable, parsedRows);

            // Common column: m/z
            column = featureTable.getColumn(ColumnName.MZ.getName(), null);
            currentRow.setData(column, rtAverageValue);

            // Common column: Chromatography Info
            column = featureTable.getColumn("Chromatography Info", null);
            currentRow.setData(column, chromatographyInfo);

            // Common column: Ion Annotation
            column = featureTable.getColumn("Ion Annotation", null);
            currentRow.setData(column, ionAnnotation);

            // Add data to sample specific columns
            /*
             * TODO:
             */

            System.out.println("m/z: " + mzExp + ", RT: " + rtAverageValue);

        }
    }

}
