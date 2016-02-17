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

package io.github.mzmine.util.charts.jfreechart;

import java.net.URL;
import java.text.NumberFormat;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;

public class ManualZoomDialog extends Dialog<Void> {

    private static final String DIALOG_FXML = "ManualZoomDialog.fxml";

    private ValueAxis xAxis;
    private ValueAxis yAxis;

    private Logger logger = Logger.getLogger(this.getClass().getName());

    @FXML
    public Label xAxisLabel;
    
    @FXML
    public Label yAxisLabel;
    
    @FXML
    public TextField xAxisRangeMin;
    
    @FXML
    public TextField yAxisRangeMin;

    @FXML
    public TextField xAxisRangeMax;
    
    @FXML
    public TextField yAxisRangeMax;

    /**
     * Constructor
     */
    public ManualZoomDialog(Window parent, XYPlot plot) {

        initOwner(parent);

        setGraphic(new ImageView("file:icons/axesicon.png"));

        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        try {
            URL layersDialogFXML = getClass().getResource(DIALOG_FXML);
            FXMLLoader loader = new FXMLLoader(layersDialogFXML);
            GridPane grid = loader.load();
            getDialogPane().setContent(grid);
        } catch (Exception e) {
            e.printStackTrace();
        }

        xAxis = plot.getDomainAxis();
        yAxis = plot.getRangeAxis();

        NumberFormat xAxisFormatter;
        if (xAxis instanceof NumberAxis)
            xAxisFormatter = ((NumberAxis) xAxis).getNumberFormatOverride();
        else
            xAxisFormatter = NumberFormat.getNumberInstance();

        NumberFormat yAxisFormatter;
        if (yAxis instanceof NumberAxis)
            yAxisFormatter = ((NumberAxis) yAxis).getNumberFormatOverride();
        else
            yAxisFormatter = NumberFormat.getNumberInstance();

        
    }

  
/*
    private void getValuesToControls() {

        checkXAutoRange.setSelected(xAxis.isAutoRange());
        fieldXMin.setValue(xAxis.getRange().getLowerBound());
        fieldXMax.setValue(xAxis.getRange().getUpperBound());
        if (xAxis instanceof NumberAxis) {
            checkXAutoTick.setSelected(xAxis.isAutoTickUnitSelection());
            fieldXTick.setValue(((NumberAxis) xAxis).getTickUnit().getSize());
        }

        checkYAutoRange.setSelected(yAxis.isAutoRange());
        fieldYMin.setValue(yAxis.getRange().getLowerBound());
        fieldYMax.setValue(yAxis.getRange().getUpperBound());
        if (yAxis instanceof NumberAxis) {
            checkYAutoTick.setSelected(yAxis.isAutoTickUnitSelection());
            fieldYTick.setValue(((NumberAxis) yAxis).getTickUnit().getSize());
        }

        updateAutoRangeAvailability();
    }

    private void updateAutoRangeAvailability() {
        if (checkXAutoRange.isSelected()) {
            fieldXMax.setEnabled(false);
            fieldXMin.setEnabled(false);
        } else {
            fieldXMax.setEnabled(true);
            fieldXMin.setEnabled(true);
        }

        if (checkXAutoTick.isSelected()) {
            fieldXTick.setEnabled(false);
        } else {
            fieldXTick.setEnabled(true);
        }

        if (checkYAutoRange.isSelected()) {
            fieldYMax.setEnabled(false);
            fieldYMin.setEnabled(false);
        } else {
            fieldYMax.setEnabled(true);
            fieldYMin.setEnabled(true);
        }

        if (checkYAutoTick.isSelected()) {
            fieldYTick.setEnabled(false);
        } else {
            fieldYTick.setEnabled(true);
        }

    }

    private boolean setValuesToPlot() {
        if (checkXAutoRange.isSelected()) {

            xAxis.setAutoRange(true);

        } else {

            double lower = ((Number) fieldXMin.getValue()).doubleValue();
            double upper = ((Number) fieldXMax.getValue()).doubleValue();
            if (lower > upper) {
                displayMessage("Invalid " + xAxis.getLabel() + " range.");
                return false;
            }
            xAxis.setRange(lower, upper);

        }

        if (xAxis instanceof NumberAxis) {

            if (checkXAutoTick.isSelected()) {
                xAxis.setAutoTickUnitSelection(true);

            } else {
                double tickSize = ((Number) fieldXTick.getValue())
                        .doubleValue();
                ((NumberAxis) xAxis).setTickUnit(new NumberTickUnit(tickSize));
            }

        }

        if (checkYAutoRange.isSelected()) {

            yAxis.setAutoRange(true);

        } else {

            double lower = ((Number) fieldYMin.getValue()).doubleValue();
            double upper = ((Number) fieldYMax.getValue()).doubleValue();
            if (lower > upper) {
                displayMessage("Invalid " + yAxis.getLabel() + " range.");
                return false;
            }
            yAxis.setRange(lower, upper);
        }

        if (yAxis instanceof NumberAxis) {

            if (checkYAutoTick.isSelected()) {
                yAxis.setAutoTickUnitSelection(true);

            } else {
                double tickSize = ((Number) fieldYTick.getValue())
                        .doubleValue();
                ((NumberAxis) yAxis).setTickUnit(new NumberTickUnit(tickSize));
            }

        }

        return true;
    }

    private void displayMessage(String msg) {
        try {
            logger.info(msg);
            JOptionPane.showMessageDialog(this, msg, "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception exce) {
        }
    }

    public ExitCode getExitCode() {
        return exitCode;
    }*/

}
