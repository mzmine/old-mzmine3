/*
 * Copyright 2006-2015 The MZmine 3 Development Team
 * 
 * This file is part of MZmine 3.
 * 
 * MZmine 3 is free software; you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * MZmine 3 is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with MZmine 3; if not,
 * write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301
 * USA
 */

package io.github.mzmine.util;

import java.text.NumberFormat;

import io.github.msdk.datamodel.IsolationInfo;
import io.github.msdk.datamodel.MsScan;
import io.github.msdk.datamodel.RawDataFile;
import io.github.mzmine.main.MZmineCore;

/**
 * MS scan utilities
 */
public class MsScanUtils {

  /**
   * Generates a multi-line textual description of a given MsScan
   */
  public static String createFullMsScanDescription(MsScan scan) {

    RawDataFile rdf = scan.getRawDataFile();
    Integer scanNum = scan.getScanNumber();
    MsFunction msFunc = scan.getMsFunction();
    Integer msLevel = msFunc.getMsLevel();
    ChromatographyInfo chromInfo = scan.getChromatographyInfo();

    StringBuilder sb = new StringBuilder();

    if (rdf != null)
      sb.append("Raw data file: " + rdf.getName() + "\n");

    sb.append("Scan number: " + scanNum + "\n");

    if (chromInfo != null) {
      NumberFormat rtFormat = MZmineCore.getConfiguration().getRTFormat();
      Float rt = chromInfo.getRetentionTime();
      sb.append("Retention time: ");
      sb.append(rtFormat.format(rt / 60f));
      sb.append(" min\n");
      Float secondRT = chromInfo.getSecondaryRetentionTime();
      if (secondRT != null) {
        sb.append("Secondary retention time: ");
        sb.append(rtFormat.format(secondRT / 60f));
        sb.append(" min\n");
      }
    }

    sb.append("MS function: ");
    sb.append(msFunc.getName());
    sb.append("\n");
    if (msLevel != null) {
      sb.append("MS level: ");
      sb.append(msLevel);
      sb.append("\n");
    }
    sb.append("Scan definition: ");
    sb.append(scan.getScanDefinition());
    sb.append("\n");
    sb.append("Polarity: ");
    sb.append(scan.getPolarity());
    sb.append("\n");

    return sb.toString();
  }

  /**
   * Generates a single-line textual description of a given MS/MS scan.
   */
  public static String createSingleLineMsScanDescription(MsScan scan) {
    return createSingleLineMsScanDescription(scan, null);
  }

  /**
   * Generates a single-line textual description of a given MS/MS scan, taking the precursor m/z
   * from a given isolation (a single scan can have multiple isolations)
   */
  public static String createSingleLineMsScanDescription(MsScan scan, IsolationInfo isolation) {

    RawDataFile rdf = scan.getRawDataFile();

    StringBuilder sb = new StringBuilder();

    if (rdf != null) {
      sb.append(rdf.getName());
      sb.append(" ");
    }

    sb.append("#");
    sb.append(scan.getScanNumber());
    ChromatographyInfo scanRt = scan.getChromatographyInfo();
    if (scanRt != null) {
      NumberFormat rtFormat = MZmineCore.getConfiguration().getRTFormat();
      sb.append(" @");
      sb.append(rtFormat.format(scanRt.getRetentionTime() / 60f));
      sb.append(" min");
    }
    if (isolation != null) {
      Double precursorMz = isolation.getPrecursorMz();
      if (precursorMz != null) {
        NumberFormat mzFormat = MZmineCore.getConfiguration().getMZFormat();
        sb.append(", precursor ");
        sb.append(mzFormat.format(precursorMz));
        sb.append(" m/z");
      }
    }
    return sb.toString();
  }

}
