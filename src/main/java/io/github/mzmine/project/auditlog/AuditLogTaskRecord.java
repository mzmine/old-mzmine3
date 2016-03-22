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

package io.github.mzmine.project.auditlog;

import javax.annotation.Nonnull;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.Worker.State;

/**
 * Task record which keeps track of the tasks for an audit log entry.
 */
public class AuditLogTaskRecord {

    private final String taskMessage;
    private final String taskTitle;
    private State taskStatus;

    public AuditLogTaskRecord(@Nonnull Task<?> task) {
        this.taskMessage = task.getMessage();
        this.taskTitle = task.getTitle();

        task.stateProperty().addListener(new ChangeListener<State>() {
            public void changed(ObservableValue<? extends State> ov, State oldState,
                    State newState) {
                taskStatus = newState;
            }
        });

    }

    @Nonnull
    public String getTaskMessage() {
        return taskMessage;
    }

    @Nonnull
    public String getTaskTitle() {
        return taskTitle;
    }

    @Nonnull
    public State getTaskStatus() {
        return taskStatus;
    }
}
