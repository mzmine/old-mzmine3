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

import java.util.Hashtable;

import javax.annotation.Nonnull;

import io.github.msdk.datamodel.peaklists.PeakList;
import io.github.msdk.datamodel.peaklists.PeakListColumn;
import io.github.msdk.datamodel.peaklists.PeakListRow;
import io.github.msdk.datamodel.rawdata.ChromatographyInfo;

/**
 * Implementation of PeakListRow
 */
class SimplePeakListRow implements PeakListRow {

    private @Nonnull int rowId;
    private @Nonnull PeakList peaklist;
    private @Nonnull Hashtable<PeakListColumn<?>, Object> rowData;

    SimplePeakListRow(@Nonnull PeakList peaklist, int rowId) {
        this.peaklist = peaklist;
        this.rowId = rowId;
        rowData = new Hashtable<>();
    }

    @Override
    public PeakList getPeakList() {
        return peaklist;
    }

    @Override
    public Integer getId() {
        return rowId;
    }

    @Override
    public Double getMz() {
        return getData(MSDKObjectBuilder.getMzPeakListColumn());
    }

    @Override
    public ChromatographyInfo getChromatographyInfo() {
        return getData(MSDKObjectBuilder.getChromatographyInfoPeakListColumn());
    }

    @Override
    public <DataType> void setData(PeakListColumn<DataType> column,
            DataType data) {
        rowData.put(column, data);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <DataType> DataType getData(PeakListColumn<DataType> column) {
        return (DataType) rowData.get(column);
    }

}
