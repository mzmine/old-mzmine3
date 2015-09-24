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

package io.github.mzmine.project.auditlog;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import io.github.mzmine.modules.MZmineModule;
import io.github.mzmine.parameters.ParameterSet;
import javafx.concurrent.Task;

/**
 * Audit log entry which keeps track of the module, parameters and tasks.
 */
public class AuditLogEntry {

    private final MZmineModule module;
    private final ParameterSet parameterSet;
    private final List<AuditLogTaskRecord> taskRecords = new ArrayList<>();

    @SuppressWarnings("null")
    public AuditLogEntry(@Nonnull MZmineModule module,
            @Nonnull ParameterSet parameterSet, @Nonnull List<Task<?>> tasks) {
        this.module = module;
        this.parameterSet = parameterSet;

        for (Task<?> task : tasks) {
            AuditLogTaskRecord taskRecord = new AuditLogTaskRecord(task);
            taskRecords.add(taskRecord);
        }
    }

    @SuppressWarnings("null")
    @Nonnull
    public MZmineModule getModule() {
        return module;
    }

    @SuppressWarnings("null")
    @Nonnull
    public ParameterSet getParameterSet() {
        return parameterSet;
    }

    @SuppressWarnings("null")
    @Nonnull
    public List<AuditLogTaskRecord> getTaskRecords() {
        return taskRecords;
    }

}
