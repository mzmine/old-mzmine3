/*
 * Copyright 2006-2015 The MZmine 3 Development Team
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

package io.github.mzmine.main;

import java.util.logging.Logger;

import io.github.mzmine.conf.MZmineConfiguration;
import io.github.mzmine.datamodel.MZmineProject;
import io.github.mzmine.datamodel.PeakList;
import io.github.mzmine.datamodel.RawDataFile;

/**
 * Shutdown hook - invoked on JRE shutdown. This method saves current
 * configuration to XML and closes (and removes) all opened temporary files.
 */
class ShutDownHook implements Runnable {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @Override
    public void run() {

        logger.finest("Running post-shutdown code");

        // Cancel all running tasks - this is important because tasks can spawn
        // additional processes (such as ThermoRawDump.exe on Windows) and these
        // will block the shutdown of the JVM. If we cancel the tasks, the
        // processes will be killed immediately.
        /*
         * for (WrappedTask wt : MZmineCore.getTaskController().getTaskQueue()
         * .getQueueSnapshot()) { Task t = wt.getActualTask(); if
         * ((t.getStatus() == TaskStatus.WAITING) || (t.getStatus() ==
         * TaskStatus.PROCESSING)) { t.cancel(); } }
         */

        // Save configuration
        try {
            MZmineConfiguration configuration = MZmineCore.getConfiguration();
            if (configuration != null) {
                configuration
                        .saveConfiguration(MZmineConfiguration.CONFIG_FILE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Remove all temporary files
        MZmineProject currentProject = MZmineCore.getCurrentProject();
        if (currentProject != null) {
            for (RawDataFile dataFile : currentProject.getDataFiles()) {
                dataFile.dispose();
            }
            for (PeakList peakList : currentProject.getPeakLists()) {
                peakList.dispose();
            }
        }

    }
}