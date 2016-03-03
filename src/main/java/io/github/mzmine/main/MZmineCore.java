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

package io.github.mzmine.main;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import javax.annotation.Nonnull;

import io.github.mzmine.gui.MZmineGUI;
import io.github.mzmine.gui.mainwindow.MainWindowController;
import io.github.mzmine.modules.MZmineModule;
import io.github.mzmine.modules.MZmineRunnableModule;
import io.github.mzmine.parameters.ParameterSet;
import io.github.mzmine.project.MZmineProject;
import io.github.mzmine.project.auditlog.AuditLogEntry;
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

    public static <ModuleType extends MZmineModule> ModuleType getModuleInstance(
            Class<ModuleType> moduleClass) {
        return MZmineStarter.getModuleInstance(moduleClass);
    }

    public static void runModule(
            @Nonnull Class<? extends MZmineRunnableModule> moduleClass,
            @Nonnull ParameterSet parameters) {

        MZmineRunnableModule module = (MZmineRunnableModule) getModuleInstance(
                moduleClass);

        // Usage Tracker
        GoogleAnalyticsTracker GAT = new GoogleAnalyticsTracker(
                module.getName(), "/JAVA/" + module.getName());
        Thread gatThread = new Thread(GAT);
        gatThread.setPriority(Thread.MIN_PRIORITY);
        gatThread.start();

        // Run the module
        final List<Task<?>> newTasks = new ArrayList<>();
        module.runModule(currentProject, parameters, newTasks);
        submitTasks(newTasks);

        // Log module run in audit log
        AuditLogEntry auditLogEntry = new AuditLogEntry(module, parameters,
                newTasks);
        currentProject.logProcessingStep(auditLogEntry);

    }

}
