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

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.ui.RectangleEdge;

import com.google.common.base.Preconditions;
import com.google.common.collect.Range;

import io.github.msdk.datamodel.msspectra.MsSpectrum;
import io.github.msdk.datamodel.msspectra.MsSpectrumType;
import io.github.msdk.datamodel.rawdata.IsolationInfo;
import io.github.msdk.datamodel.rawdata.MsScan;
import io.github.msdk.datamodel.rawdata.RawDataFile;
import io.github.msdk.io.txt.MsSpectrumParserAlgorithm;
import io.github.msdk.spectra.isotopepattern.IsotopePatternGeneratorAlgorithm;
import io.github.msdk.spectra.splash.SplashCalculationAlgorithm;
import io.github.msdk.util.MsSpectrumUtil;
import io.github.mzmine.gui.MZmineGUI;
import io.github.mzmine.main.MZmineCore;
import io.github.mzmine.modules.plots.isotopepattern.IsotopePatternPlotModule;
import io.github.mzmine.modules.plots.isotopepattern.IsotopePatternPlotParameters;
import io.github.mzmine.modules.plots.spectrumparser.SpectrumParserPlotModule;
import io.github.mzmine.modules.plots.spectrumparser.SpectrumParserPlotParameters;
import io.github.mzmine.parameters.ParameterSet;
import io.github.mzmine.parameters.parametertypes.selectors.RawDataFilesSelection;
import io.github.mzmine.parameters.parametertypes.selectors.ScanSelection;
import io.github.mzmine.project.MZmineProject;
import io.github.mzmine.util.JavaFXUtil;
import io.github.mzmine.util.charts.jfreechart.ChartNodeJFreeChart;
import io.github.mzmine.util.charts.jfreechart.IntelligentItemLabelGenerator;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * MS spectrum plot window
 */
public class MsSpectrumPlotWindowController {

    // Colors
    private static final Color gridColor = Color.rgb(220, 220, 220, 0.3);
    private static final Color labelsColor = Color.BLACK;
    private static final Color[] plotColors = { Color.rgb(0, 0, 192), // blue
            Color.rgb(192, 0, 0), // red
            Color.rgb(0, 192, 0), // green
            Color.MAGENTA, Color.CYAN, Color.ORANGE };

    // Font
    private static final Font legendFont = new Font("SansSerif", Font.PLAIN,
            11);

    private static final String LAYERS_DIALOG_FXML = "MsSpectrumLayersDialog.fxml";

    private final ObservableList<MsSpectrumDataSet> dataSets = FXCollections
            .observableArrayList();
    private int numberOfDataSets = 0;

    private final DoubleProperty mzShift = new SimpleDoubleProperty(this,
            "mzShift", 0.0);

    @FXML
    private BorderPane chartPane;

    @FXML
    private ChartNodeJFreeChart chartNode;

    @FXML
    private MenuItem setToMenuItem;

    @FXML
    private Menu findMSMSMenu;

    @FXML
    private TextField splashField;

    @FXML
    public void initialize() {

        final XYPlot plot = chartNode.getChart().getXYPlot();

        // Do not set colors and strokes dynamically. They are instead provided
        // by the dataset and configured in configureRenderer()
        plot.setDrawingSupplier(null);

        plot.setDomainGridlinePaint(JavaFXUtil.convertColorToAWT(gridColor));
        plot.setRangeGridlinePaint(JavaFXUtil.convertColorToAWT(gridColor));

        chartNode.setCursor(Cursor.CROSSHAIR);
    }

    @FXML
    public void handleAddScan(Event e) {
        ParameterSet parameters = MZmineCore.getConfiguration()
                .getModuleParameters(MsSpectrumPlotModule.class);
        ButtonType exitCode = parameters.showSetupDialog("Add scan");
        if (exitCode != ButtonType.OK)
            return;

        final RawDataFilesSelection fileSelection = parameters
                .getParameter(MsSpectrumPlotParameters.inputFiles).getValue();
        final Integer scanNumber = parameters
                .getParameter(MsSpectrumPlotParameters.scanNumber).getValue();
        final ScanSelection scanSelection = new ScanSelection(
                Range.singleton(scanNumber), null, null, null, null, null);
        final List<RawDataFile> dataFiles = fileSelection
                .getMatchingRawDataFiles();
        boolean spectrumAdded = false;
        for (RawDataFile dataFile : dataFiles) {
            for (MsScan scan : scanSelection.getMatchingScans(dataFile)) {
                addSpectrum(scan);
                spectrumAdded = true;
            }
        }
        if (!spectrumAdded) {
            MZmineGUI.displayMessage("Scan not found");
        }
    }

