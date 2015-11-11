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

package io.github.mzmine.modules.auditlogexport;

import java.util.Arrays;

import io.github.mzmine.parameters.ParameterSet;
import io.github.mzmine.parameters.ParameterValidator;
import io.github.mzmine.parameters.parametertypes.filenames.FileNameParameter;
import javafx.stage.FileChooser.ExtensionFilter;

public class AuditLogExportParameters extends ParameterSet {

    public static final FileNameParameter outputFile = new FileNameParameter(
            "Output file",
            "Path and name of the exported pdf file. If the file already exists, it will be overwritten.",
            "Algorithm Parameters",
            ParameterValidator.createNonEmptyValidator(),
            FileNameParameter.Type.SAVE,
            Arrays.asList(new ExtensionFilter("PDF file", "*.pdf")));

    public AuditLogExportParameters() {
        super(outputFile);
    }

}
