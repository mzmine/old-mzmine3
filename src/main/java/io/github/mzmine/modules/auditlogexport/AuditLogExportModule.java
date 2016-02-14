/*
 * Copyright 2006-2016 The MZmine 3 Development Team
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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.mzmine.main.MZmineCore;
import io.github.mzmine.modules.MZmineModule;
import io.github.mzmine.modules.MZmineProcessingModule;
import io.github.mzmine.modules.io.csvexport.CsvExportModule;
import io.github.mzmine.parameters.Parameter;
import io.github.mzmine.parameters.ParameterSet;
import io.github.mzmine.project.MZmineProject;
import io.github.mzmine.project.auditlog.AuditLogEntry;
import javafx.concurrent.Task;

/**
 * Feature table import module
 */
public class AuditLogExportModule implements MZmineProcessingModule {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Nonnull
    private static final String MODULE_NAME = "Audit log export";
    @Nonnull
    private static final String MODULE_DESCRIPTION = "This module exports the audit log of the project.";

    @Override
    public @Nonnull String getName() {
        return MODULE_NAME;
    }

    @Override
    public @Nonnull String getDescription() {
        return MODULE_DESCRIPTION;
    }

    @Override
    public void runModule(@Nonnull MZmineProject project,
            @Nonnull ParameterSet parameters,
            @Nonnull Collection<Task<?>> tasks) {

        final File outputFile = parameters
                .getParameter(AuditLogExportParameters.outputFile).getValue();

        // List of modules which won't be shown in the audit log
        final List<MZmineModule> removeModules = new ArrayList<>();
        removeModules
                .add(MZmineCore.getModuleInstance(AuditLogExportModule.class));
        removeModules.add(MZmineCore.getModuleInstance(CsvExportModule.class));

        // Loop through all entries in the audit log
        List<AuditLogEntry> auditLog = MZmineCore.getCurrentProject()
                .getAuditLog();
        for (AuditLogEntry logEntry : auditLog) {

            // Don't show modules from the remove list
            if (!removeModules.contains(logEntry.getModule())) {
                String moduleName = logEntry.getModule().getName();

                /*
                 * TODO
                 */
                for (Parameter<?> pramaeter : logEntry.getParameterSet()) {
                    logger.debug(
                            pramaeter.getName() + ": " + pramaeter.getValue());
                }
            }

        }

    }

    @Override
    public @Nonnull Class<? extends ParameterSet> getParameterSetClass() {
        return AuditLogExportParameters.class;
    }
}
