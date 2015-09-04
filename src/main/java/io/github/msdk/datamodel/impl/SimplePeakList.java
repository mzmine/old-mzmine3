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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableList;
import com.sun.istack.Nullable;

import io.github.msdk.datamodel.datapointstore.DataPointStore;
import io.github.msdk.datamodel.peaklists.PeakList;
import io.github.msdk.datamodel.peaklists.PeakListColumn;
import io.github.msdk.datamodel.peaklists.PeakListRow;
import io.github.msdk.datamodel.peaklists.Sample;

/**
 * Implementation of the PeakList interface.
 */
class SimplePeakList implements PeakList {

    private @Nonnull String name;
    private @Nonnull DataPointStore dataPointStore;
    private @Nullable ArrayList<PeakListRow> peakListRows;
    private @Nullable ArrayList<PeakListColumn<?>> peakListColumns;

    SimplePeakList(@Nonnull String name, @Nonnull DataPointStore dataPointStore) {
        this.name = name;
        this.dataPointStore = dataPointStore;
        peakListRows = new ArrayList<PeakListRow>();
        peakListColumns = new ArrayList<PeakListColumn<?>>();
    }

    @Override
    public @Nonnull String getName() {
        return name;
    }

    @Override
    public void setName(@Nonnull String name) {
        this.name = name;
    }

    @SuppressWarnings("null")
    @Override
    public @Nonnull List<PeakListRow> getRows() {
        List<PeakListRow> peakListRowCopy = ImmutableList.copyOf(peakListRows);
        return peakListRowCopy;
    }

    @Override
    public void addRow(@Nonnull PeakListRow row) {
        synchronized (peakListRows) {
            peakListRows.add(row);
        }
    }

    @Override
    public void removeRow(@Nonnull PeakListRow row) {
        synchronized (peakListRows) {
            peakListRows.remove(row);
        }
    }

    @SuppressWarnings("null")
    @Override
    public @Nonnull List<PeakListColumn<?>> getColumns() {
        List<PeakListColumn<?>> peakListColumnsCopy = ImmutableList
                .copyOf(peakListColumns);
        return peakListColumnsCopy;
    }

    @Override
    public void addColumn(@Nonnull PeakListColumn<?> col) {
        synchronized (peakListColumns) {
            peakListColumns.add(col);
        }
    }

    @Override
    public void removeColumn(@Nonnull PeakListColumn<?> col) {
        synchronized (peakListColumns) {
            peakListColumns.remove(col);
        }
    }

    @SuppressWarnings("null")
    @Override
    public @Nonnull List<Sample> getSamples() {
        ArrayList<Sample> sampleList = new ArrayList<Sample>();
        synchronized (peakListColumns) {
            for (PeakListColumn<?> col : peakListColumns) {
                Sample s = col.getSample();
                if (s != null)
                    sampleList.add(s);
            }
        }
        return ImmutableList.copyOf(sampleList);
    }

    @Override
    public void dispose() {
        dataPointStore.dispose();
    }

}
