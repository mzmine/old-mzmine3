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

package net.sf.mzmine.modules.peaklistmethods.io.xmlexport;

import net.sf.mzmine.parameters.Parameter;
import net.sf.mzmine.parameters.impl.SimpleParameterSet;
import net.sf.mzmine.parameters.parametertypes.BooleanParameter;
import net.sf.mzmine.parameters.parametertypes.FileNameParameter;
import net.sf.mzmine.parameters.parametertypes.PeakListsParameter;

public class XMLExportParameters extends SimpleParameterSet {

	public static final PeakListsParameter peakList = new PeakListsParameter(1, 1);

	public static final FileNameParameter filename = new FileNameParameter(
			"Filename",
			"Name of exported peak list file name. If the file exists, it will be overwritten.",
			"mpl");

	public static final BooleanParameter compression = new BooleanParameter(
			"Compressed file", "Generates a compressed file (.zip)");

	public XMLExportParameters() {
		super(new Parameter[] { peakList, filename, compression });
	}
}
