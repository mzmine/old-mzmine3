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

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.mzmine.gui.MZmineGUI;
import io.github.mzmine.util.InetUtils;
import javafx.application.Platform;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

/**
 * Check for a new version of MZmine.
 */
public class NewVersionCheck implements Runnable {

    private static final String urlAddress = "http://mzmine.github.io/version.txt";
    private static final String currentVersion = MZmineCore.getMZmineVersion();

    public enum CheckType {
        DESKTOP, MENU
    };

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final CheckType checkType;

    public NewVersionCheck(CheckType type) {
        checkType = type;
    }

    public void run() {
        // Check for updated version
        if (checkType.equals(CheckType.MENU)) {
            logger.info("Checking for updates...");
        }

        // Get version from URL
        String newestVersion;
        try {
            final URL newestVersionURL = new URL(urlAddress);
            newestVersion = InetUtils.retrieveData(newestVersionURL);
            newestVersion = newestVersion.trim();
        } catch (Exception e) {
            if (checkType.equals(CheckType.MENU)) {
                e.printStackTrace();
            }
            newestVersion = null;
        }

        if (newestVersion == null) {
            if (checkType.equals(CheckType.MENU)) {
                final String msg = "An error occured. Please make sure that you are connected to the internet or try again later.";
                logger.info(msg);
                MZmineGUI.displayMessage(msg);
            }
        } else if (currentVersion.equals(newestVersion)
                || currentVersion.equals("0.0")) {
            if (checkType.equals(CheckType.MENU)) {
                final String msg = "No updates were found - your version is up to date!";
                logger.info(msg);
                MZmineGUI.displayMessage(msg);
            }
        } else {
            final String msg = "An updated version is available: MZmine "
                    + newestVersion;
            final String msg2 = "Please download the newest version from: ";
            final String url = "http://mzmine.github.io";
            logger.info(msg);
            if (checkType.equals(CheckType.MENU)) {
                MZmineGUI.displayMessage(msg + "\n" + msg2);
            } else if (checkType.equals(CheckType.DESKTOP)) {
                MZmineGUI.setStatusBarMessage(msg + "\n" + msg2 + url);
                Platform.runLater(() -> {
                    Text t = new Text("Welcome to MZmine 3\n\n" + msg + "\n"
                            + msg2 + url);
                    t.setTextAlignment(TextAlignment.CENTER);
                    MZmineGUI.addWindow(t, "");
                });
            }
        }

    }

}
