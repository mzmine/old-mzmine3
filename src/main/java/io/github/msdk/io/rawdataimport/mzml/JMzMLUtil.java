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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.collect.Range;

import io.github.msdk.datamodel.chromatograms.ChromatogramDataPointList;
import io.github.msdk.datamodel.chromatograms.ChromatogramType;
import io.github.msdk.datamodel.impl.MSDKObjectBuilder;
import io.github.msdk.datamodel.msspectra.MsSpectrumDataPointList;
import io.github.msdk.datamodel.rawdata.ActivationInfo;
import io.github.msdk.datamodel.rawdata.ActivationType;
import io.github.msdk.datamodel.rawdata.ChromatographyInfo;
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
                if (accession.equals(JMzMLCV.cvScanStartTime)) {
                    try {
                        float retentionTime;
                        if ((unitAccession == null)
                                || (unitAccession.equals(JMzMLCV.cvUnitsMin1))
                                || unitAccession.equals(JMzMLCV.cvUnitsMin2)) {
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
            MsSpectrumDataPointList spectrumDataPoints) {

        spectrumDataPoints.clear();

        BinaryDataArrayList dataList = spectrum.getBinaryDataArrayList();

        if ((dataList == null) || (dataList.getCount().equals(0)))
            return;

        spectrumDataPoints.allocate(dataList.getCount());

        BinaryDataArray mzArray = dataList.getBinaryDataArray().get(0);
        BinaryDataArray intensityArray = dataList.getBinaryDataArray().get(1);
        Number mzValues[] = mzArray.getBinaryDataAsNumberArray();
        Number intensityValues[] = intensityArray.getBinaryDataAsNumberArray();
        for (int i = 0; i < mzValues.length; i++) {
            final double mz = mzValues[i].doubleValue();
            final float intensity = intensityValues[i].floatValue();
            spectrumDataPoints.add(mz, intensity);
        }

    }

    public static void extractDataPoints(
            uk.ac.ebi.jmzml.model.mzml.Chromatogram jmzChromatogram,
            ChromatogramDataPointList dataPointList) {
        ChromatographyInfo chromatographyInfo;
        dataPointList.clear();

        BinaryDataArrayList dataList = jmzChromatogram.getBinaryDataArrayList();

        if ((dataList == null) || (dataList.getCount().equals(0)))
            return;

        dataPointList.allocate(dataList.getCount());

        BinaryDataArray rtArray = dataList.getBinaryDataArray().get(0);
        BinaryDataArray intensityArray = dataList.getBinaryDataArray().get(1);
        Number rtValues[] = rtArray.getBinaryDataAsNumberArray();
        Number intensityValues[] = intensityArray.getBinaryDataAsNumberArray();
        for (int i = 0; i < rtValues.length; i++) {
            final float rt = rtValues[i].floatValue();
            final float intensity = intensityValues[i].floatValue();
            chromatographyInfo = MSDKObjectBuilder
                    .getChromatographyInfo1D(SeparationType.UNKNOWN, rt);
            dataPointList.add(chromatographyInfo, intensity);
        }
    }

    static void extractDataPoints(Spectrum spectrum,
            ChromatogramDataPointList chromatogramDataPoints) {
        ChromatographyInfo chromatographyInfo;

        chromatogramDataPoints.clear();

        BinaryDataArrayList dataList = spectrum.getBinaryDataArrayList();

        if ((dataList == null) || (dataList.getCount().equals(0)))
            return;

        chromatogramDataPoints.allocate(dataList.getCount());

        BinaryDataArray rtArray = dataList.getBinaryDataArray().get(0);
        BinaryDataArray intensityArray = dataList.getBinaryDataArray().get(1);
        Number rtValues[] = rtArray.getBinaryDataAsNumberArray();
        Number intensityValues[] = intensityArray.getBinaryDataAsNumberArray();
        for (int i = 0; i < rtValues.length; i++) {
            final float rt = rtValues[i].floatValue();
            final float intensity = intensityValues[i].floatValue();
            chromatographyInfo = MSDKObjectBuilder
                    .getChromatographyInfo1D(SeparationType.UNKNOWN, rt);
            chromatogramDataPoints.add(chromatographyInfo, intensity);
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
                if (accession.equals(JMzMLCV.cvPolarityPositive))
                    return PolarityType.POSITIVE;
                if (accession.equals(JMzMLCV.cvPolarityNegative))
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
                        if (accession.equals(JMzMLCV.cvPolarityPositive))
                            return PolarityType.POSITIVE;
                        if (accession.equals(JMzMLCV.cvPolarityNegative))
                            return PolarityType.NEGATIVE;
                    }

                }
            }
        }
        return PolarityType.UNKNOWN;

    }

    static ActivationInfo extractSourceFragmentation(Spectrum spectrum) {
        return null;
    }

    static List<IsolationInfo> extractIsolations(Spectrum spectrum) {
        return Collections.emptyList();
    }

    static SeparationType extractSeparationType(Spectrum spectrum) {
        return SeparationType.UNKNOWN;
    }

    static ChromatogramType extractChromatogramType(Spectrum spectrum) {
        return ChromatogramType.UNKNOWN;
    }

    public static SeparationType extractSeparationType(
            uk.ac.ebi.jmzml.model.mzml.Chromatogram chromatogram) {
        return SeparationType.UNKNOWN;
    }

    public static ChromatogramType extractChromatogramType(
            uk.ac.ebi.jmzml.model.mzml.Chromatogram chromatogram) {
        List<CVParam> cvParams = chromatogram.getCvParam();
        cvParams = chromatogram.getCvParam();

        if (cvParams != null) {
            for (CVParam param : cvParams) {
                String accession = param.getAccession();

                if (accession == null)
                    continue;
                if (accession.equals(JMzMLCV.cvChromatogramTIC))
                    return ChromatogramType.TIC;
                if (accession.equals(JMzMLCV.cvChromatogramMRM_SRM))
                    return ChromatogramType.MRM_SRM;
                if (accession.equals(JMzMLCV.cvChromatogramSIC))
                    return ChromatogramType.SIC;
                if (accession.equals(JMzMLCV.cvChromatogramBPC))
                    return ChromatogramType.BPC;
            }
        }

        return ChromatogramType.UNKNOWN;
    }

    
    @SuppressWarnings("null")
    public static List<IsolationInfo> extractIsolations(
            uk.ac.ebi.jmzml.model.mzml.Chromatogram chromatogram) {
        if (extractChromatogramType(chromatogram) == ChromatogramType.MRM_SRM) {
            List<CVParam> cvParams;
            Double precursorIsolationMz = null, productIsolationMz = null,
                    precursorActivationEnergy = null;
            ActivationType precursorActivation = ActivationType.UNKNOWN;
            ActivationInfo activationInfo = null;

            // Precursor isolation window
            cvParams = chromatogram.getPrecursor().getIsolationWindow()
                    .getCvParam();
            if (cvParams != null) {
                for (CVParam param : cvParams) {
                    if (param.getAccession().equals(JMzMLCV.cvIsolationWindow)) {
                        precursorIsolationMz = Double
                                .parseDouble(param.getValue());
                        break;
                    }
                }
            }

            // Precursor activation
            cvParams = chromatogram.getPrecursor().getActivation().getCvParam();
            if (cvParams != null) {
                for (CVParam param : cvParams) {
                    if (param.getAccession().equals(JMzMLCV.cvActivationCID))
                        precursorActivation = ActivationType.CID;
                    if (param.getAccession().equals(JMzMLCV.cvActivationEnergy))
                        precursorActivationEnergy = Double
                                .parseDouble(param.getValue());
                }
            }

            // Product isolation window
            cvParams = chromatogram.getProduct().getIsolationWindow()
                    .getCvParam();
            if (cvParams != null) {
                for (CVParam param : cvParams) {
                    if (param.getAccession().equals(JMzMLCV.cvIsolationWindow)) {
                        productIsolationMz = Double
                                .parseDouble(param.getValue());
                        break;
                    }
                }
            }

            if (precursorActivationEnergy != null) {
                activationInfo = MSDKObjectBuilder.getActivationInfo(precursorActivationEnergy, precursorActivation);
            }

            List<IsolationInfo> isolations = new ArrayList<>();
            IsolationInfo isolationInfo = null;

            if (precursorIsolationMz != null) {
                isolationInfo = MSDKObjectBuilder.getIsolationInfo(Range.singleton(precursorIsolationMz), null, precursorIsolationMz, null, activationInfo);
                isolations.add(isolationInfo);
            }

            if (productIsolationMz != null) {
                isolationInfo = MSDKObjectBuilder.getIsolationInfo(Range.singleton(productIsolationMz), null, productIsolationMz, null, null);
                isolations.add(isolationInfo);
            }

            return isolations;
        }

        return Collections.emptyList();

    }

}
