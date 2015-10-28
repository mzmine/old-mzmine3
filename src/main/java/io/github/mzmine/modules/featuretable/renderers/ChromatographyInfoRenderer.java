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

package io.github.mzmine.modules.featuretable.renderers;

import java.text.NumberFormat;

import io.github.msdk.datamodel.featuretables.FeatureTableRow;
import io.github.msdk.datamodel.rawdata.ChromatographyInfo;
import io.github.mzmine.main.MZmineCore;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.util.Callback;

public class ChromatographyInfoRenderer implements
        Callback<TreeTableColumn<FeatureTableRow, Object>, TreeTableCell<FeatureTableRow, Object>> {

    @Override
    public TreeTableCell<FeatureTableRow, Object> call(
            TreeTableColumn<FeatureTableRow, Object> p) {
        return new TreeTableCell<FeatureTableRow, Object>() {
            @Override
            public void updateItem(Object object, boolean empty) {
                super.updateItem(object, empty);
                setStyle("-fx-alignment: CENTER;"
                        + "-fx-border-color: transparent -fx-table-cell-border-color -fx-table-cell-border-color transparent;");
                if (object == null) {
                    setText(null);
                } else {
                    // Format to RT1, RT2
                    String value = null;
                    ChromatographyInfo chromatographyInfo = (ChromatographyInfo) object;
                    Float rt1 = chromatographyInfo.getRetentionTime();
                    Float rt2 = chromatographyInfo.getSecondaryRetentionTime();
                    NumberFormat formatter = MZmineCore.getConfiguration()
                            .getRTFormat();
                    if (rt1 != null) {
                        value = formatter.format(rt1);
                    }
                    if (value != null && rt2 != null) {
                        value = value + ", ";
                    }
                    if (rt2 != null) {
                        value = value + formatter.format(rt2);
                    }
                    setText(value);
                }
            }
        };
    }

}
