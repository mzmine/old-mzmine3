package net.sf.mzmine.modules.rawdataimport;

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
 * MZmine 2; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

import java.io.File;
import java.util.List;

import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import net.sf.mzmine.main.MZmineCore;
import net.sf.mzmine.main.SimpleParameterSet;
import net.sf.mzmine.parameters.Parameter;
import net.sf.mzmine.util.ExitCode;

public class RawDataImportParameters extends SimpleParameterSet {

	private static final ExtensionFilter filters[] = new ExtensionFilter[] {
			new ExtensionFilter("All raw data files", "cdf", "nc", "mzData",
					"mzML", "mzXML", "xml", "raw", "csv"),
			new ExtensionFilter("All XML files", "xml"),
			new ExtensionFilter("NetCDF files", "cdf", "nc"),
			new ExtensionFilter("mzData files", "mzData"),
			new ExtensionFilter("mzML files", "mzML"),
			new ExtensionFilter("XCalibur RAW files", "raw"),
			new ExtensionFilter("mzXML files", "mzXML"),
			new ExtensionFilter("Agilent CSV files", "csv") };

	public static final FileNamesParameter fileNames = new FileNamesParameter();

	public RawDataImportParameters() {
		super(new Parameter[] { fileNames });
	}

	public ExitCode showSetupDialog() {

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Resource File");
		fileChooser.getExtensionFilters().addAll(filters);

		List<File> selectedFiles = fileChooser
				.showOpenMultipleDialog(MZmineCore.getMainWindow());

		getParameter(fileNames).setValue(selectedFiles);

		return ExitCode.OK;

	}
}
