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

import java.nio.file.Paths;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.mzmine.gui.MZmineGUI;
import javafx.application.Application;

/**
 * MZmine main class
 */
public final class MZmineMain {

	private static final Logger logger = LoggerFactory.getLogger(MZmineMain.class);

	public static void main(String args[]) {

		/*
		 * In the beginning, set the default locale to English, to avoid
		 * problems with conversion of numbers etc. (e.g. decimal separator may
		 * be . or , depending on the locale)
		 */
		Locale.setDefault(new Locale("en", "US"));

		/*
		 * Configure the logging properties before we start logging
		 */
		MZmineLogging.configureLogging();

		/*
		 * Report current working directory
		 */
		String cwd = Paths.get(".").toAbsolutePath().normalize().toString();
		logger.info("MZmine " + MZmineCore.getMZmineVersion() + " starting");
		logger.debug("Working directory is " + cwd);

		/*
		 * Cleanup old temporary files on a new thread
		 */
		TmpFileCleanup cleanupClass = new TmpFileCleanup();
		Thread cleanupThread = new Thread(cleanupClass);
		cleanupThread.setPriority(Thread.MIN_PRIORITY);
		cleanupThread.start();

		/*
		 * Register shutdown hook
		 */
		ShutDownHook shutDownHook = new ShutDownHook();
		Thread shutDownThread = new Thread(shutDownHook);
		Runtime.getRuntime().addShutdownHook(shutDownThread);

		/*
		 * Load modules on a new thread after the GUI has started
		 */
		MZmineStarter moduleStarter = new MZmineStarter();
		Thread moduleStarterThread = new Thread(moduleStarter);
		moduleStarterThread.setPriority(Thread.MIN_PRIORITY);
		moduleStarterThread.start();

		/*
		 * Usage Tracker
		 */
		GoogleAnalyticsTracker GAT = new GoogleAnalyticsTracker("MZmine Loaded (GUI mode)", "/JAVA/Main/GUI");
		Thread gatThread = new Thread(GAT);
		gatThread.setPriority(Thread.MIN_PRIORITY);
		gatThread.start();

		/*
		 * Start the JavaFX GUI
		 */
		logger.info("Starting MZmine GUI");
		Application.launch(MZmineGUI.class, args);

	}

}
