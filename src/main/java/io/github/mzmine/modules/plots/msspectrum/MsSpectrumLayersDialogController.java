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

package io.github.mzmine.modules.plots.msspectrum;

import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

/**
 * MS spectrum layer setup dialog controller
 */
public class MsSpectrumLayersDialogController {

    @FXML
    private Stage dialogStage;

    @FXML
    private TableView<MsSpectrumDataSet> layersTable;

    @FXML
    public void close(Event event) {
        dialogStage.close();
    }
    
    public void setItems(ObservableList<MsSpectrumDataSet> items) {
        layersTable.setItems(items);
    }

}