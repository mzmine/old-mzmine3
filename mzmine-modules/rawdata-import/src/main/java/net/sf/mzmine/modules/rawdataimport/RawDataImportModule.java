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

package net.sf.mzmine.modules.rawdataimport;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nonnull;

import net.sf.mzmine.datamodel.RawDataFile;
import net.sf.mzmine.datamodel.impl.MZmineObjectBuilder;
import net.sf.mzmine.modules.MZmineModuleCategory;
import net.sf.mzmine.modules.MZmineProcessingModule;
import net.sf.mzmine.modules.rawdataimport.fileformats.XMLReadTask;
import net.sf.mzmine.parameters.ParameterSet;
import net.sf.mzmine.taskcontrol.Task;
import net.sf.mzmine.util.ExitCode;

/**
 * Raw data import module
 */
public class RawDataImportModule implements MZmineProcessingModule {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	private static final String MODULE_NAME = "Raw data import";
	private static final String MODULE_DESCRIPTION = "This module imports raw data into the project.";

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
	public ExitCode runModule(@Nonnull ParameterSet parameters,
			@Nonnull Collection<Task> tasks) {

		List<File> fileNames = parameters.getParameter(
				RawDataImportParameters.fileNames).getValue();

		for (File fileName : fileNames) {

			if ((!fileName.exists()) || (!fileName.canRead())) {
				// MZmineCore.getDesktop().displayErrorMessage("Cannot read file "
				// + fileName);
				logger.warning("Cannot read file " + fileName);
				return ExitCode.ERROR;
			}

			RawDataFile newMZmineFile = MZmineObjectBuilder.getRawDataFile();

			String extension = fileName.getName()
					.substring(fileName.getName().lastIndexOf(".") + 1)
					.toLowerCase();
			Task newTask = null;

			if (extension.endsWith("mzml") || extension.endsWith("mzdata")
					|| extension.endsWith("mzxml") || extension.endsWith("xml")) {
				newTask = new XMLReadTask(fileName, newMZmineFile);
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
