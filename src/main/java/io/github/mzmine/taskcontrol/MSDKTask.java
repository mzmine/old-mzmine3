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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.msdk.MSDKMethod;
import io.github.mzmine.gui.MZmineGUI;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;

public class MSDKTask extends Task<Object> implements MZmineTask {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private MSDKMethod<?> method;
    private String title, message;

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

    @Override
    public void refreshStatus() {

        // Progress
        final Float finishedPerc = method.getFinishedPercentage();
        if (finishedPerc != null)
            updateProgress(finishedPerc.doubleValue(), 1.0);

        // Title and message
        updateTitle(title);
        updateMessage(message);
    }

    @Override
    protected Object call() throws Exception {
        Object result = null;
        try {
            result = method.execute();
        } catch (Throwable e) {
            final String msg = "Error executing task " + title + ": "
                    + e.getMessage();
            logger.error(msg, e);
            MZmineGUI.displayMessage(msg);
        }
        return result;
    }

}