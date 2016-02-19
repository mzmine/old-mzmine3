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

package io.github.mzmine.util.jfreechart;

import java.net.URL;
import java.text.NumberFormat;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.XYPlot;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;
import javafx.util.converter.NumberStringConverter;

public class ManualZoomDialog extends Dialog<Void> {

    private static final String DIALOG_FXML = "ManualZoomDialog.fxml";

    private NumberAxis xAxis, yAxis;

    @FXML
    private Label xAxisLabel;

    @FXML
    private Label yAxisLabel;

    @FXML
    private TextField xAxisRangeMin;

    @FXML
    private TextField yAxisRangeMin;

    @FXML
    private TextField xAxisRangeMax;

    @FXML
    private TextField yAxisRangeMax;

    @FXML
    private TextField xAxisTickSize;

    @FXML
    private TextField yAxisTickSize;

    @FXML
    private CheckBox xAxisAutoRange;

    @FXML
    private CheckBox yAxisAutoRange;

    @FXML
    private CheckBox xAxisAutoTickSize;

    @FXML
    private CheckBox yAxisAutoTickSize;

    /**
     * Constructor
     */
    public ManualZoomDialog(Window parent, XYPlot plot) {

        initOwner(parent);

        setTitle("Manual zoom");
        
        setGraphic(new ImageView("file:icons/axesicon.png"));

        getDialogPane().getButtonTypes().addAll(ButtonType.OK,
                ButtonType.CANCEL);

        xAxis = (NumberAxis) plot.getDomainAxis();
        yAxis = (NumberAxis) plot.getRangeAxis();

        try {
            URL layersDialogFXML = getClass().getResource(DIALOG_FXML);
            FXMLLoader loader = new FXMLLoader(layersDialogFXML);
            loader.setController(this);
            GridPane grid = loader.load();
            getDialogPane().setContent(grid);
        } catch (Exception e) {
            e.printStackTrace();
        }

        final Button btOk = (Button) getDialogPane()
                .lookupButton(ButtonType.OK);
        btOk.addEventFilter(ActionEvent.ACTION, event -> {
            commitChanges(event);
        });

    }

    @FXML
    public void initialize() {

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

        xAxisLabel.setText(xAxis.getLabel());
        yAxisLabel.setText(yAxis.getLabel());

        xAxisRangeMin.setTextFormatter(
                new TextFormatter<>(new NumberStringConverter(xAxisFormatter)));
        xAxisRangeMin.disableProperty().bind(xAxisAutoRange.selectedProperty());
        xAxisRangeMin.setText(String.valueOf(xAxis.getLowerBound()));
        xAxisRangeMax.setTextFormatter(
                new TextFormatter<>(new NumberStringConverter(xAxisFormatter)));
        xAxisRangeMax.disableProperty().bind(xAxisAutoRange.selectedProperty());
        xAxisRangeMax.setText(String.valueOf(xAxis.getUpperBound()));
        xAxisAutoRange.setSelected(xAxis.isAutoRange());

        yAxisRangeMin.setTextFormatter(
                new TextFormatter<>(new NumberStringConverter(yAxisFormatter)));
        yAxisRangeMin.setText(String.valueOf(yAxis.getLowerBound()));
        yAxisRangeMin.disableProperty().bind(yAxisAutoRange.selectedProperty());
        yAxisRangeMax.setTextFormatter(
                new TextFormatter<>(new NumberStringConverter(yAxisFormatter)));
        yAxisRangeMax.setText(String.valueOf(yAxis.getUpperBound()));
        yAxisRangeMax.disableProperty().bind(yAxisAutoRange.selectedProperty());
        yAxisAutoRange.setSelected(yAxis.isAutoRange());

        xAxisTickSize.disableProperty()
                .bind(xAxisAutoTickSize.selectedProperty());
        xAxisTickSize.setText(String.valueOf(xAxis.getTickUnit().getSize()));
        xAxisAutoTickSize.setSelected(xAxis.isAutoTickUnitSelection());

        yAxisTickSize.setTextFormatter(
                new TextFormatter<>(new NumberStringConverter(yAxisFormatter)));
        yAxisTickSize.setText(String.valueOf(yAxis.getTickUnit().getSize()));
        yAxisTickSize.disableProperty()
                .bind(yAxisAutoTickSize.selectedProperty());
        yAxisAutoTickSize.setSelected(yAxis.isAutoTickUnitSelection());

    }

    private void commitChanges(ActionEvent event) {
        
        if (xAxisAutoRange.isSelected()) {
            xAxis.setAutoRange(true);
        } else {

            double lower = Double.parseDouble(xAxisRangeMin.getText());
            double upper = Double.parseDouble(xAxisRangeMax.getText());
            if (lower > upper) {
                Alert alert = new Alert(AlertType.ERROR,
                        "Invalid " + xAxis.getLabel() + " range.");
                alert.show();
                event.consume();
                return;
            }
            xAxis.setRange(lower, upper);
        }

        if (xAxisAutoTickSize.isSelected()) {
            xAxis.setAutoTickUnitSelection(true);
        } else {
            double tickSize = Double.parseDouble(xAxisTickSize.getText());
            xAxis.setTickUnit(new NumberTickUnit(tickSize));
        }

        if (yAxisAutoRange.isSelected()) {
            yAxis.setAutoRange(true);
        } else {

            double lower = Double.parseDouble(yAxisRangeMin.getText());
            double upper = Double.parseDouble(yAxisRangeMax.getText());
            if (lower > upper) {
                Alert alert = new Alert(AlertType.ERROR,
                        "Invalid " + yAxis.getLabel() + " range.");
                alert.show();
                event.consume();
                return;
            }
            yAxis.setRange(lower, upper);
        }

        if (yAxisAutoTickSize.isSelected()) {
            yAxis.setAutoTickUnitSelection(true);
        } else {
            double tickSize = Double.parseDouble(yAxisTickSize.getText());
            yAxis.setTickUnit(new NumberTickUnit(tickSize));
        }

    }

}
