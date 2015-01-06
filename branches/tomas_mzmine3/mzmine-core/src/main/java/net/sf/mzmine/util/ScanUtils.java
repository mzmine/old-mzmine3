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

package net.sf.mzmine.util;

import java.text.Format;
import java.util.ArrayList;
import java.util.Arrays;

import javax.annotation.Nonnull;

import net.sf.mzmine.datamodel.DataPoint;
import net.sf.mzmine.datamodel.MassSpectrumType;
import net.sf.mzmine.datamodel.MsScan;
import net.sf.mzmine.main.MZmineCore;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;

import com.google.common.collect.Range;

/**
 * Scan related utilities
 */
@TestClass("net.sf.mzmine.util.ScanUtilsTest")
public class ScanUtils {

    /**
     * Common utility method to be used as Scan.toString() method in various
     * Scan implementations
     * 
     * @param scan
     *            Scan to be converted to String
     * @return String representation of the scan
     */
    public static @Nonnull String scanToString(@Nonnull MsScan scan) {
	StringBuffer buf = new StringBuffer();
	Format rtFormat = MZmineCore.getConfiguration().getRTFormat();
	Format mzFormat = MZmineCore.getConfiguration().getMZFormat();
	buf.append("#");
	buf.append(scan.getScanNumber());
	buf.append(" @");
	//buf.append(rtFormat.format(scan.getRetentionTime()));
	buf.append(" MS");
	buf.append(scan.getMSLevel());
	//if (scan.getMSLevel() > 1)
	//    buf.append(" (" + mzFormat.format(scan.getPrecursorMZ()) + ")");
	switch (scan.getSpectrumType()) {
	case CENTROIDED:
	    buf.append(" c");
	    break;
	case PROFILE:
	    buf.append(" p");
	    break;
	case THRESHOLDED:
	    buf.append(" t");
	    break;
	}

	return buf.toString();
    }

    /**
     * Find a base peak of a given scan in a given m/z range
     * 
     * @param scan
     *            Scan to search
     * @param mzMin
     *            m/z range minimum
     * @param mzMax
     *            m/z range maximum
     * @return double[2] containing base peak m/z and intensity
     */
    public static @Nonnull DataPoint findBasePeak(@Nonnull MsScan scan,
	    @Nonnull Range<Double> mzRange) {

	DataPoint dataPoints[] = scan.getDataPointsByMass(mzRange);
	DataPoint basePeak = null;

	for (DataPoint dp : dataPoints) {
	    if ((basePeak == null)
		    || (dp.getIntensity() > basePeak.getIntensity()))
		basePeak = dp;
	}

	return basePeak;
    }

    /**
     * Calculate the total ion count of a scan within a given mass range.
     * 
     * @param scan
     *            the scan.
     * @param mzRange
     *            mass range.
     * @return the total ion count of the scan within the mass range.
     */
    public static double calculateTIC(MsScan scan, Range<Double> mzRange) {

	double tic = 0.0;
	for (final DataPoint dataPoint : scan.getDataPointsByMass(mzRange)) {
	    tic += dataPoint.getIntensity();
	}
	return tic;
    }

    /**
     * Selects data points within given m/z range
     * 
     */
    public static DataPoint[] selectDataPointsByMass(DataPoint dataPoints[],
	    Range<Double> mzRange) {
	ArrayList<DataPoint> goodPoints = new ArrayList<DataPoint>();
	for (DataPoint dp : dataPoints) {
	    if (mzRange.contains(dp.getMz()))
		goodPoints.add(dp);
	}
	return goodPoints.toArray(new DataPoint[0]);
    }

    /**
     * Selects data points with intensity >= given intensity
     * 
     */
    public static DataPoint[] selectDataPointsOverIntensity(
	    DataPoint dataPoints[], double minIntensity) {
	ArrayList<DataPoint> goodPoints = new ArrayList<DataPoint>();
	for (DataPoint dp : dataPoints) {
	    if (dp.getIntensity() >= minIntensity)
		goodPoints.add(dp);
	}
	return goodPoints.toArray(new DataPoint[0]);
    }

    /**
     * Binning modes
     */
    public static enum BinningType {
	SUM, MAX, MIN, AVG
    }

