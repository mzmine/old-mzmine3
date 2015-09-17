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

import java.io.InputStream;
import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import javax.annotation.Nonnull;

import io.github.mzmine.gui.MZmineGUI;
import io.github.mzmine.gui.mainwindow.MainWindowController;
import io.github.mzmine.project.MZmineProject;
import javafx.concurrent.Task;

/**
 * MZmine core functions for modules
 */
public final class MZmineCore {

    private static final @Nonnull MZmineConfiguration configuration = new MZmineConfiguration();

    private static final @Nonnull ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(
            2);

    private static @Nonnull MZmineProject currentProject = new MZmineProject();

    public static @Nonnull MZmineConfiguration getConfiguration() {
        return configuration;
    }

    public static @Nonnull String getMZmineVersion() {
        try {
            ClassLoader myClassLoader = MZmineCore.class.getClassLoader();
            InputStream inStream = myClassLoader.getResourceAsStream(
                    "META-INF/maven/io.github.mzmine/mzmine/pom.properties");
            if (inStream == null)
                return "0.0";
            Properties properties = new Properties();
            properties.load(inStream);
            String value = properties.getProperty("version");
            if (value == null)
                return "0.0";
            else
                return value;
        } catch (Exception e) {
            e.printStackTrace();
            return "0.0";
        }
    }

    public static @Nonnull MZmineProject getCurrentProject() {
        return currentProject;
    }

    public static void setCurrentProject(@Nonnull MZmineProject newProject) {
        currentProject = newProject;
    }

    public static void submitTasks(@Nonnull Collection<Task<?>> tasks) {

        for (Task<?> task : tasks) {
            MainWindowController mwc = MZmineGUI.getMainWindowController();
            if (mwc != null) {
                mwc.getTaskTable().getTasks().add(task);
            }
            executor.execute(task);
        }
    }

    public static @Nonnull ScheduledThreadPoolExecutor getTaskExecutor() {
        return executor;
    }

}
