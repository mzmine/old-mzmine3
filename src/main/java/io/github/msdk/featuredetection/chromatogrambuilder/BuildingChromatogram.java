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

package io.github.msdk.featuredetection.chromatogrambuilder;

import java.util.ArrayList;
import java.util.List;

import io.github.msdk.MSDKRuntimeException;
import io.github.msdk.datamodel.rawdata.ChromatographyInfo;
import io.github.msdk.datamodel.rawdata.MsScan;

class BuildingChromatogram {

    // All data points of this chromatogram
    private final List<ChromatogramDataPoint> dataPoints = new ArrayList<>();

    // A set of scan numbers of a segment which is currently being connected
    private final List<ChromatogramDataPoint> buildingSegment = new ArrayList<>();

    // Number of connected segments, which have been committed by
    // commitBuildingSegment()
    private int numOfCommittedSegments = 0;

    int getNumberOfCommittedSegments() {
        return numOfCommittedSegments;
    }

    @SuppressWarnings("null")
    public float getBuildingSegmentLength() {

        if (buildingSegment.size() < 2)
            return 0.0f;
        MsScan firstScan = buildingSegment.get(0).getScan();
        MsScan lastScan = buildingSegment.get(buildingSegment.size() - 1)
                .getScan();
        ChromatographyInfo firstChromInfo = firstScan.getChromatographyInfo();
        ChromatographyInfo lastChromInfo = lastScan.getChromatographyInfo();

        if ((firstChromInfo == null) || (lastChromInfo == null))
            throw new MSDKRuntimeException(
                    "Scans do not contain retention times");

        float firstRT = firstChromInfo.getRetentionTime();
        float lastRT = lastChromInfo.getRetentionTime();

        return (lastRT - firstRT);
    }

    void removeBuildingSegment() {
        dataPoints.removeAll(buildingSegment);
        buildingSegment.clear();
    }

    void commitBuildingSegment() {
        numOfCommittedSegments++;
        buildingSegment.clear();
    }

    void addDataPoint(ChromatogramDataPoint dataPoint) {
        dataPoints.add(dataPoint);
        buildingSegment.add(dataPoint);
    }

    ChromatogramDataPoint getLastDataPoint() {
        return dataPoints.get(dataPoints.size() - 1);
    }

    float getHeight() {
        float maxIntensity = 0f;
        for (ChromatogramDataPoint dataPoint : dataPoints) {
            maxIntensity = Math.max(maxIntensity, dataPoint.getIntensity());
        }
        return maxIntensity;
    }

    List<ChromatogramDataPoint> getDataPoints() {
        return dataPoints;
    }

}
