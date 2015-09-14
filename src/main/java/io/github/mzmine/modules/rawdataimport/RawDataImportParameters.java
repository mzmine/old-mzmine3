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

package io.github.mzmine.modules.rawdataimport;

import io.github.mzmine.parameters.ParameterSet;
import io.github.mzmine.parameters.parametertypes.StringParameter;
import javafx.stage.FileChooser.ExtensionFilter;

public class RawDataImportParameters extends ParameterSet {

    private static final ExtensionFilter filters[] = new ExtensionFilter[] {
            new ExtensionFilter("All raw data files", "*.csv", "*.cdf", "*.nc",
                    "*.mzData", "*.mzML", "*.mzXML", "*.raw", "*.xml"),
            new ExtensionFilter("Agilent CSV files", "*.csv"),
            new ExtensionFilter("mzData files", "*.mzData"),
            new ExtensionFilter("mzML files", "*.mzML"),
            new ExtensionFilter("mzXML files", "*.mzXML"),
            new ExtensionFilter("NetCDF files", "*.cdf", "*.nc"),
            new ExtensionFilter("Waters RAW folders", "*.raw"),
            new ExtensionFilter("XCalibur RAW files", "*.raw"),
            new ExtensionFilter("XML files", "*.xml") };

    public static final StringParameter param1 = new StringParameter("asda",
            "asdasdasa");
    
    public static final StringParameter param2 = new StringParameter("asda2afsa",
            "asdasdasa asdasd");
    
    public static final StringParameter param3 = new StringParameter("asda3a sdas",
            "asdasdasa asd as");

    public RawDataImportParameters() {
        parameters.addAll(param1, param2, param3);
    }

}
