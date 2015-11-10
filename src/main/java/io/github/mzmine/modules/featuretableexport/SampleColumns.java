/*
 * Copyright 2006-2015 The MZmine 3 Development Team
 * 
 * This file is part of MZmine 3.
 * 
 * MZmine 3 is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * MZmine 3 is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * MZmine 3; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

package io.github.mzmine.modules.featuretableexport;

import java.util.ArrayList;
import java.util.List;

import io.github.msdk.datamodel.featuretables.ColumnName;
import io.github.msdk.datamodel.featuretables.FeatureTable;
import io.github.mzmine.main.MZmineCore;

public class SampleColumns {

    public static List<String> getSampleColumns() {

        List<String> columnNames = new ArrayList<String>();

        // If there are features tables in the current project then get the
        // sample columns from them
        List<FeatureTable> featuretables = MZmineCore.getCurrentProject()
                .getFeatureTables();
        /*
         * TODO: Loop through all featuretables and add unique sample columns
         */

        // Otherwise get the sample columns from the predefined names
        if (featuretables.size() == 0) {
            for (ColumnName columName : ColumnName.values()) {
                if (!columName.getName().equals(ColumnName.ID.getName())
                        && !columName.getName()
                                .equals(ColumnName.GROUPID.getName())
                        && !columName.getName()
                                .equals(ColumnName.CHROMATOGRAM.getName())) {
                    columnNames.add(columName.getName());
                }
            }
        }

        return columnNames;

    }
}
