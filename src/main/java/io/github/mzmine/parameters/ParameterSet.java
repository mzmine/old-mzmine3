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

import java.util.Collection;
import java.util.Optional;
import java.util.logging.Logger;

import org.controlsfx.control.PropertySheet;
import org.w3c.dom.Element;

import io.github.mzmine.main.MZmineCore;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

/**
 * Simple storage for the parameters. A typical MZmine module will inherit this
 * class and define the parameters for the constructor.
 */
public class ParameterSet implements Cloneable {

    private static Logger logger = Logger.getLogger(MZmineCore.class.getName());

    private static final String parameterElement = "parameter";
    private static final String nameAttribute = "name";

    protected final ObservableList<PropertySheet.Item> parameters = FXCollections.observableArrayList();


    public void loadValuesFromXML(Element xmlElement) {
        
    }

    public void saveValuesToXML(Element xmlElement) {
        
    }

    /**
     * Represent method's parameters and their values in human-readable format
     */
    public String toString() {

                return super.toString();
    }

    /**
     * Make a deep copy
     */
    public ParameterSet clone() {
        return this;
    }

    public ObservableList<PropertySheet.Item> getParameters() {
        return parameters;
        
    }
    
    public ButtonType showSetupDialog() {
        ParameterSetupDialog dialog = new ParameterSetupDialog(this);
        Optional<ButtonType> result = dialog.showAndWait();
        return result.get();
    }


    public boolean checkParameterValues(Collection<String> errorMessages) {
        return true;
    }

}
