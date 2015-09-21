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

package io.github.mzmine.modules.featuretable;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import com.google.common.collect.Range;

import io.github.msdk.datamodel.chromatograms.Chromatogram;
import io.github.msdk.datamodel.featuretables.ColumnName;
import io.github.msdk.datamodel.featuretables.FeatureTableRow;
import io.github.msdk.datamodel.ionannotations.IonAnnotation;
import io.github.msdk.datamodel.rawdata.ChromatographyInfo;
import io.github.mzmine.main.MZmineCore;
import javafx.scene.Node;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.util.Callback;

public class CellFactoryCallback implements
        Callback<TreeTableColumn<FeatureTableRow, Object>, TreeTableCell<FeatureTableRow, Object>> {

    private String name;

    public CellFactoryCallback(String columnName) {
        this.name = columnName;
    }

    @Override
    public TreeTableCell<FeatureTableRow, Object> call(
            TreeTableColumn<FeatureTableRow, Object> p) {
        return new TreeTableCell<FeatureTableRow, Object>() {
            @Override
            public void updateItem(Object object, boolean empty) {
                super.updateItem(object, empty);
                if (object == null) {
                    setText(null);
                } else {

                    // Find ColumnName
                    ColumnName columnName = null;
                    for (ColumnName cn : ColumnName.values()) {
                        if (name.equals(cn.getName())) {
                            columnName = cn;
                            break;
                        }
                    }

                    // Default
                    String value = object.toString();
                    Integer integerValue;
                    Float floatValue;
                    Double doubleValue;
                    Range rangeValue;

                    // Default format to two decimals
                    NumberFormat formatter = new DecimalFormat("#0.00");

                    if (columnName == null) {
                        if (object instanceof ChromatographyInfo) {
                            // Format to RT1, RT2
                            value = null;
                            ChromatographyInfo chromatographyInfo = (ChromatographyInfo) object;
                            Float rt1 = chromatographyInfo.getRetentionTime();
                            Float rt2 = chromatographyInfo
                                    .getSecondaryRetentionTime();
                            formatter = MZmineCore.getConfiguration()
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
                        } else if (object instanceof IonAnnotation) {
                            IonAnnotation ionAnnotation = (IonAnnotation) object;
                            value = ionAnnotation.getDescription();

                            if (value == null)
                                value = ionAnnotation.getAnnotationId();
                        }
                    } else {
                        switch (columnName) {
                        case ID:
                        case CHARGE:
                        case NUMBEROFDATAPOINTS:
                            integerValue = (Integer) object;
                            value = integerValue.toString();
                            break;
                        case RT:
                            ChromatographyInfo chromatographyInfo = (ChromatographyInfo) object;
                            floatValue = chromatographyInfo.getRetentionTime();
                            formatter = MZmineCore.getConfiguration()
                                    .getRTFormat();
                            value = formatter.format(floatValue);
                            break;
                        case MZ:
                            doubleValue = (Double) object;
                            formatter = MZmineCore.getConfiguration()
                                    .getMZFormat();
                            value = formatter.format(doubleValue);
                            break;
                        case DURATION:
                            doubleValue = (Double) object;
                            value = doubleValue.toString();
                            break;
                        case AREA:
                            doubleValue = (Double) object;
                            formatter = MZmineCore.getConfiguration()
                                    .getIntensityFormat();
                            value = formatter.format(doubleValue);
                            break;
                        case HEIGHT:
                            doubleValue = (Double) object;
                            formatter = MZmineCore.getConfiguration()
                                    .getIntensityFormat();
                            value = formatter.format(doubleValue);
                            break;
                        case RTRANGE:
                            rangeValue = (Range) object;
                            formatter = MZmineCore.getConfiguration()
                                    .getMZFormat();
                            value = formatter.format(rangeValue.lowerEndpoint())
                                    + " - " + formatter
                                            .format(rangeValue.upperEndpoint());
                        case FWHM:
                        case TAILINGFACTOR:
                        case ASYMMETRYFACTOR:
                            doubleValue = (Double) object;
                            value = doubleValue.toString();
                            break;
                        case CHROMATOGRAM:
                            Chromatogram chromatogram = (Chromatogram) object;
                            value = null;
                            //setGraphic(node);
                        }
                    }
                    setText(value);
                }
            }
        };
    }

}
