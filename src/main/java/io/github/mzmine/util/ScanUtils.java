/*
 * Copyright 2006-2015 The MZmine 3 Development Team
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

package io.github.mzmine.util;

import javax.annotation.Nonnull;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;

import io.github.mzmine.datamodel.DataPoint;
import io.github.mzmine.datamodel.MassSpectrumType;

/**
 * Scan related utilities
 */
@TestClass("net.sf.mzmine.util.ScanUtilsTest")
public class ScanUtils {

    /**
     * Determines if the spectrum represented by given array of data points is
     * centroided or continuous (profile or thresholded). Profile spectra are
     * easy to detect, because they contain zero-intensity data points. However,
     * distinguishing centroided from thresholded spectra is not trivial. MZmine
     * uses multiple checks for that purpose, as described in the code comments.
     */
    @TestMethod("testDetectSpectrumType")
    public static MsSpectrumType detectSpectrumType(
            @Nonnull DataPoint[] dataPoints) {

        // If the spectrum has less than 5 data points, it should be centroided.
        if (dataPoints.length < 5)
            return MsSpectrumType.CENTROIDED;

        // Go through the data points and find the highest one
        double maxIntensity = 0.0;
        int topDataPointIndex = 0;
        for (int i = 0; i < dataPoints.length; i++) {

            // If the spectrum contains data points of zero intensity, it should
            // be in profile mode
            if (dataPoints[i].getIntensity() == 0.0) {
                return MsSpectrumType.PROFILE;
            }

            // Let's ignore the first and the last data point, because
            // that would complicate our following checks
            if ((i == 0) || (i == dataPoints.length - 1))
                continue;

            // Update the maxDataPointIndex accordingly
            if (dataPoints[i].getIntensity() > maxIntensity) {
                maxIntensity = dataPoints[i].getIntensity();
                topDataPointIndex = i;
            }
        }

        // Now we have the index of the top data point (except the first and
        // the last). We also know the spectrum has at least 5 data points.
        assert topDataPointIndex > 0;
        assert topDataPointIndex < dataPoints.length - 1;
        assert dataPoints.length >= 5;

        // Calculate the m/z difference between the top data point and the
        // previous one
        final double topMzDifference = Math
                .abs(dataPoints[topDataPointIndex].getMz()
                        - dataPoints[topDataPointIndex - 1].getMz());

        // For 5 data points around the top one (with the top one in the
        // center), we check the distribution of the m/z values. If the spectrum
        // is continuous (thresholded), the distances between data points should
        // be more or less constant. On the other hand, centroided spectra
        // usually have unevenly distributed data points.
        for (int i = topDataPointIndex - 2; i < topDataPointIndex + 2; i++) {

            // Check if the index is within acceptable range
            if ((i < 1) || (i > dataPoints.length - 1))
                continue;

            final double currentMzDifference = Math
                    .abs(dataPoints[i].getMz() - dataPoints[i - 1].getMz());

            // Check if the m/z distance of the pair of consecutive data points
            // falls within 25% tolerance of the distance of the top data point
            // and its neighbor. If not, the spectrum should be centroided.
            if ((currentMzDifference < 0.8 * topMzDifference)
                    || (currentMzDifference > 1.25 * topMzDifference)) {
                return MsSpectrumType.CENTROIDED;
            }

        }

        // The previous check will detect most of the centroided spectra, but
        // there is a catch: some centroided spectra were produced by binning,
        // and the bins typically have regular distribution of data points, so
        // the above check would fail. Binning is normally used for
        // low-resolution spectra, so we can check the m/z difference the 3
        // consecutive data points (with the top one in the middle). If it goes
        // above 0.1, the spectrum should be centroided.
        final double mzDifferenceTopThree = Math
                .abs(dataPoints[topDataPointIndex - 1].getMz()
                        - dataPoints[topDataPointIndex + 1].getMz());
        if (mzDifferenceTopThree > 0.1)
            return MsSpectrumType.CENTROIDED;

        // Finally, we check the data points on the left and on the right of the
        // top one. If the spectrum is continous (thresholded), their intensity
        // should decrease gradually from the top data point. Let's check if
        // their intensity is above 1/3 of the top data point. If not, the
        // spectrum should be centroided.
        final double thirdMaxIntensity = maxIntensity / 3;
        final double leftDataPointIntensity = dataPoints[topDataPointIndex - 1]
                .getIntensity();
        final double rightDataPointIntensity = dataPoints[topDataPointIndex + 1]
                .getIntensity();
        if ((leftDataPointIntensity < thirdMaxIntensity)
                || (rightDataPointIntensity < thirdMaxIntensity))
            return MsSpectrumType.CENTROIDED;

        // If we could not find any sign that the spectrum is centroided, we
        // conclude it should be thresholded.
        return MsSpectrumType.THRESHOLDED;

    }

}
