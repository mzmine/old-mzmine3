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
 * MZmine 3; if not, write to the Free Software Foundation, Inc., 51 Franklin
 * St, Fifth Floor, Boston, MA 02110-1301 USA
 */

package io.github.mzmine.modules;

import java.util.Collection;

import javax.annotation.Nonnull;

import io.github.mzmine.parameters.ParameterSet;
import io.github.mzmine.project.MZmineProject;
import javafx.concurrent.Task;

/**
 * Interface representing a module that can be executed from the GUI through a
 * menu item. This can be either a data processing method (@see
 * MZmineProcessingModule) or a visualization/data analysis method.
 */
public interface MZmineRunnableModule extends MZmineModule {

    /**
     * Returns a brief module description for quick tooltips in the GUI
     * 
     * @return Module description
     */
    @Nonnull
    public String getDescription();

    /**
     * Run this module with given parameters. The module may create new Tasks
     * and add them to the 'tasks' collection. The module is not supposed to
     * submit the tasks to the TaskController by itself.
     * 
     * @param parameters
     *            ParameterSet to invoke this module with. The ParameterSet has
     *            already been cloned for exclusive use by this module,
     *            therefore the module does not need to clone it again. Upon
     *            invocation of the runModule() method it is guaranteed that the
     *            ParameterSet is of the proper class as returned by
     *            getParameterSetClass(). Also, it is guaranteed that the
     *            ParameterSet is checked by checkParameters(), therefore the
     *            module does not need to perform these checks again.
     * @param tasks
     *            A collection where the module should add its newly created
     *            Tasks, if it creates any.
     */
    public void runModule(@Nonnull MZmineProject project,
            @Nonnull ParameterSet parameters,
            @Nonnull Collection<Task<?>> tasks);

}