    @FXML
    public void handleAddSpectrumFromText(Event e) {
        ParameterSet parameters = MZmineCore.getConfiguration()
                .getModuleParameters(SpectrumParserPlotModule.class);
        ButtonType exitCode = parameters.showSetupDialog("Parse spectrum");
        if (exitCode != ButtonType.OK)
            return;

        final String spectrumText = parameters
                .getParameter(SpectrumParserPlotParameters.spectrumText)
                .getValue();
        final MsSpectrumType spectrumType = parameters
                .getParameter(SpectrumParserPlotParameters.spectrumType)
                .getValue();
        final Double normalizedIntensity = parameters
                .getParameter(SpectrumParserPlotParameters.normalizedIntensity)
                .getValue();

        Preconditions.checkNotNull(spectrumText);
        Preconditions.checkNotNull(spectrumType);
        Preconditions.checkNotNull(normalizedIntensity);

        final MsSpectrum spectrum = MsSpectrumParserAlgorithm
                .parseMsSpectrum(spectrumText);
        spectrum.setSpectrumType(spectrumType);

        // Normalize the intensity
        double mzValues[] = spectrum.getMzValues();
        float intensityValues[] = spectrum.getIntensityValues();
        int size = spectrum.getNumberOfDataPoints();
        MsSpectrumUtil.normalizeIntensity(intensityValues, size,
                normalizedIntensity.floatValue());
        spectrum.setDataPoints(mzValues, intensityValues, size);

        addSpectrum(spectrum);
    }

    @FXML
    public void handleAddIsotopePattern(Event e) {
        ParameterSet parameters = MZmineCore.getConfiguration()
                .getModuleParameters(IsotopePatternPlotModule.class);
        ButtonType exitCode = parameters.showSetupDialog("Add isotope pattern");
        if (exitCode != ButtonType.OK)
            return;
        final String formula = parameters
                .getParameter(IsotopePatternPlotParameters.formula).getValue();
        final Double mzTolerance = parameters
                .getParameter(IsotopePatternPlotParameters.mzTolerance)
                .getValue();
        final Double minAbundance = parameters
                .getParameter(IsotopePatternPlotParameters.minAbundance)
                .getValue();
        final Double normalizedIntensity = parameters
                .getParameter(IsotopePatternPlotParameters.normalizedIntensity)
                .getValue();
        final MsSpectrum pattern = IsotopePatternGeneratorAlgorithm
                .generateIsotopes(formula, minAbundance,
                        normalizedIntensity.floatValue(), mzTolerance);
        addSpectrum(pattern);
    }

    @FXML
    public void handlePreviousScan(Event e) {

    }

    @FXML
    public void handleNextScan(Event e) {

    }

