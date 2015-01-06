/*
 * Copyright 2006-2014 The MZmine 2 Development Team
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

package net.sf.mzmine.main;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.stage.Stage;

import javax.annotation.Nonnull;

import net.sf.extcos.ComponentQuery;
import net.sf.extcos.ComponentScanner;
import net.sf.mzmine.conf.MZmineConfiguration;
import net.sf.mzmine.datamodel.MZmineProject;
import net.sf.mzmine.gui.MZmineGUI;
import net.sf.mzmine.modules.MZmineProcessingModule;

/**
 * MZmine main class
 */
public final class MZmineCore {

    private static Logger logger = Logger.getLogger(MZmineCore.class.getName());

    public static void main(String args[]) {

	final Set<Class<? extends MZmineProcessingModule>> samples = new HashSet<>();

	ComponentScanner scanner = new ComponentScanner();

	scanner.getClasses(new ComponentQuery() {
	    protected void query() {
		select().from("net.sf.mzmine.modules").andStore(
			thoseImplementing(MZmineProcessingModule.class).into(
				samples));
	    }
	});

	System.out.println(samples.toString());

	Application.launch(MZmineGUI.class, args);

    }

    public static MZmineConfiguration getConfiguration() {
	return null;
    }

    public static MZmineProject getCurrentProject() {
	return null;
    }

    public static Stage getMainWindow() {
	return null;
    }

    @Nonnull
    public static String getMZmineVersion() {
	try {
	    ClassLoader myClassLoader = MZmineCore.class.getClassLoader();
	    InputStream inStream = myClassLoader
		    .getResourceAsStream("META-INF/maven/net.sf.mzmine/mzmine/pom.properties");
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

}
