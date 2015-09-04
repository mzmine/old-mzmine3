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

package io.github.msdk.datamodel.impl;

import java.io.File;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.github.msdk.datamodel.peaklists.Sample;
import io.github.msdk.datamodel.rawdata.RawDataFile;

/**
 * Implementation of Sample
 */
class SimpleSample implements Sample {

    private @Nonnull String name;
    private RawDataFile rawDataFile;
    private File originalFile;

    SimpleSample(@Nonnull String name) {
        this.name = name;
    }

    @Override
    public @Nonnull String getName() {
        return name;
    }

    @Override
    public void setName(@Nonnull String name) {
        this.name = name;
    }

    @Override
    public @Nullable RawDataFile getRawDataFile() {
        return rawDataFile;
    }

    @Override
    public void setRawDataFile(@Nullable RawDataFile rawDataFile) {
        this.rawDataFile = rawDataFile;
    }

    @Override
    public @Nullable File getOriginalFile() {
        return originalFile;
    }

    @Override
    public void setOriginalFile(@Nullable File originalFile) {
        this.originalFile = originalFile;
    }

}
