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
import java.util.Properties;
import java.util.logging.Logger;

import javax.annotation.Nonnull;

import io.github.mzmine.project.MZmineProject;

/**
 * MZmine core functions for modules
 */
public final class MZmineCore {

    private static Logger logger = Logger.getLogger(MZmineCore.class.getName());

    public static MZmineConfiguration getConfiguration() {
        return null;
    }

    @Nonnull
    public static String getMZmineVersion() {
        try {
            ClassLoader myClassLoader = MZmineCore.class.getClassLoader();
            InputStream inStream = myClassLoader.getResourceAsStream(
                    "META-INF/maven/io.github.mzmine/mzmine/pom.properties");
            if (inStream == null)
                return "0.0";
            Properties properties = new Properties();
            properties.load(inStream);
            return properties.getProperty("version");
        } catch (Exception e) {
            e.printStackTrace();
            return "0.0";
        }
    }

    static MZmineProject getCurrentProject() {
        return null;
    }

}
