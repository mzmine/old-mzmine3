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

import java.util.List;

import io.github.msdk.datamodel.peaklists.PeakList;
import io.github.msdk.datamodel.peaklists.PeakListColumn;
import io.github.msdk.datamodel.peaklists.PeakListRow;
import io.github.msdk.datamodel.rawdata.RawDataFile;

/**
 * Simple implementation of the PeakList interface.
 */
class SimplePeakList implements PeakList {

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setName(String name) {
        // TODO Auto-generated method stub

    }

    @Override
    public List<PeakListRow> getRows() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addRow(PeakListRow row) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeRow(PeakListRow row) {
        // TODO Auto-generated method stub

    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub

    }

    @Override
    public List<PeakListColumn<?>> getColumns() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addColumn(PeakListColumn<?> col) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void removeColumn(PeakListColumn<?> col) {
        // TODO Auto-generated method stub
        
    }

}
