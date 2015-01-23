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

package net.sf.mzmine.gui;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;

/**
 * MZmine menu class
 */
public final class MZmineMenuBar extends MenuBar {

    private Menu menu1 = new Menu("File");
    private Menu menu2 = new Menu("Options");
    private Menu menu3 = new Menu("Help");

    MZmineMenuBar() {

	// On Mac OS, the menu bar is always at the top of the screen. Setting
	// the following property will set this native-like behavior
	setUseSystemMenuBar(true);
	
	getMenus().addAll(menu1, menu2, menu3);
    }

}
