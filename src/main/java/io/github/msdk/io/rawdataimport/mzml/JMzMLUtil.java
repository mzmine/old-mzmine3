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

package io.github.msdk.io.rawdataimport.mzml;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.collect.Range;

import io.github.msdk.datamodel.impl.MSDKObjectBuilder;
import io.github.msdk.datamodel.msspectra.MsSpectrumDataPointList;
import io.github.msdk.datamodel.rawdata.ChromatographyInfo;
import io.github.msdk.datamodel.rawdata.FragmentationInfo;
import io.github.msdk.datamodel.rawdata.IsolationInfo;
import io.github.msdk.datamodel.rawdata.MsFunction;
import io.github.msdk.datamodel.rawdata.MsScanType;
import io.github.msdk.datamodel.rawdata.PolarityType;
import io.github.msdk.datamodel.rawdata.SeparationType;
import uk.ac.ebi.jmzml.model.mzml.BinaryDataArray;
import uk.ac.ebi.jmzml.model.mzml.BinaryDataArrayList;
import uk.ac.ebi.jmzml.model.mzml.CVParam;
import uk.ac.ebi.jmzml.model.mzml.Scan;
import uk.ac.ebi.jmzml.model.mzml.ScanList;
import uk.ac.ebi.jmzml.model.mzml.Spectrum;

/**
 * This class provides conversions from the jzml data model to MSDK data model
 */
class JMzMLUtil {

    // CV term for scan start time
    private static final String cvScanStartTime="MS:1000016";
    
    // CV term for minutes. MS:1000038 is used in mzML 1.0, while UO:000003 is used in mzML 1.1.0
    private static final String cvUnitsMin1="MS:1000038";
    private static final String cvUnitsMin2="UO:0000031";

    
    static MsFunction extractMsFunction(Spectrum spectrum) {
        Integer msLevel = 1;
        // Browse the spectrum parameters
        List<CVParam> cvParams = spectrum.getCvParam();
        if (cvParams != null) {
            for (CVParam param : cvParams) {
                String accession = param.getAccession();
                String value = param.getValue();
                if ((accession == null) || (value == null))
                    continue;

                // MS level MS:1000511
                if (accession.equals("MS:1000511")) {
                    msLevel = Integer.parseInt(value);
                }
            }
        }
        return MSDKObjectBuilder.getMsFunction(msLevel);
    }

    static ChromatographyInfo extractChromatographyData(Spectrum spectrum) {

        ScanList scanListElement = spectrum.getScanList();
        if (scanListElement == null)
            return null;
        List<Scan> scanElements = scanListElement.getScan();
        if (scanElements == null)
            return null;

        for (Scan scan : scanElements) {
            List<CVParam> cvParams = scan.getCvParam();
            if (cvParams == null)
                continue;

            for (CVParam param : cvParams) {
                String accession = param.getAccession();
                String unitAccession = param.getUnitAccession();
                String value = param.getValue();
                if ((accession == null) || (value == null))
                    continue;

                // Retention time (actually "Scan start time") MS:1000016
                if (accession.equals(cvScanStartTime)) {
                    try {
                        float retentionTime;
                        if ((unitAccession == null)
                                || (unitAccession.equals(cvUnitsMin1))
                                || unitAccession.equals(cvUnitsMin2)) {
                            // Minutes
                            retentionTime = Float.parseFloat(value) * 60f;
                        } else {
                            // Seconds
                            retentionTime = Float.parseFloat(value);
                        }
                        ChromatographyInfo chromInfo = MSDKObjectBuilder
                                .getChromatographyInfo1D(SeparationType.UNKNOWN,
                                        retentionTime);
                        return chromInfo;
                    } catch (Exception e) {
                        // Ignore incorrectly formatted numbers, just dump the
                        // exception
                        e.printStackTrace();
                    }

                }
            }
        }

        return null;
    }

    static void extractDataPoints(Spectrum spectrum,
            MsSpectrumDataPointList dataPoints) {

        dataPoints.clear();

        BinaryDataArrayList dataList = spectrum.getBinaryDataArrayList();

        if ((dataList == null) || (dataList.getCount().equals(0)))
            return;

        dataPoints.allocate(dataList.getCount());

        BinaryDataArray mzArray = dataList.getBinaryDataArray().get(0);
        BinaryDataArray intensityArray = dataList.getBinaryDataArray().get(1);
        Number mzValues[] = mzArray.getBinaryDataAsNumberArray();
        Number intensityValues[] = intensityArray.getBinaryDataAsNumberArray();
        for (int i = 0; i < mzValues.length; i++) {
            final double mz = mzValues[i].doubleValue();
            final float intensity = intensityValues[i].floatValue();
            dataPoints.add(mz, intensity);
        }

    }

    static void extractDataPointsByMzAndIntensity(Spectrum spectrum,
            MsSpectrumDataPointList dataPoints, @Nonnull Range<Double> mzRange,
            @Nonnull Range<Float> intensityRange) {

        dataPoints.clear();

        BinaryDataArrayList dataList = spectrum.getBinaryDataArrayList();

        if ((dataList == null) || (dataList.getCount().equals(0)))
            return;

        BinaryDataArray mzArray = dataList.getBinaryDataArray().get(0);
        BinaryDataArray intensityArray = dataList.getBinaryDataArray().get(1);
        Number mzValues[] = mzArray.getBinaryDataAsNumberArray();
        Number intensityValues[] = intensityArray.getBinaryDataAsNumberArray();
        for (int i = 0; i < mzValues.length; i++) {
            final double mz = mzValues[i].doubleValue();
            if (!mzRange.contains(mz))
                continue;
            final float intensity = intensityValues[i].floatValue();
            if (!intensityRange.contains(intensity))
                continue;
            dataPoints.add(mz, intensity);
        }
    }

    static MsScanType extractScanType(Spectrum spectrum) {
        return MsScanType.UNKNOWN;
    }

    static PolarityType extractPolarity(Spectrum spectrum) {
        List<CVParam> cvParams = spectrum.getCvParam();
        if (cvParams != null) {
            for (CVParam param : cvParams) {
                String accession = param.getAccession();

                if (accession == null)
                    continue;
                if (accession.equals("MS:1000130"))
                    return PolarityType.POSITIVE;
                if (accession.equals("MS:1000129"))
                    return PolarityType.NEGATIVE;
            }
        }
        ScanList scanListElement = spectrum.getScanList();
        if (scanListElement != null) {
            List<Scan> scanElements = scanListElement.getScan();
            if (scanElements != null) {
                for (Scan scan : scanElements) {
                    cvParams = scan.getCvParam();
                    if (cvParams == null)
                        continue;
                    for (CVParam param : cvParams) {
                        String accession = param.getAccession();
                        if (accession == null)
                            continue;
                        if (accession.equals("MS:1000130"))
                            return PolarityType.POSITIVE;
                        if (accession.equals("MS:1000129"))
                            return PolarityType.NEGATIVE;
                    }

                }
            }
        }
        return PolarityType.UNKNOWN;

    }

    static FragmentationInfo extractSourceFragmentation(Spectrum spectrum) {
        return null;
    }

    static List<IsolationInfo> extractIsolations(Spectrum spectrum) {
        return Collections.emptyList();
    }
}
