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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.github.msdk.datamodel.peaklists.PeakListColumn;
import io.github.msdk.datamodel.peaklists.Sample;

/**
 * Implementation of PeakListColumn
 */
class SimplePeakListColumn<DataType> implements PeakListColumn<DataType> {

    private @Nonnull String name;
    private @Nonnull Class<DataType> dataTypeClass;
    private @Nullable SimpleSample simpleSample;

    SimplePeakListColumn(@Nonnull String name, @Nonnull Class<DataType> dataTypeClass,
            @Nullable SimpleSample simpleSample) {
        this.name = name;
        this.dataTypeClass = dataTypeClass;
        this.simpleSample = simpleSample;
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
    public @Nonnull Class<DataType> getDataTypeClass() {
        return dataTypeClass;
    }

    @Override
    public Sample getSample() {
        return simpleSample;
    }

}
