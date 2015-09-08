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

package io.github.msdk.io.rawdataimport.mzxml_mzdata;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import com.google.common.base.Strings;

import io.github.msdk.datamodel.impl.MSDKObjectBuilder;
import io.github.msdk.datamodel.msspectra.MsSpectrumDataPointList;
import io.github.msdk.datamodel.rawdata.ChromatographyInfo;
import io.github.msdk.datamodel.rawdata.ActivationInfo;
import io.github.msdk.datamodel.rawdata.IsolationInfo;
import io.github.msdk.datamodel.rawdata.MsFunction;
import io.github.msdk.datamodel.rawdata.MsScanType;
import io.github.msdk.datamodel.rawdata.PolarityType;
import io.github.msdk.datamodel.rawdata.SeparationType;
import uk.ac.ebi.pride.tools.jmzreader.model.Spectrum;
import uk.ac.ebi.pride.tools.jmzreader.model.impl.CvParam;
import uk.ac.ebi.pride.tools.jmzreader.model.impl.ParamGroup;

/**
 * This class provides conversions from the jmzreader data model to MSDK data
 * model
 */
class JmzReaderUtil {

    private static DatatypeFactory dataTypeFactory;

    static {
        try {
            dataTypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
    }

    static MsFunction extractMsFunction(Spectrum spectrum) {
        Integer msLevel = spectrum.getMsLevel();
        return MSDKObjectBuilder.getMsFunction(msLevel);
    }

    static ChromatographyInfo extractChromatographyData(Spectrum spectrum) {

        ParamGroup params = spectrum.getAdditional();

        for (CvParam cvParam : params.getCvParams()) {
            final String accession = cvParam.getAccession();
            if (Strings.isNullOrEmpty(accession))
                continue;
            if (JmzReaderCV.cvScanRetentionTime.equals(accession)) {
                String value = cvParam.getValue();
                if (Strings.isNullOrEmpty(value))
                    continue;

                Date currentDate = new Date();
                Duration dur = dataTypeFactory.newDuration(value);
                float rt = dur.getTimeInMillis(currentDate) / 1000f;

                return MSDKObjectBuilder
                        .getChromatographyInfo1D(SeparationType.UNKNOWN, rt);
            }
        }

        return null;
    }

    static void extractDataPoints(Spectrum spectrum,
            MsSpectrumDataPointList dataPoints) {
        Map<Double, Double> jmzreaderPeakList = spectrum.getPeakList();
        dataPoints.clear();
        dataPoints.allocate(jmzreaderPeakList.size());
        for (Double mz : jmzreaderPeakList.keySet()) {
            final float intensity = jmzreaderPeakList.get(mz).floatValue();
            dataPoints.add(mz.floatValue(), intensity);
        }
    }

    static MsScanType extractScanType(Spectrum spectrum) {
        return MsScanType.UNKNOWN;
    }

    static PolarityType extractPolarity(Spectrum spectrum) {
        ParamGroup params = spectrum.getAdditional();

        for (CvParam cvParam : params.getCvParams()) {
            final String accession = cvParam.getAccession();
            if (Strings.isNullOrEmpty(accession))
                continue;
            if (JmzReaderCV.cvScanPolarity.equals(accession)) {
                String value = cvParam.getValue();
                if (Strings.isNullOrEmpty(value))
                    continue;
                if ("+".equals(value))
                    return PolarityType.POSITIVE;
                if ("-".equals(value))
                    return PolarityType.NEGATIVE;
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

}
