/*
 * Copyright 2006-2014 The MZmine 2 Development Team
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

package net.sf.mzmine.datamodel;

import javax.annotation.Nonnull;
import javax.swing.table.TableCellRenderer;

public interface PeakListColumn<DataType> {

    /**
     * @return Short descriptive name for the peak list column
     */
    String getName();

    /**
     * Change the name of this peak list column
     */
    void setName(String name);

    /**
     * @return
     */
    @Nonnull
    Class<DataType> getDataTypeClass();

    /**
     * 
     */
    @Nonnull
    TableCellRenderer getTableCellRenderer();

}
