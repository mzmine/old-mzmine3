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

import java.io.File;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Range;

import io.github.msdk.datamodel.rawdata.Chromatogram;
import io.github.msdk.datamodel.rawdata.ChromatographyInfo;
import io.github.msdk.datamodel.rawdata.MsFunction;
import io.github.msdk.datamodel.rawdata.MsScan;
import io.github.msdk.datamodel.rawdata.RawDataFile;
import io.github.msdk.datamodel.rawdata.RawDataFileType;
import uk.ac.ebi.pride.tools.jmzreader.JMzReader;

/**
 * This class reads XML-based mass spec data formats (mzData, mzXML, and mzML)
 * using the jmzreader library.
 */
class XMLBasedRawDataFile implements RawDataFile {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final @Nonnull File sourceFile;
    private final @Nonnull RawDataFileType fileType;
    private final @Nonnull JMzReader jmzreader;

    private @Nonnull String name;


    @SuppressWarnings("null")
    public XMLBasedRawDataFile(@Nonnull File sourceFile,
            @Nonnull RawDataFileType fileType,
            @Nonnull JMzReader jmzreader) {
        this.sourceFile = sourceFile;
        this.fileType = fileType;
        this.jmzreader = jmzreader;
        this.name = sourceFile.getName();
    }

    @Override
    @Nonnull
    public String getName() {
        return name;
    }

    @Override
    public void setName(@Nonnull String name) {
this.name = name;
}

    @Override
    @Nullable
    public File getOriginalFile() {
        return sourceFile;
    }


    @Override
    @Nonnull
    public RawDataFileType getRawDataFileType() {
        return fileType;
    }

    @Override
    @Nonnull
    public List<MsFunction> getMsFunctions() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @Nonnull
    public List<MsScan> getScans() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @Nonnull
    public List<MsScan> getScans(MsFunction function) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @Nonnull
    public List<Chromatogram> getChromatograms() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void dispose() {
        jmzreader.
        
    }


}
