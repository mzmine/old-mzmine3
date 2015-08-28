/*
 * Copyright 2006-2015 The MZmine 3 Development Team
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
 * MZmine 2; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

package io.github.mzmine.modules.rawdataimport;

import java.util.List;

import io.github.mzmine.gui.MZmineGUI;
import io.github.mzmine.parameters.Parameter;
import io.github.mzmine.parameters.SimpleParameterSet;
import io.github.mzmine.util.ExitCode;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import java.io.File;

public class RawDataImportParameters extends SimpleParameterSet {

    private static final ExtensionFilter filters[] = new ExtensionFilter[] {
            new ExtensionFilter("All raw data files", "cdf", "nc", "mzData",
                    "mzML", "mzXML", "xml", "raw", "csv"),
            new ExtensionFilter("All XML files", "xml"),
            new ExtensionFilter("NetCDF files", "cdf", "nc"),
            new ExtensionFilter("mzData files", "mzData"),
            new ExtensionFilter("mzML files", "mzML"),
            new ExtensionFilter("XCalibur RAW files", "raw"),
            new ExtensionFilter("Waters RAW folders", "raw"),
            new ExtensionFilter("mzXML files", "mzXML"),
            new ExtensionFilter("Agilent CSV files", "csv") };

    public static final FileNamesParameter fileNames = new FileNamesParameter();

    public RawDataImportParameters() {
        super(new Parameter[] { fileNames });
    }

    public ExitCode showSetupDialog() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select files to import");
        fileChooser.getExtensionFilters().addAll(filters);

        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(null);

        if ((selectedFiles == null) || (selectedFiles.isEmpty()))
            return ExitCode.CANCEL;

        getParameter(fileNames).setValue(selectedFiles);

        return ExitCode.OK;

    }
}
