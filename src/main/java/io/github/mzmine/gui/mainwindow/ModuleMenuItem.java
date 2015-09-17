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

package io.github.mzmine.gui.mainwindow;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import io.github.mzmine.gui.MZmineGUI;
import io.github.mzmine.main.MZmineCore;
import io.github.mzmine.main.MZmineStarter;
import io.github.mzmine.modules.MZmineModule;
import io.github.mzmine.modules.MZmineRunnableModule;
import io.github.mzmine.parameters.ParameterSet;
import io.github.mzmine.project.MZmineProject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;

/**
 * 
 */
public final class ModuleMenuItem extends MenuItem {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    private final StringProperty moduleClass = new SimpleStringProperty();

    @SuppressWarnings({ "unchecked", "null" })
    public ModuleMenuItem() {
        setOnAction(event -> {
            logger.info("Menu item activated: " + event);
            Class<? extends MZmineModule> moduleJavaClass;
            try {
                moduleJavaClass = (Class<? extends MZmineModule>) Class
                        .forName(moduleClass.get());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                MZmineGUI.displayMessage(
                        "Cannot find module class " + moduleClass.get());
                return;
            }

            MZmineModule module = MZmineStarter
                    .getModuleInstance(moduleJavaClass);

            if (module == null) {
                MZmineGUI.displayMessage(
                        "Cannot find module of class " + moduleClass.get());
                return;
            }

            if (!(module instanceof MZmineRunnableModule)) {
                MZmineGUI.displayMessage("Cannot run module " + module.getName()
                        + ", because it does not implement the MZmineRunnableModule interface");
                return;
            }

            MZmineRunnableModule runnableModule = (MZmineRunnableModule) module;
            ParameterSet moduleParameters = MZmineCore.getConfiguration()
                    .getModuleParameters(moduleJavaClass);

            logger.info("Setting parameters for module " + module.getName());
            ButtonType exitCode = moduleParameters.showSetupDialog();
            if (exitCode == ButtonType.OK) {
                ParameterSet parametersCopy = moduleParameters.clone();
                logger.finest("Starting module " + module.getName()
                        + " with parameters " + parametersCopy);
                List<Task<?>> tasks = new ArrayList<>();
                MZmineProject project = MZmineCore.getCurrentProject();
                logger.info("Starting module " + module.getName());
                runnableModule.runModule(project, parametersCopy, tasks);
                MZmineCore.submitTasks(tasks);
            }
        });
    }

    public String getModuleClass() {
        return moduleClass.get();
    }

    public void setModuleClass(String newClass) {
        moduleClass.set(newClass);
    }

    public StringProperty moduleClassProperty() {
        return moduleClass;
    }

}
