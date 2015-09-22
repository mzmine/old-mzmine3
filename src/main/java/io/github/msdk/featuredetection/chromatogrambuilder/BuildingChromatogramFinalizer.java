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

package io.github.msdk.featuredetection.chromatogrambuilder;

import javax.annotation.Nonnull;

import io.github.msdk.datamodel.featuretables.ColumnName;
import io.github.msdk.datamodel.featuretables.FeatureTable;
import io.github.msdk.datamodel.featuretables.FeatureTableColumn;
import io.github.msdk.datamodel.featuretables.FeatureTableRow;
import io.github.msdk.datamodel.featuretables.Sample;
import io.github.msdk.datamodel.impl.MSDKObjectBuilder;
import io.github.msdk.datamodel.rawdata.ChromatographyInfo;

class BuildingChromatogramFinalizer {

    private FeatureTableColumn<Double> mzColumn;
    private FeatureTableColumn<ChromatographyInfo> rtColumn;
    private FeatureTableColumn<Double> heightColumn;
    private FeatureTableColumn<Double> areaColumn;

    void convertChromatogramToTableRow(BuildingChromatogram chromatogram,
            FeatureTableRow row) {

        
        
    }

    void addColumns(@Nonnull FeatureTable newFeatureTable,
            @Nonnull Sample sample) {

        mzColumn = MSDKObjectBuilder.getFeatureTableColumn(ColumnName.MZ,
                sample);
        rtColumn = MSDKObjectBuilder.getFeatureTableColumn(ColumnName.RT,
                sample);
        heightColumn = MSDKObjectBuilder
                .getFeatureTableColumn(ColumnName.HEIGHT, sample);
        areaColumn = MSDKObjectBuilder.getFeatureTableColumn(ColumnName.AREA,
                sample);

        newFeatureTable.addColumn(mzColumn);
        newFeatureTable.addColumn(rtColumn);
        newFeatureTable.addColumn(heightColumn);
        newFeatureTable.addColumn(areaColumn);

    }

}
