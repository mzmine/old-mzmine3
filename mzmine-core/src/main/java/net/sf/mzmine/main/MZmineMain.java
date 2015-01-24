/*
 * Copyright 2006-2015 The MZmine 2 Development Team
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

import java.util.logging.Logger;

import javafx.application.Application;
import net.sf.mzmine.gui.MZmineGUI;

/**
 * MZmine main class
 */
public final class MZmineMain {

    private static Logger logger = Logger.getLogger(MZmineMain.class.getName());

    public static void main(String args[]) {

	/*
	 * Cleanup old temporary files
	 */
	Thread tmpFileCleanup = new Thread(new TmpFileCleanup());
	tmpFileCleanup.start();

	/*
	 * Register shutdown hook
	 */
	Thread shutDownHook = new Thread(new ShutDownHook());
	Runtime.getRuntime().addShutdownHook(shutDownHook);

	logger.finest("Starting MZmine GUI");
	Application.launch(MZmineGUI.class, args);
    }

}
