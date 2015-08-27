/*
 * Copyright 2006-2015 The MZmine 2 Development Team
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

import java.io.File;
import java.io.FileReader;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nonnull;

import io.github.mzmine.datamodel.MZmineProject;
import io.github.mzmine.modules.MZmineModuleCategory;
import io.github.mzmine.modules.MZmineProcessingModule;
import io.github.mzmine.modules.rawdataimport.fileformats.XMLReadTask;
import io.github.mzmine.parameters.ParameterSet;
import io.github.mzmine.taskcontrol.Task;
import io.github.mzmine.util.ExitCode;

/**
 * Raw data import module
 */
public class RawDataImportModule implements MZmineProcessingModule {

    private Logger logger = Logger.getLogger(this.getClass().getName());

    private static final String MODULE_NAME = "Raw data import";
    private static final String MODULE_DESCRIPTION = "This module imports raw data into the project.";

    private static final char[] thermoHeader = new char[] { 0x01, 0xA1, 'F', 0,
	    'i', 0, 'n', 0, 'n', '\0', 'i', '\0', 'g', '\0', 'a', '\0', 'n',
	    '\0' };

    @Override
    public @Nonnull String getName() {
	return MODULE_NAME;
    }

    @Override
    public @Nonnull String getDescription() {
	return MODULE_DESCRIPTION;
    }

    @Override
    @Nonnull
    public ExitCode runModule(@Nonnull MZmineProject project,
	    @Nonnull ParameterSet parameters, @Nonnull Collection<Task> tasks) {

	List<File> fileNames = parameters.getParameter(
		RawDataImportParameters.fileNames).getValue();

	for (File fileName : fileNames) {

	    if ((!fileName.exists()) || (!fileName.canRead())) {
		// MZmineCore.getDesktop().displayErrorMessage("Cannot read file "
		// + fileName);
		logger.warning("Cannot read file " + fileName);
		return ExitCode.ERROR;
	    }

	    RawDataFileType fileType = null;

	    try {
		FileReader reader = new FileReader(fileName);
		char buffer[] = new char[512];
		reader.read(buffer);
		reader.close();
		String fileHeader = new String(buffer);
		if (fileHeader.contains("mzXML")) {
		    fileType = RawDataFileType.MZXML;
		}
		if (fileHeader.contains("mzData")) {
		    fileType = RawDataFileType.MZXML;
		}
		if (fileHeader.contains("mzML")) {
		    fileType = RawDataFileType.MZML;
		}
	    } catch (Exception e) {
		e.printStackTrace();
	    }

	    if (fileType == null) {
		return null;
	    }

	    Task newTask = null;

	    switch (fileType) {
	    case MZDATA:
	    case MZML:
	    case MZXML:
		newTask = new XMLReadTask(project, fileName, fileType);
		break;
	    }

	    /*
	     * 
	     * if (extension.endsWith("cdf")) { newTask = new
	     * NetCDFReadTask(fileName, newMZmineFile); } if
	     * (extension.endsWith("raw")) { newTask = new
	     * XcaliburRawFileReadTask(fileName, newMZmineFile); } if
	     * (extension.endsWith("csv")) { newTask = new
	     * AgilentCsvReadTask(fileName, newMZmineFile); }
	     */

	    if (newTask == null) {
		logger.warning("Cannot determine file type of file " + fileName);
		return ExitCode.ERROR;
	    }

	    tasks.add(newTask);

	}

	return ExitCode.OK;
    }

    @Override
    public @Nonnull MZmineModuleCategory getModuleCategory() {
	return MZmineModuleCategory.RAWDATA;
    }

    @Override
    public @Nonnull Class<? extends ParameterSet> getParameterSetClass() {
	return RawDataImportParameters.class;
    }

}