    /**
     * This method bins values on x-axis. Each bin is assigned biggest y-value
     * of all values in the same bin.
     * 
     * @param x
     *            X-coordinates of the data
     * @param y
     *            Y-coordinates of the data
     * @param firstBinStart
     *            Value at the "left"-edge of the first bin
     * @param lastBinStop
     *            Value at the "right"-edge of the last bin
     * @param numberOfBins
     *            Number of bins
     * @param interpolate
     *            If true, then empty bins will be filled with interpolation
     *            using other bins
     * @param binningType
     *            Type of binning (sum of all 'y' within a bin, max of 'y', min
     *            of 'y', avg of 'y')
     * @return Values for each bin
     */
    public static double[] binValues(double[] x, double[] y,
	    Range<Double> binRange, int numberOfBins, boolean interpolate,
	    BinningType binningType) {

	Double[] binValues = new Double[numberOfBins];
	double binWidth = (binRange.upperEndpoint() - binRange.lowerEndpoint())
		/ numberOfBins;

	double beforeX = Double.MIN_VALUE;
	double beforeY = 0.0f;
	double afterX = Double.MAX_VALUE;
	double afterY = 0.0f;

	double[] noOfEntries = null;

	// Binnings
	for (int valueIndex = 0; valueIndex < x.length; valueIndex++) {

	    // Before first bin?
	    if ((x[valueIndex] - binRange.lowerEndpoint()) < 0) {
		if (x[valueIndex] > beforeX) {
		    beforeX = x[valueIndex];
		    beforeY = y[valueIndex];
		}
		continue;
	    }

	    // After last bin?
	    if ((binRange.upperEndpoint() - x[valueIndex]) < 0) {
		if (x[valueIndex] < afterX) {
		    afterX = x[valueIndex];
		    afterY = y[valueIndex];
		}
		continue;
	    }

	    int binIndex = (int) ((x[valueIndex] - binRange.lowerEndpoint()) / binWidth);

	    // in case x[valueIndex] is exactly lastBinStop, we would overflow
	    // the array
	    if (binIndex == binValues.length)
		binIndex--;

	    switch (binningType) {
	    case MAX:
		if (binValues[binIndex] == null) {
		    binValues[binIndex] = y[valueIndex];
		} else {
		    if (binValues[binIndex] < y[valueIndex]) {
			binValues[binIndex] = y[valueIndex];
		    }
		}
		break;
	    case MIN:
		if (binValues[binIndex] == null) {
		    binValues[binIndex] = y[valueIndex];
		} else {
		    if (binValues[binIndex] > y[valueIndex]) {
			binValues[binIndex] = y[valueIndex];
		    }
		}
		break;
	    case AVG:
		if (noOfEntries == null) {
		    noOfEntries = new double[binValues.length];
		}
		if (binValues[binIndex] == null) {
		    noOfEntries[binIndex] = 1;
		    binValues[binIndex] = y[valueIndex];
		} else {
		    noOfEntries[binIndex]++;
		    binValues[binIndex] += y[valueIndex];
		}
		break;

	    case SUM:
	    default:
		if (binValues[binIndex] == null) {
		    binValues[binIndex] = y[valueIndex];
		} else {
		    binValues[binIndex] += y[valueIndex];
		}
		break;

	    }

	}

	// calculate the AVG
	if (binningType.equals(BinningType.AVG)) {
	    assert noOfEntries != null;
	    for (int binIndex = 0; binIndex < binValues.length; binIndex++) {
		if (binValues[binIndex] != null) {
		    binValues[binIndex] /= noOfEntries[binIndex];
		}
	    }
	}

	// Interpolation
	if (interpolate) {

	    for (int binIndex = 0; binIndex < binValues.length; binIndex++) {
		if (binValues[binIndex] == null) {

		    // Find exisiting left neighbour
		    double leftNeighbourValue = beforeY;
		    int leftNeighbourBinIndex = (int) Math
			    .floor((beforeX - binRange.lowerEndpoint())
				    / binWidth);
		    for (int anotherBinIndex = binIndex - 1; anotherBinIndex >= 0; anotherBinIndex--) {
			if (binValues[anotherBinIndex] != null) {
			    leftNeighbourValue = binValues[anotherBinIndex];
			    leftNeighbourBinIndex = anotherBinIndex;
			    break;
			}
		    }

		    // Find existing right neighbour
		    double rightNeighbourValue = afterY;
		    int rightNeighbourBinIndex = (binValues.length - 1)
			    + (int) Math.ceil((afterX - binRange
				    .upperEndpoint()) / binWidth);
		    for (int anotherBinIndex = binIndex + 1; anotherBinIndex < binValues.length; anotherBinIndex++) {
			if (binValues[anotherBinIndex] != null) {
			    rightNeighbourValue = binValues[anotherBinIndex];
			    rightNeighbourBinIndex = anotherBinIndex;
			    break;
			}
		    }

		    double slope = (rightNeighbourValue - leftNeighbourValue)
			    / (rightNeighbourBinIndex - leftNeighbourBinIndex);
		    binValues[binIndex] = new Double(leftNeighbourValue + slope
			    * (binIndex - leftNeighbourBinIndex));

		}

	    }

	}

	double[] res = new double[binValues.length];
	for (int binIndex = 0; binIndex < binValues.length; binIndex++) {
	    res[binIndex] = binValues[binIndex] == null ? 0
		    : binValues[binIndex];
	}
	return res;

    }

