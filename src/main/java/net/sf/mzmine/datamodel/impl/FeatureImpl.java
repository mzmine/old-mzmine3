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

package net.sf.mzmine.datamodel.impl;

import javax.annotation.Nonnull;

import net.sf.mzmine.datamodel.ChromatographyData;
import net.sf.mzmine.datamodel.DataPoint;
import net.sf.mzmine.datamodel.Feature;
import net.sf.mzmine.datamodel.FeatureShape;
import net.sf.mzmine.datamodel.FeatureType;
import net.sf.mzmine.datamodel.IsotopePattern;
import net.sf.mzmine.datamodel.PeakListRow;
import net.sf.mzmine.datamodel.RawDataFile;
import net.sf.mzmine.util.PeakUtils;

import com.google.common.collect.Range;

/**
 * This class is a simple implementation of the peak interface.
 */
public class FeatureImpl implements Feature {

    private @Nonnull FeatureType peakStatus = FeatureType.UNKNOWN;
    private RawDataFile dataFile;

    // Scan numbers
    private int scanNumbers[];

    private DataPoint dataPointsPerScan[];

    // M/Z, RT, Height and Area
    private double mz, rt, height, area;

    // Boundaries of the peak raw data points
    private Range<Double> rtRange, mzRange, intensityRange;

    // Number of representative scan
    private int representativeScan;

    // Number of most intense fragment scan
    private int fragmentScanNumber;

    // Isotope pattern. Null by default but can be set later by deisotoping
    // method.
    private IsotopePattern isotopePattern;
    private int charge = 0;

    FeatureImpl() {
    }

    /**
     * Initializes a new peak using given values
     * 
     */
    public FeatureImpl(RawDataFile dataFile, double MZ, double RT,
	    double height, double area, int[] scanNumbers,
	    DataPoint[] dataPointsPerScan, FeatureType peakStatus,
	    int representativeScan, int fragmentScanNumber,
	    Range<Double> rtRange, Range<Double> mzRange,
	    Range<Double> intensityRange) {

	if (dataPointsPerScan.length == 0) {
	    throw new IllegalArgumentException(
		    "Cannot create a SimplePeak instance with no data points");
	}

	this.dataFile = dataFile;
	this.mz = MZ;
	this.rt = RT;
	this.height = height;
	this.area = area;
	this.scanNumbers = scanNumbers;
	this.peakStatus = peakStatus;
	this.representativeScan = representativeScan;
	this.fragmentScanNumber = fragmentScanNumber;
	this.rtRange = rtRange;
	this.mzRange = mzRange;
	this.intensityRange = intensityRange;
	this.dataPointsPerScan = dataPointsPerScan;

    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return PeakUtils.peakToString(this);
    }

    /**
     * @see net.sf.mzmine.datamodel.Feature#getRawDataPointsIntensityRange()
     */
    public @Nonnull Range<Double> getRawDataPointsIntensityRange() {
	return intensityRange;
    }

    /**
     * @see net.sf.mzmine.datamodel.Feature#getRawDataPointsMZRange()
     */
    public @Nonnull Range<Double> getRawDataPointsMZRange() {
	return mzRange;
    }

    /**
     * @see net.sf.mzmine.datamodel.Feature#getRawDataPointsRTRange()
     */
    public @Nonnull Range<Double> getRawDataPointsRTRange() {
	return rtRange;
    }

    /**
     * @see net.sf.mzmine.datamodel.Feature#getRepresentativeScanNumber()
     */
    public int getRepresentativeScanNumber() {
	return representativeScan;
    }

    public int getMostIntenseFragmentScanNumber() {
	return fragmentScanNumber;
    }

    @Override
    public IsotopePattern getIsotopePattern() {
	return isotopePattern;
    }

    @Override
    public void setIsotopePattern(@Nonnull IsotopePattern isotopePattern) {
	this.isotopePattern = isotopePattern;
    }

    @Override
    public PeakListRow getParentPeakListRow() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public FeatureType getFeatureType() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public void setFeatureType(FeatureType newStatus) {
	// TODO Auto-generated method stub

    }

    @Override
    public Double getMZ() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public void setMZ(Double newMZ) {
	// TODO Auto-generated method stub

    }

    @Override
    public ChromatographyData getChromatographyData() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public void setChromatographyData(ChromatographyData chromData) {
	// TODO Auto-generated method stub

    }

    @Override
    public Double getHeight() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public void setHeight(Double newHeight) {
	// TODO Auto-generated method stub

    }

    @Override
    public Double getArea() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public void setArea(Double newArea) {
	// TODO Auto-generated method stub

    }

    @Override
    public FeatureShape getFeatureShape() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public void setFeatureShape(FeatureShape rawData) {
	// TODO Auto-generated method stub

    }

    @Override
    public Integer getCharge() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public void setCharge(Integer charge) {
	// TODO Auto-generated method stub

    }

}
