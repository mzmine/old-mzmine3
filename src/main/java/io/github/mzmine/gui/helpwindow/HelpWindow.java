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
 * MZmine 2; if not, write to the Free Software Foundation, Inc., 51 Franklin
 * St, Fifth Floor, Boston, MA 02110-1301 USA
 */

package io.github.mzmine.gui.helpwindow;

import com.google.common.base.Strings;

import javafx.scene.control.Alert;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;

/**
 * Simple help window
 */
public class HelpWindow extends Alert {

    public HelpWindow(String helpFileURL) {

        super(AlertType.INFORMATION);

        // Titles
        setTitle("Help");
        setHeaderText("Loading help...");

        // Window parameters
        setResizable(true);
        initModality(Modality.NONE);

        // WWW browser
        WebView browser = new WebView();
        browser.setPrefSize(800, 600);
        getDialogPane().setContent(browser);

        // Load the requested page
        WebEngine webEngine = browser.getEngine();
        webEngine.load(helpFileURL);

        // Update title based on loaded page
        webEngine.titleProperty().addListener(e -> {
            final String title = webEngine.getTitle();
            if (!Strings.isNullOrEmpty(title))
                setHeaderText("MZmine help: " + title);
        });

    }

}