    /**
     * Returns index of m/z value in a given array, which is closest to given
     * value, limited by given m/z tolerance. We assume the m/z array is sorted.
     * 
     * @return index of best match, or -1 if no datapoint was found
     */
    public static int findClosestDatapoint(double key, double mzValues[],
	    double mzTolerance) {

	int index = Arrays.binarySearch(mzValues, key);

	if (index >= 0)
	    return index;

	// Get "insertion point"
	index = (index * -1) - 1;

	// If key value is bigger than biggest m/z value in array
	if (index == mzValues.length)
	    index--;
	else if (index > 0) {
	    // Check insertion point value and previous one, see which one
	    // is closer
	    if (Math.abs(mzValues[index - 1] - key) < Math.abs(mzValues[index]
		    - key))
		index--;
	}

	// Check m/z tolerancee
	if (Math.abs(mzValues[index] - key) <= mzTolerance)
	    return index;

	// Nothing was found
	return -1;

    }

    /**
     * Determines if the spectrum represented by given array of data points is
     * centroided or continuous (profile or thresholded). Profile spectra are
     * easy to detect, because they contain zero-intensity data points. However,
     * distinguishing centroided from thresholded spectra is not trivial. MZmine
     * uses multiple checks for that purpose, as described in the code comments.
     */
    @TestMethod("testDetectSpectrumType")
    public static MassSpectrumType detectSpectrumType(
	    @Nonnull DataPoint[] dataPoints) {

	// If the spectrum has less than 5 data points, it should be centroided.
	if (dataPoints.length < 5)
	    return MassSpectrumType.CENTROIDED;

	// Go through the data points and find the highest one
	double maxIntensity = 0.0;
	int topDataPointIndex = 0;
	for (int i = 0; i < dataPoints.length; i++) {

	    // If the spectrum contains data points of zero intensity, it should
	    // be in profile mode
	    if (dataPoints[i].getIntensity() == 0.0) {
		return MassSpectrumType.PROFILE;
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
	final double topMzDifference = Math.abs(dataPoints[topDataPointIndex]
		.getMz() - dataPoints[topDataPointIndex - 1].getMz());

	// For 5 data points around the top one (with the top one in the
	// center), we check the distribution of the m/z values. If the spectrum
	// is continuous (thresholded), the distances between data points should
	// be more or less constant. On the other hand, centroided spectra
	// usually have unevenly distributed data points.
	for (int i = topDataPointIndex - 2; i < topDataPointIndex + 2; i++) {

	    // Check if the index is within acceptable range
	    if ((i < 1) || (i > dataPoints.length - 1))
		continue;

	    final double currentMzDifference = Math.abs(dataPoints[i].getMz()
		    - dataPoints[i - 1].getMz());

	    // Check if the m/z distance of the pair of consecutive data points
	    // falls within 25% tolerance of the distance of the top data point
	    // and its neighbor. If not, the spectrum should be centroided.
	    if ((currentMzDifference < 0.8 * topMzDifference)
		    || (currentMzDifference > 1.25 * topMzDifference)) {
		return MassSpectrumType.CENTROIDED;
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
	    return MassSpectrumType.CENTROIDED;

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
	    return MassSpectrumType.CENTROIDED;

	// If we could not find any sign that the spectrum is centroided, we
	// conclude it should be thresholded.
	return MassSpectrumType.THRESHOLDED;

    }



    /**
     * Find the highest data point in array
     * 
     */
    public static @Nonnull DataPoint findTopDataPoint(
	    @Nonnull DataPoint dataPoints[]) {

	DataPoint topDP = null;

	for (DataPoint dp : dataPoints) {
	    if ((topDP == null) || (dp.getIntensity() > topDP.getIntensity())) {
		topDP = dp;
	    }
	}

	return topDP;
    }


}
