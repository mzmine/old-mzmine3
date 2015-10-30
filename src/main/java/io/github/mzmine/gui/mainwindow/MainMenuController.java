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

package io.github.mzmine.gui.mainwindow;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.mzmine.gui.MZmineGUI;
import io.github.mzmine.main.MZmineCore;
import io.github.mzmine.main.NewVersionCheck;
import io.github.mzmine.main.NewVersionCheck.CheckType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;

/**
 * The controller class for conf/mainmenu.fxml
 * 
 */
public class MainMenuController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @FXML
    protected void closeProject(ActionEvent event) {
        MZmineGUI.closeProject();
    }

    @FXML
    protected void exitApplication(ActionEvent event) {
        MZmineGUI.requestQuit();
    }

    @FXML
    protected void openLink(ActionEvent event) {
        String url = "";

        // Link for menu item
        MenuItem item = (MenuItem) event.getSource();
        switch (item.getText()) {
        case "Tutorials":
            url = "http://mzmine.github.io/documentation.html";
            break;
        case "Support":
            url = "http://mzmine.github.io/support.html";
            break;
        case "Report Problem":
            url = "https://github.com/mzmine/mzmine3/issues";
            break;
        }

        // Open link in browser
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.browse(new URI(url));
            } catch (IOException | URISyntaxException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            Runtime runtime = Runtime.getRuntime();
            try {
                runtime.exec("xdg-open " + url);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @FXML
    protected void versionCheck(ActionEvent event) {
        // Check for new version of MZmine
        logger.info("Checking for new MZmine version");
        NewVersionCheck NVC = new NewVersionCheck(CheckType.MENU);
        Thread nvcThread = new Thread(NVC);
        nvcThread.setPriority(Thread.MIN_PRIORITY);
        nvcThread.start();
    }
    
    @FXML
    protected void setPreferences(ActionEvent event) {
        // Show the Preferences dialog
        logger.info("Showing the Preferences dialog");
        MZmineCore.getConfiguration().getPreferences().showSetupDialog();
    }

}
