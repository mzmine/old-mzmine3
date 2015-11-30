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

package io.github.mzmine.parameters.parametertypes.selectors;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import com.google.common.base.Strings;
import com.google.common.collect.Range;

import io.github.msdk.datamodel.msspectra.MsSpectrumType;
import io.github.msdk.datamodel.rawdata.MsScan;
import io.github.msdk.datamodel.rawdata.PolarityType;
import io.github.msdk.datamodel.rawdata.RawDataFile;
import io.github.mzmine.util.TextUtils;

@Immutable
public class ScanSelection {

    private final Range<Integer> scanNumberRange;
    private final Range<Double> scanRTRange;
    private final PolarityType polarity;
    private final MsSpectrumType spectrumType;
    private final Integer msLevel;
    private String scanDefinition;

    public ScanSelection() {
        this(1);
    }

    public ScanSelection(int msLevel) {
        this(null, null, null, null, msLevel, null);
    }

    public ScanSelection(Range<Double> scanRTRange, int msLevel) {
        this(null, scanRTRange, null, null, msLevel, null);
    }

    public ScanSelection(Range<Integer> scanNumberRange,
            Range<Double> scanRTRange, PolarityType polarity,
            MsSpectrumType spectrumType, Integer msLevel,
            String scanDefinition) {
        this.scanNumberRange = scanNumberRange;
        this.scanRTRange = scanRTRange;
        this.polarity = polarity;
        this.spectrumType = spectrumType;
        this.msLevel = msLevel;
        this.scanDefinition = scanDefinition;
    }

    public Range<Integer> getScanNumberRange() {
        return scanNumberRange;
    }

    public Range<Double> getScanRTRange() {
        return scanRTRange;
    }

    public PolarityType getPolarity() {
        return polarity;
    }

    public MsSpectrumType getSpectrumType() {
        return spectrumType;
    }

    public Integer getMsLevel() {
        return msLevel;
    }

    public String getScanDefinition() {
        return scanDefinition;
    }

    public List<MsScan> getMatchingScans(RawDataFile dataFile) {

        final List<MsScan> matchingScans = new ArrayList<>();

        for (MsScan scan : dataFile.getScans()) {

            if ((msLevel != null)
                    && (!msLevel.equals(scan.getMsFunction().getMsLevel())))
                continue;

            if ((polarity != null) && (!polarity.equals(scan.getPolarity())))
                continue;

            if ((spectrumType != null)
                    && (!spectrumType.equals(scan.getSpectrumType())))
                continue;

            if ((scanNumberRange != null)
                    && (!scanNumberRange.contains(scan.getScanNumber())))
                continue;

            if ((scanRTRange != null) && (!scanRTRange.contains(scan
                    .getChromatographyInfo().getRetentionTime().doubleValue())))
                continue;

            if (!Strings.isNullOrEmpty(scanDefinition)) {

                final String actualScanDefition = scan.getScanDefinition();

                if (Strings.isNullOrEmpty(actualScanDefition))
                    continue;

                final String regex = TextUtils
                        .createRegexFromWildcards(scanDefinition);

                if (!actualScanDefition.matches(regex))
                    continue;

            }

            matchingScans.add(scan);
        }

        return matchingScans;

    }
}
