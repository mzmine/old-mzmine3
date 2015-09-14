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

package io.github.mzmine.parameters;

import java.net.URL;

import org.controlsfx.control.PropertySheet;

import io.github.mzmine.gui.helpwindow.HelpWindow;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;

/**
 * Parameter setup dialog
 */
public class ParameterSetupDialog extends Alert {

    ParameterSetupDialog(ParameterSet parameters) {
        
        super(AlertType.CONFIRMATION);
        
        setTitle("Parameters");
        setHeaderText("Please set parameter values");
        setResizable(true);

        
        ButtonType helpButtonType = new ButtonType("Help", ButtonData.HELP);
        
        
        getDialogPane().getButtonTypes().add(helpButtonType);
        
        Button helpButton = (Button) getDialogPane().lookupButton(helpButtonType);
        helpButton.setOnAction(e -> {
            
            System.out.println(e);
            URL r = parameters.getClass().getResource("help/help.html");
            System.out.println(r);
            
            HelpWindow hw = new HelpWindow(r.toString());
            hw.show();
            
            e.consume();
        });
        
        ObservableList<PropertySheet.Item> parametersList = parameters.getParameters();
        PropertySheet sheet = new PropertySheet(parametersList);
        sheet.setPrefSize(600.0, 500.0);
        getDialogPane().setContent(sheet);

    }

}
