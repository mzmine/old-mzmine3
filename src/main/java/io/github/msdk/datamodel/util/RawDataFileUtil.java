package io.github.msdk.datamodel.util;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.collect.Range;

import io.github.msdk.datamodel.rawdata.ChromatographyInfo;
import io.github.msdk.datamodel.rawdata.MsFunction;
import io.github.msdk.datamodel.rawdata.MsScan;
import io.github.msdk.datamodel.rawdata.RawDataFile;

public class RawDataFileUtil {

    @Nonnull
    static public List<MsScan> getScans(RawDataFile rawDataFile,
            MsFunction msFunction) {
        ArrayList<MsScan> msScanList = new ArrayList<MsScan>();
        List<MsScan> scans = rawDataFile.getScans();
        synchronized (scans) {
            for (MsScan scan : scans) {
                if (scan.getMsFunction().equals(msFunction))
                    msScanList.add(scan);
            }
        }
        return msScanList;
    }

    @Nonnull
    static public List<MsScan> getScans(RawDataFile rawDataFile,
            Range<ChromatographyInfo> rtRange) {
        ArrayList<MsScan> msScanList = new ArrayList<MsScan>();
        List<MsScan> scans = rawDataFile.getScans();
        synchronized (scans) {
            for (MsScan scan : scans) {
                ChromatographyInfo scanRT = scan.getChromatographyInfo();
                if (scanRT != null) {
                    if (rtRange.contains(scanRT))
                        msScanList.add(scan);
                }
            }
        }
        return new ArrayList<MsScan>();
    }

    @Nonnull
    static public List<MsScan> getScans(RawDataFile rawDataFile,
            MsFunction msFunction, Range<ChromatographyInfo> rtRange) {
        ArrayList<MsScan> msScanList = new ArrayList<MsScan>();
        List<MsScan> scans = rawDataFile.getScans();
        synchronized (scans) {
            for (MsScan scan : scans) {
                ChromatographyInfo scanRT = scan.getChromatographyInfo();
                if (scanRT != null) {
                    if (scan.getMsFunction().equals(msFunction)
                            && rtRange.contains(scanRT))
                        msScanList.add(scan);
                }
            }
        }
        return new ArrayList<MsScan>();
    }

}
