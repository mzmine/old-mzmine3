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

package io.github.mzmine.taskcontrol;

import javax.annotation.Nullable;

import io.github.msdk.MSDKMethod;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;

public class MSDKTask extends Task<Object> {

    private MSDKMethod<?> method;
    private String title, message;
    private Double progress;

    public MSDKTask(String title, @Nullable String message,
            MSDKMethod<?> method) {
        this.title = title;
        this.message = message;
        this.method = method;
        refreshStatus();

        EventHandler<WorkerStateEvent> cancelEvent = new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerEvent) {
                method.cancel();
            }
        };

        setOnCancelled(cancelEvent);
    }

    public void refreshStatus() {
        // Progress
        if (method.getFinishedPercentage() == null)
            progress = 0.0;
        else
            progress = method.getFinishedPercentage().doubleValue();
        updateProgress(progress, 1);

        // Title and message
        updateTitle(title);
        updateMessage(message);
    }

    @Override
    protected Object call() throws Exception {
        method.execute();
        return null;
    }

}