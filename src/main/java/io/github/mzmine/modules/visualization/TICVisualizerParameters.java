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

package io.github.mzmine.modules.visualization;

import java.util.List;

import org.controlsfx.control.PropertySheet;
import org.controlsfx.property.editor.DefaultPropertyEditorFactory;
import org.controlsfx.property.editor.Editors;
import org.controlsfx.property.editor.PropertyEditor;

import io.github.mzmine.parameters.parametertypes.ComboParameter;
import io.github.mzmine.parameters.parametertypes.StringParameter;
import javafx.beans.property.SimpleObjectProperty;
import javafx.util.Callback;

public class TICVisualizerParameters extends PropertySheet {

    /**
     * The data file.
     */
    public final PropertySheet.Item dataFiles = new StringParameter("aa","bbb", "ccc");
    // public final PropertySheet.Item dataFiles = new RawDataFilesParameter();

    /**
     * Scans (used to be RT range).
     */
    /*public final PropertySheet.Item scanSelection = new ScanSelectionParameter(
            new ScanSelection(1));*/

    /**
     * Type of plot.
     */
    /*public final PropertySheet.Item plotType = new ComboParameter<TICPlotType>(
            "Plot type",
            "Type of Y value calculation (TIC = sum, base peak = max)",
            TICPlotType.values());*/

    /**
     * m/z range.
     */
   // public static final PropertySheet.Item mzRange = new MZRangeParameter();

    /**
     * Create the parameter set.
     */
    public TICVisualizerParameters() {
        getItems().addAll(dataFiles);
        
        SimpleObjectProperty<Callback<PropertySheet.Item, PropertyEditor<?>>> propertyEditorFactory = new SimpleObjectProperty<>(this, "propertyEditor", new DefaultPropertyEditorFactory());

        setPropertyEditorFactory(new Callback<PropertySheet.Item, PropertyEditor<?>>() {
            @Override
            public PropertyEditor<?> call(PropertySheet.Item param) {
                if(param.getValue() instanceof List) {
                    return Editors.createChoiceEditor(param, (List) param.getValue());
                }

                return propertyEditorFactory.get().call(param);
            }
        });
        
    }

}
