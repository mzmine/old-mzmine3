/*
 * Copyright 2006-2015 The MZmine 2 Development Team
 * 
 * This file is part of MZmine 2.
 * 
 * MZmine 2 is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * MZmine 2 is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * MZmine 2; if not, write to the Free Software Foundation, Inc., 51 Franklin
 * St, Fifth Floor, Boston, MA 02110-1301 USA
 */

package io.github.msdk.featuredetection.chromatogrambuilder;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.github.msdk.MSDKException;
import io.github.msdk.MSDKMethod;
import io.github.msdk.datamodel.datapointstore.DataPointStore;
import io.github.msdk.datamodel.featuretables.FeatureTable;
import io.github.msdk.datamodel.impl.MSDKObjectBuilder;
import io.github.msdk.datamodel.rawdata.ChromatographyInfo;
import io.github.msdk.datamodel.rawdata.MsScan;
import io.github.msdk.datamodel.rawdata.RawDataFile;
import io.github.msdk.datamodel.util.MZTolerance;

public class ChromatogramBuilderMethod implements MSDKMethod<FeatureTable> {

    private final @Nonnull DataPointStore dataPointStore;
    private final @Nonnull RawDataFile inputFile;
    private final @Nonnull List<MsScan> inputScans;
    private final @Nonnull Double minimumTimeSpan, minimumHeight;
    private final @Nonnull MZTolerance mzTolerance;

    private int processedScans = 0, totalScans = 0;
    private boolean canceled = false;
    private FeatureTable resultTable;

    public ChromatogramBuilderMethod(@Nonnull DataPointStore dataPointStore,
            @Nonnull RawDataFile inputFile, @Nonnull Double minimumTimeSpan,
            @Nonnull Double minimumHeight, @Nonnull MZTolerance mzTolerance) {
        this(dataPointStore, inputFile, inputFile.getScans(), minimumTimeSpan,
                minimumHeight, mzTolerance);
    }

    public ChromatogramBuilderMethod(@Nonnull DataPointStore dataPointStore,
            @Nonnull RawDataFile inputFile, @Nonnull List<MsScan> inputScans,
            @Nonnull Double minimumTimeSpan, @Nonnull Double minimumHeight,
            @Nonnull MZTolerance mzTolerance) {
        this.dataPointStore = dataPointStore;
        this.inputFile = inputFile;
        this.inputScans = inputScans;
        this.minimumTimeSpan = minimumTimeSpan;
        this.minimumHeight = minimumHeight;
        this.mzTolerance = mzTolerance;
    }

    @Override
    @Nullable
    public FeatureTable execute() throws MSDKException {

        // Check if we have any scans
        totalScans = inputScans.size();
        if (totalScans == 0) {
            throw new MSDKException(
                    "No scans provided for Chromatogram Builder");
        }

        // Check if the scans are properly ordered by RT
        ChromatographyInfo prevRT = null;
        for (MsScan s : inputScans) {
            if (s.getChromatographyInfo() == null)
                continue;
            if (prevRT == null) {
                prevRT = s.getChromatographyInfo();
                continue;
            }
            if (prevRT.compareTo(s.getChromatographyInfo()) < 0) {
                final String msg = "Retention time of scan #"
                        + s.getScanNumber()
                        + " is smaller then the retention time of the previous scan."
                        + " Please make sure you only use scans with increasing retention times.";
                throw new MSDKException(msg);
            }
            prevRT = s.getChromatographyInfo();
        }

        // Create a new feature table
        final String featureTableName = inputFile.getName() + " chromatograms";
        resultTable = MSDKObjectBuilder.getFeatureTable(featureTableName,
                dataPointStore);

        HighestDataPointConnector massConnector = new HighestDataPointConnector(
                minimumTimeSpan, minimumHeight, mzTolerance);

        for (MsScan scan : inputScans) {

            if (canceled)
                return null;

            massConnector.addScan(inputFile, scan);
            processedScans++;
        }

        massConnector.finishChromatograms(resultTable);

        return resultTable;
    }

    @Override
    @Nullable
    public Float getFinishedPercentage() {
        if (totalScans == 0)
            return null;
        else
            return (float) processedScans / totalScans;
    }

    @Override
    @Nullable
    public FeatureTable getResult() {
        return resultTable;
    }

    @Override
    public void cancel() {
        this.canceled = true;
    }

}
