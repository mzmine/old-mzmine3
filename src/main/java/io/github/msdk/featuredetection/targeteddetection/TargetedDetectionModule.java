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

package io.github.msdk.featuredetection.targeteddetection;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.github.msdk.MSDKException;
import io.github.msdk.MSDKMethod;
import io.github.msdk.datamodel.chromatograms.Chromatogram;
import io.github.msdk.datamodel.chromatograms.ChromatogramType;
import io.github.msdk.datamodel.datapointstore.DataPointStore;
import io.github.msdk.datamodel.impl.MSDKObjectBuilder;
import io.github.msdk.datamodel.ionannotations.IonAnnotation;
import io.github.msdk.datamodel.rawdata.MsScan;
import io.github.msdk.datamodel.rawdata.RawDataFile;
import io.github.msdk.datamodel.rawdata.SeparationType;
import io.github.msdk.datamodel.util.MZTolerance;
import io.github.msdk.datamodel.util.RawDataFileUtil;

/**
 * This class creates a list of Chromatograms for a RawDataFile based the
 * inputted list of IonAnnotations.
 */
public class TargetedDetectionModule implements MSDKMethod<List<Chromatogram>> {

    private final @Nonnull List<IonAnnotation> ionAnnotations;
    private final @Nonnull RawDataFile rawDataFile;
    private final @Nonnull DataPointStore dataPointStore;
    private final @Nonnull MZTolerance mzTolerance;

    private List<Chromatogram> result;
    private boolean canceled = false;
    private int processedScans = 0, totalScans = 0;

    /**
     * @param ionAnnotations,
     *            rawDataFile
     */
    public TargetedDetectionModule(@Nonnull List<IonAnnotation> ionAnnotations,
            @Nonnull RawDataFile rawDataFile,
            @Nonnull DataPointStore dataPointStore, @Nonnull MZTolerance mzTolerance) {
        this.ionAnnotations = ionAnnotations;
        this.rawDataFile = rawDataFile;
        this.dataPointStore = dataPointStore;
        this.mzTolerance = mzTolerance;
    }

    /**
     * @throws MSDKException
     */
    @Override
    public List<Chromatogram> execute() throws MSDKException {

        List<Chromatogram> chromatogramList = new ArrayList<Chromatogram>();
        int chromatogramNumber = RawDataFileUtil
                .getNextChromatogramNumber(rawDataFile);

        // Loop through all the ions in the ion annotation list and add a new
        // chromatogram
        for (IonAnnotation ionAnnotation : ionAnnotations) {
            Chromatogram chromatogram = MSDKObjectBuilder.getChromatogram(
                    dataPointStore, chromatogramNumber, ChromatogramType.XIC,
                    SeparationType.UNKNOWN);
            chromatogramList.add(chromatogram);
            chromatogramNumber++;
        }

        // Loop through all MS scans in the raw data file
        List<MsScan> msScans = rawDataFile.getScans();
        totalScans = msScans.size();
        for (MsScan msScan : msScans) {

            // Loop through all the ions in the ion annotation list
            for (IonAnnotation ionAnnotation : ionAnnotations) {
                Double ionMz = ionAnnotation.getExpectedMz();
                Float ionRt = ionAnnotation.getChromatographyInfo().getRetentionTime();
                Float scanRt = msScan.getChromatographyInfo().getRetentionTime();

                // msScan.getDataPointsByMzAndIntensity(dataPointList, mzRange,
                // Range.all());
            }

            processedScans++;

            if (canceled)
                return null;

        }

        return chromatogramList;
    }

    @Override
    @Nullable
    public Float getFinishedPercentage() {
        return totalScans == 0 ? null : (float) processedScans / totalScans;
    }

    @Override
    @Nullable
    public List<Chromatogram> getResult() {
        return result;
    }

    @Override
    public void cancel() {
        canceled = true;
    }

}
