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

package io.github.msdk.io.rawdataimport.xmlformats;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import io.github.msdk.datamodel.impl.MSDKObjectBuilder;
import io.github.msdk.datamodel.msspectra.MsSpectrumDataPointList;
import io.github.msdk.datamodel.rawdata.ChromatographyInfo;
import io.github.msdk.datamodel.rawdata.FragmentationInfo;
import io.github.msdk.datamodel.rawdata.IsolationInfo;
import io.github.msdk.datamodel.rawdata.MsFunction;
import io.github.msdk.datamodel.rawdata.MsScanType;
import io.github.msdk.datamodel.rawdata.PolarityType;
import io.github.msdk.datamodel.rawdata.SeparationType;
import uk.ac.ebi.pride.tools.jmzreader.model.Param;
import uk.ac.ebi.pride.tools.jmzreader.model.Spectrum;
import uk.ac.ebi.pride.tools.jmzreader.model.impl.CvParam;
import uk.ac.ebi.pride.tools.jmzreader.model.impl.ParamGroup;
import uk.ac.ebi.pride.tools.jmzreader.model.impl.UserParam;

/**
 * This class provides conversions from the jmzreader data model to MSDK data
 * model
 */
class JmzReaderUtil {

    static MsFunction extractMsFunction(Spectrum spectrum) {
        Integer msLevel = spectrum.getMsLevel();
        return MSDKObjectBuilder.getMsFunction(msLevel);
    }

    static ChromatographyInfo extractChromatographyData(Spectrum spectrum) {

        ParamGroup params = spectrum.getAdditional();

        ParamGroup additional = spectrum.getAdditional();

        if (false) {
            for (CvParam cvParam : additional.getCvParams()) {
                System.out.println("CV PARAM " + cvParam.getAccession() + " - "
                        + cvParam.getName() + " = " + cvParam.getValue());
            }
            for (Param userParam : additional.getParams()) {
                System.out.println("PARAM " + userParam.getName() + " = "
                        + userParam.getValue());
            }
            for (UserParam userParam : additional.getUserParams()) {
                System.out.println("USER PARAM " + userParam.getName() + " = "
                        + userParam.getValue());
            }
        }

        if (true)
            return null;
        List<CvParam> cvParams = params.getCvParams();
        List<Param> paramss = params.getParams();

        for (CvParam param : cvParams) {
            String accession = param.getAccession();
            // String unitAccession = param.getUnitAccession();
            String value = param.getValue();
            if ((accession == null) || (value == null))
                continue;

            // Retention time (actually "Scan start time") MS:1000016
            if (accession.equals("MS:1000016")) {
                // MS:1000038 is used in mzML 1.0, while UO:0000031
                // is used in mzML 1.1.0 :-/
                double retentionTime;
                String unitAccession = "UO:0000031";
                if ((unitAccession == null)
                        || (unitAccession.equals("MS:1000038"))
                        || unitAccession.equals("UO:0000031")) {
                    retentionTime = Double.parseDouble(value);
                } else {
                    retentionTime = Double.parseDouble(value) / 60d;
                }
                final ChromatographyInfo newChromData = MSDKObjectBuilder
                        .getChromatographyInfo1D(SeparationType.UNKNOWN,
                                (float) retentionTime);
                return newChromData;

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
        return PolarityType.UNKNOWN;
    }

    static FragmentationInfo extractSourceFragmentation(Spectrum spectrum) {
        return null;
    }

    static List<IsolationInfo> extractIsolations(Spectrum spectrum) {
        return Collections.emptyList();
    }
}