    @FXML
    public void handleSetupLayers(Event event) {
        try {
            URL layersDialogFXML = getClass().getResource(LAYERS_DIALOG_FXML);
            FXMLLoader loader = new FXMLLoader(layersDialogFXML);
            Stage layersDialog = loader.load();
            MsSpectrumLayersDialogController controller = loader
                    .getController();
            controller.setItems(dataSets);
            layersDialog.initModality(Modality.APPLICATION_MODAL);
            layersDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleContextMenuShowing(ContextMenuEvent event) {

        // Calculate the m/z value of the clicked point
        final double clickedX = event.getX();
        XYPlot plot = chartNode.getChart().getXYPlot();
        Rectangle2D chartArea = chartNode.getRenderingInfo().getPlotInfo()
                .getDataArea();
        RectangleEdge axisEdge = plot.getDomainAxisEdge();
        ValueAxis domainAxis = plot.getDomainAxis();
        final double clickedMz = domainAxis.java2DToValue(clickedX, chartArea,
                axisEdge);

        // Update the "Set to xxx" m/z shift menu item
        DecimalFormat mzFormat = MZmineCore.getConfiguration().getMZFormat();
        setToMenuItem.setText("Set to " + mzFormat.format(clickedMz));
        setToMenuItem.setUserData(clickedMz);

        // Update the MS/MS menu
        findMSMSMenu.getItems().clear();
        MZmineProject project = MZmineCore.getCurrentProject();
        for (RawDataFile file : project.getRawDataFiles()) {
            scans: for (MsScan scan : file.getScans()) {
                if (scan.getMsFunction().getMsLevel() == 1)
                    continue;
                for (IsolationInfo isolation : scan.getIsolations()) {
                    if (!isolation.getIsolationMzRange().contains(clickedMz))
                        continue;
                    String menuLabel = file.getName() + " scan #"
                            + scan.getScanNumber();
                    if (scan.getChromatographyInfo() != null)
                        menuLabel += " @" + scan.getChromatographyInfo();
                    MenuItem msmsItem = new MenuItem(menuLabel);
                    msmsItem.setOnAction(e -> MsSpectrumPlotModule
                            .showNewSpectrumWindow(scan));
                    findMSMSMenu.getItems().add(msmsItem);
                    continue scans;
                }
            }
        }
        if (findMSMSMenu.getItems().isEmpty()) {
            MenuItem noneItem = new MenuItem("None");
            noneItem.setDisable(true);
            findMSMSMenu.getItems().add(noneItem);
        }

    }

    @FXML
    public void handleSetMzShiftTo(Event event) {
        Double newMzShift = (Double) setToMenuItem.getUserData();
        if (newMzShift == null)
            return;
        mzShift.set(newMzShift);
    }

    @FXML
    public void handleSetMzShiftManually(Event event) {
        DecimalFormat mzFormat = MZmineCore.getConfiguration().getMZFormat();
        String newMzShiftString = "0.0";
        Double newMzShift = (Double) setToMenuItem.getUserData();
        if (newMzShift != null)
            newMzShiftString = mzFormat.format(newMzShift);
        TextInputDialog dialog = new TextInputDialog(newMzShiftString);
        dialog.setTitle("m/z shift");
        dialog.setHeaderText("Set m/z shift value");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(value -> {
            try {
                double newValue = Double.parseDouble(value);
                mzShift.set(newValue);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    @FXML
    public void handleResetMzShift(Event event) {
        mzShift.set(0.0);
    }

    /**
     * Add a new spectrum to the plot.
     * 
     * @param spectrum
     */
    public synchronized void addSpectrum(@Nonnull MsSpectrum spectrum) {

        Preconditions.checkNotNull(spectrum);

        MsSpectrumDataSet newDataSet = new MsSpectrumDataSet(spectrum, mzShift);
        dataSets.add(newDataSet);

        if (dataSets.size() == 1) {
            String splash = SplashCalculationAlgorithm
                    .calculateSplash(spectrum);
            splashField.setText(splash);
            splashField.setVisible(true);
        } else {
            splashField.setVisible(false);
        }

        final int datasetIndex = numberOfDataSets;
        numberOfDataSets++;

        final XYPlot plot = chartNode.getChart().getXYPlot();

        final Color newColor = plotColors[datasetIndex % plotColors.length];
        newDataSet.setColor(newColor);

        configureRenderer(newDataSet, datasetIndex);

        newDataSet.renderingTypeProperty().addListener(e -> {
            configureRenderer(newDataSet, datasetIndex);
        });
        newDataSet.colorProperty().addListener(e -> {
            configureRenderer(newDataSet, datasetIndex);
        });
        newDataSet.lineThicknessProperty().addListener(e -> {
            configureRenderer(newDataSet, datasetIndex);
        });
        newDataSet.showDataPointsProperty().addListener(e -> {
            configureRenderer(newDataSet, datasetIndex);
        });

        // Once everything is configured, add the dataset to the plot
        Platform.runLater(() -> plot.setDataset(datasetIndex, newDataSet));

    }

    private void configureRenderer(MsSpectrumDataSet dataSet,
            int dataSetIndex) {

        if (!Platform.isFxApplicationThread())
            throw new IllegalStateException(
                    "configureRenderer() must be called on JavaFX thread");

        final MsSpectrumType renderingType = dataSet.getRenderingType();
        final XYPlot plot = chartNode.getChart().getXYPlot();

        // Set renderer
        XYItemRenderer newRenderer;
        switch (renderingType) {
        case PROFILE:
        case THRESHOLDED:
            XYLineAndShapeRenderer newLineRenderer = new XYLineAndShapeRenderer();
            final int lineThickness = dataSet.getLineThickness();
            newLineRenderer.setBaseShape(
                    new Ellipse2D.Double(-2 * lineThickness, -2 * lineThickness,
                            4 * lineThickness + 1, 4 * lineThickness + 1));
            newLineRenderer.setBaseShapesFilled(true);
            newLineRenderer.setBaseShapesVisible(dataSet.getShowDataPoints());
            newLineRenderer.setDrawOutlines(false);
            Stroke baseStroke = new BasicStroke(lineThickness);
            newLineRenderer.setBaseStroke(baseStroke);
            newRenderer = newLineRenderer;
            break;
        case CENTROIDED:
        default:
            XYBarRenderer newBarRenderer = new XYBarRenderer();
            // Avoid gradients
            newBarRenderer.setBarPainter(new StandardXYBarPainter());
            newBarRenderer.setShadowVisible(false);
            newRenderer = newBarRenderer;
            break;
        }

        // Set color
        Color baseColor = dataSet.getColor();
        newRenderer.setBasePaint(JavaFXUtil.convertColorToAWT(baseColor));

        // Set label generator
        XYItemLabelGenerator intelligentLabelGenerator = new IntelligentItemLabelGenerator(
                chartNode, 100, dataSet);
        newRenderer.setBaseItemLabelGenerator(intelligentLabelGenerator);
        newRenderer.setBaseItemLabelPaint(
                JavaFXUtil.convertColorToAWT(labelsColor));
        newRenderer.setBaseItemLabelsVisible(true);

        // Set tooltip generator
        newRenderer.setBaseToolTipGenerator(dataSet);

        plot.setRenderer(dataSetIndex, newRenderer);

    }

}