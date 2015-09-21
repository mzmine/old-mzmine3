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
 * MZmine 2; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

package io.github.msdk.centroiding.binning;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.github.msdk.MSDKException;
import io.github.msdk.MSDKMethod;
import io.github.msdk.datamodel.datapointstore.DataPointStore;
import io.github.msdk.datamodel.impl.MSDKObjectBuilder;
import io.github.msdk.datamodel.msspectra.MsSpectrumDataPointList;
import io.github.msdk.datamodel.rawdata.MsScan;
import io.github.msdk.datamodel.util.MsScanUtil;

public class BinningCentroidingMethod implements MSDKMethod<MsScan> {

    private final @Nonnull MsScan inputScan;
    private final @Nonnull DataPointStore dataPointStore;
    private final @Nonnull Double binSize;

    private float methodProgress = 0f;
    private MsScan newScan;

    public BinningCentroidingMethod(@Nonnull MsScan inputScan,
            @Nonnull DataPointStore dataPointStore, @Nonnull Double binSize) {
        this.inputScan = inputScan;
        this.dataPointStore = dataPointStore;
        this.binSize = binSize;
    }

    @Override
    public MsScan execute() throws MSDKException {

        // Copy all scan properties
        this.newScan = MsScanUtil.clone(dataPointStore, inputScan, false);

        // Create data structures
        final MsSpectrumDataPointList inputDataPoints = MSDKObjectBuilder
                .getMsSpectrumDataPointList();
        final MsSpectrumDataPointList newDataPoints = MSDKObjectBuilder
                .getMsSpectrumDataPointList();

        // Load data points
        inputScan.getDataPoints(inputDataPoints);
        final double mzBuffer[] = inputDataPoints.getMzBuffer();
        final float intensityBuffer[] = inputDataPoints.getIntensityBuffer();

        // If there are no data points, just return the scan
        if (inputDataPoints.getSize() == 0) {
            newScan.setDataPoints(inputDataPoints);
            methodProgress = 1f;
            return newScan;
        }

        double currentBinMzStart = mzBuffer[0];
        float currentBinIntensity = 0f;

        // Iterate through all data points
        for (int i = 0; i < inputDataPoints.getSize(); i++) {

            if (mzBuffer[i] < (currentBinMzStart + binSize)) {
                currentBinIntensity += intensityBuffer[i];
                continue;
            } else {
                // Add the new data point
                double currentBinMzValue = currentBinMzStart + (binSize / 2);
                newDataPoints.add(currentBinMzValue, currentBinIntensity);
            }

        }

        // Store the new data points
        newScan.setDataPoints(newDataPoints);

        // Finish
        methodProgress = 1f;
        return newScan;

    }

    @Override
    @Nullable
    public Float getFinishedPercentage() {
        return methodProgress;
    }

    @Override
    @Nullable
    public MsScan getResult() {
        return newScan;
    }

    @Override
    public void cancel() {
        // This method is too fast to be canceled
    }

}
