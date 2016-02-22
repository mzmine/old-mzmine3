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
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.ui.RectangleEdge;

import com.google.common.base.Preconditions;
import com.google.common.collect.Range;

import io.github.msdk.MSDKException;
import io.github.msdk.datamodel.datastore.DataPointStore;
import io.github.msdk.datamodel.datastore.DataPointStoreFactory;
import io.github.msdk.datamodel.files.FileType;
import io.github.msdk.datamodel.impl.MSDKObjectBuilder;
import io.github.msdk.datamodel.msspectra.MsSpectrum;
import io.github.msdk.datamodel.msspectra.MsSpectrumType;
import io.github.msdk.datamodel.rawdata.IsolationInfo;
import io.github.msdk.datamodel.rawdata.MsFunction;
import io.github.msdk.datamodel.rawdata.MsScan;
import io.github.msdk.datamodel.rawdata.RawDataFile;
import io.github.msdk.io.mzml.MzMLFileExportMethod;
import io.github.msdk.io.txt.TxtExportAlgorithm;
import io.github.msdk.io.txt.TxtImportAlgorithm;
import io.github.msdk.spectra.isotopepattern.IsotopePatternGeneratorAlgorithm;
import io.github.msdk.spectra.splash.SplashCalculationAlgorithm;
import io.github.msdk.util.MsScanUtil;
import io.github.mzmine.gui.MZmineGUI;
import io.github.mzmine.main.MZmineCore;
import io.github.mzmine.modules.plots.chromatogram.ChromatogramPlotModule;
import io.github.mzmine.modules.plots.chromatogram.ChromatogramPlotParameters;
import io.github.mzmine.modules.plots.isotopepattern.IsotopePatternPlotModule;
import io.github.mzmine.modules.plots.isotopepattern.IsotopePatternPlotParameters;
import io.github.mzmine.modules.plots.spectrumparser.SpectrumParserPlotModule;
import io.github.mzmine.modules.plots.spectrumparser.SpectrumParserPlotParameters;
import io.github.mzmine.parameters.ParameterSet;
import io.github.mzmine.parameters.parametertypes.selectors.RawDataFilesSelection;
import io.github.mzmine.parameters.parametertypes.selectors.ScanSelection;
import io.github.mzmine.project.MZmineProject;
import io.github.mzmine.util.JavaFXUtil;
import io.github.mzmine.util.MsScanUtils;
import io.github.mzmine.util.jfreechart.ChartExportToImage;
import io.github.mzmine.util.jfreechart.ChartExportToImage.ImgFileType;
import io.github.mzmine.util.jfreechart.ChartNodeJFreeChart;
import io.github.mzmine.util.jfreechart.IntelligentItemLabelGenerator;
import io.github.mzmine.util.jfreechart.ManualZoomDialog;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.print.PrinterJob;
import javafx.scene.Cursor;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import jersey.repackaged.com.google.common.collect.Lists;

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
    private final BooleanProperty itemLabelsVisible = new SimpleBooleanProperty(
            this, "itemLabelsVisible", true);

    private File lastSaveDirectory;

    @FXML
    private BorderPane chartPane;

    @FXML
    private ChartNodeJFreeChart chartNode;

    @FXML
    private MenuItem setToMenuItem, showXICMenuItem;

    @FXML
    private Menu findMSMSMenu;

    @FXML
    private TextField splashField;

    public void initialize() {

        final XYPlot plot = chartNode.getChart().getXYPlot();

        // Do not set colors and strokes dynamically. They are instead provided
        // by the dataset and configured in configureRenderer()
        plot.setDrawingSupplier(null);

        plot.setDomainGridlinePaint(JavaFXUtil.convertColorToAWT(gridColor));
        plot.setRangeGridlinePaint(JavaFXUtil.convertColorToAWT(gridColor));

        chartNode.setCursor(Cursor.CROSSHAIR);

        // Remove the dataset if it is removed from the list
        dataSets.addListener((Change<? extends MsSpectrumDataSet> c) -> {
            while (c.next()) {
                if (c.wasRemoved()) {
                    for (MsSpectrumDataSet ds : c.getRemoved()) {
                        int index = plot.indexOf(ds);
                        plot.setDataset(index, null);
                    }
                }
            }
        });

        itemLabelsVisible.addListener((prop, oldVal, newVal) -> {
            for (MsSpectrumDataSet dataset : dataSets) {
                int dataSetIndex = plot.indexOf(dataset);
                XYItemRenderer renderer = plot.getRenderer(dataSetIndex);
                renderer.setBaseItemLabelsVisible(newVal);
            }
        });

    }

    /**
     * Add a new spectrum to the plot.
     * 
     * @param spectrum
     */
    public synchronized void addSpectrum(@Nonnull MsSpectrum spectrum,
            @Nonnull String name) {

        Preconditions.checkNotNull(spectrum);

        MsSpectrumDataSet newDataSet = new MsSpectrumDataSet(spectrum, name);
        newDataSet.mzShiftProperty().bind(mzShift);
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
            Platform.runLater(
                    () -> configureRenderer(newDataSet, datasetIndex));
        });
        newDataSet.colorProperty().addListener(e -> {
            Platform.runLater(
                    () -> configureRenderer(newDataSet, datasetIndex));
        });
        newDataSet.lineThicknessProperty().addListener(e -> {
            Platform.runLater(
                    () -> configureRenderer(newDataSet, datasetIndex));
        });
        newDataSet.showDataPointsProperty().addListener(e -> {
            Platform.runLater(
                    () -> configureRenderer(newDataSet, datasetIndex));
        });

        // Once everything is configured, add the dataset to the plot
        Platform.runLater(() -> plot.setDataset(datasetIndex, newDataSet));

    }

    private void configureRenderer(MsSpectrumDataSet dataSet,
            int dataSetIndex) {

        final MsSpectrumType renderingType = dataSet.getRenderingType();
        final XYPlot plot = chartNode.getChart().getXYPlot();

        // Set renderer
        AbstractXYItemRenderer newRenderer;
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

        // Set tooltips for legend
        newRenderer.setLegendItemToolTipGenerator((dataset, series) -> {
            if (dataset instanceof MsSpectrumDataSet) {
                return ((MsSpectrumDataSet) dataset).getDescription();
            } else
                return null;
        });

        // Set color
        Color baseColor = dataSet.getColor();
        newRenderer.setBasePaint(JavaFXUtil.convertColorToAWT(baseColor));

        // Set label generator
        XYItemLabelGenerator intelligentLabelGenerator = new IntelligentItemLabelGenerator(
                chartNode, 100, dataSet);
        newRenderer.setBaseItemLabelGenerator(intelligentLabelGenerator);
        newRenderer.setBaseItemLabelPaint(
                JavaFXUtil.convertColorToAWT(labelsColor));
        newRenderer.setBaseItemLabelsVisible(itemLabelsVisible.get());

        // Set tooltip generator
        newRenderer.setBaseToolTipGenerator(dataSet);

        plot.setRenderer(dataSetIndex, newRenderer);

    }

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
                String title = MsScanUtils
                        .createSingleLineMsScanDescription(scan);
                addSpectrum(scan, title);
                spectrumAdded = true;
            }
        }
        if (!spectrumAdded) {
            MZmineGUI.displayMessage("Scan not found");
        }
    }

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

        Preconditions.checkNotNull(spectrumText);
        Preconditions.checkNotNull(spectrumType);

        final MsSpectrum spectrum = TxtImportAlgorithm
                .parseMsSpectrum(spectrumText);
        spectrum.setSpectrumType(spectrumType);

        String spectrumName = "Manual spectrum";

        addSpectrum(spectrum, spectrumName);
    }

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
        addSpectrum(pattern, formula);
    }

    public void handlePreviousScan(Event e) {
        for (MsSpectrumDataSet dataset : dataSets) {
            MsSpectrum spectrum = dataset.getSpectrum();
            if (!(spectrum instanceof MsScan))
                continue;
            MsScan scan = (MsScan) spectrum;
            RawDataFile rawFile = scan.getRawDataFile();
            if (rawFile == null)
                return;
            int scanIndex = rawFile.getScans().indexOf(scan);
            if (scanIndex <= 0)
                return;
            MsScan prevScan = rawFile.getScans().get(scanIndex - 1);
            String title = MsScanUtils
                    .createSingleLineMsScanDescription(prevScan);
            dataset.setSpectrum(prevScan, title);
        }
    }

    public void handleNextScan(Event e) {
        for (MsSpectrumDataSet dataset : dataSets) {
            MsSpectrum spectrum = dataset.getSpectrum();
            if (!(spectrum instanceof MsScan))
                continue;
            MsScan scan = (MsScan) spectrum;
            RawDataFile rawFile = scan.getRawDataFile();
            if (rawFile == null)
                return;
            int scanIndex = rawFile.getScans().indexOf(scan);
            if (scanIndex == rawFile.getScans().size() - 1)
                return;
            MsScan nextScan = rawFile.getScans().get(scanIndex + 1);
            String title = MsScanUtils
                    .createSingleLineMsScanDescription(nextScan);
            dataset.setSpectrum(nextScan, title);
        }
    }

    public void handleChartKeyPressed(KeyEvent e) {
        final KeyCode key = e.getCode();
        switch (key) {
        case RIGHT:
            handleNextScan(e);
            e.consume();
            break;
        case LEFT:
            handlePreviousScan(e);
            e.consume();
            break;
        case F:
            handleZoomOut(e);
            e.consume();
            break;
        case M:
            handleManualZoom(e);
            e.consume();
            break;
        case S:
            handleSetupLayers(e);
            e.consume();
            break;
        default:
        }

    }

    public void handleChartMousePressed(Event e) {
        chartNode.requestFocus();
    }

    public void handleSetupLayers(Event event) {
        try {
            URL layersDialogFXML = getClass().getResource(LAYERS_DIALOG_FXML);
            FXMLLoader loader = new FXMLLoader(layersDialogFXML);
            Stage layersDialog = loader.load();
            MsSpectrumLayersDialogController controller = loader
                    .getController();
            controller.configure(dataSets, this);
            layersDialog.initModality(Modality.APPLICATION_MODAL);
            layersDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleToggleLabels(Event event) {
        itemLabelsVisible.set(!itemLabelsVisible.get());
    }

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
        final double clickedMzWithShift = Math.abs(clickedMz - mzShift.get());

        // Update the m/z shift menu item
        DecimalFormat mzFormat = MZmineCore.getConfiguration().getMZFormat();
        setToMenuItem.setText("Set to " + mzFormat.format(clickedMz) + " m/z");
        setToMenuItem.setUserData(clickedMz);

        // Update the Show XIC menu item
        showXICMenuItem.setText(
                "Show XIC of " + mzFormat.format(clickedMzWithShift) + " m/z");
        showXICMenuItem.setUserData(clickedMzWithShift);

        // Update the MS/MS menu
        findMSMSMenu.setText("Find MS/MS of "
                + mzFormat.format(clickedMzWithShift) + " m/z");
        findMSMSMenu.getItems().clear();
        MZmineProject project = MZmineCore.getCurrentProject();
        for (RawDataFile file : project.getRawDataFiles()) {
            scans: for (MsScan scan : file.getScans()) {
                if (scan.getMsFunction().getMsLevel() == 1)
                    continue;
                for (IsolationInfo isolation : scan.getIsolations()) {
                    if (!isolation.getIsolationMzRange()
                            .contains(clickedMzWithShift))
                        continue;
                    String menuLabel = MsScanUtils
                            .createSingleLineMsScanDescription(scan, isolation);
                    MenuItem msmsItem = new MenuItem(menuLabel);
                    msmsItem.setOnAction(e -> MsSpectrumPlotModule
                            .showNewSpectrumWindow(scan, true));
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

    public void handleShowXIC(Event event) {
        Double xicMz = (Double) showXICMenuItem.getUserData();
        if (xicMz == null)
            return;

        ParameterSet chromatogramParams = MZmineCore.getConfiguration()
                .getModuleParameters(ChromatogramPlotModule.class);
        chromatogramParams.getParameter(ChromatogramPlotParameters.mzRange)
                .setValue(Range.closed(xicMz - 0.005, xicMz + 0.005));
        chromatogramParams.showSetupDialog("Show XIC");

    }

    public void handleSetMzShiftTo(Event event) {
        Double newMzShift = (Double) setToMenuItem.getUserData();
        if (newMzShift == null)
            return;
        mzShift.set(newMzShift);
    }

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

    public void handleResetMzShift(Event event) {
        mzShift.set(0.0);
    }

    public void handlePrint(Event event) {
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job == null)
            return;
        boolean confirm = job.showPrintDialog(chartNode.getScene().getWindow());
        if (!confirm) {
            job.cancelJob();
            return;
        }
        boolean success = job.printPage(chartNode);
        if (success) {
            job.endJob();
        }

    }

    public void handleNormalizeIntensityScale(Event event) {
        for (MsSpectrumDataSet dataSet : dataSets) {
            dataSet.setIntensityScale(100.0);
        }
    }

    public void handleResetIntensityScale(Event event) {
        for (MsSpectrumDataSet dataSet : dataSets) {
            dataSet.resetIntensityScale();
        }
    }

    public void handleZoomOut(Event event) {
        XYPlot plot = chartNode.getChart().getXYPlot();
        plot.getDomainAxis().setAutoRange(true);
        plot.getRangeAxis().setAutoRange(true);
    }

    public void handleManualZoom(Event event) {
        XYPlot plot = chartNode.getChart().getXYPlot();
        Window parent = chartNode.getScene().getWindow();
        ManualZoomDialog dialog = new ManualZoomDialog(parent, plot);
        dialog.show();
    }

    public void handleExportImageToClipboard(Event event) {
        ChartExportToImage.exportToClipboard(chartNode);
    }

    public void handleExportJPG(Event event) {
        ChartExportToImage.showSaveDialog(chartNode, ImgFileType.JPG);
    }

    public void handleExportPNG(Event event) {
        ChartExportToImage.showSaveDialog(chartNode, ImgFileType.PNG);
    }

    public void handleExportPDF(Event event) {
        ChartExportToImage.showSaveDialog(chartNode, ImgFileType.PDF);
    }

    public void handleExportSVG(Event event) {
        ChartExportToImage.showSaveDialog(chartNode, ImgFileType.SVG);
    }

    public void handleExportEMF(Event event) {
        ChartExportToImage.showSaveDialog(chartNode, ImgFileType.EMF);
    }

    public void handleExportEPS(Event event) {
        ChartExportToImage.showSaveDialog(chartNode, ImgFileType.EPS);
    }

    public void handleExportSpectraToClipboard(Event event) {
        StringBuilder sb = new StringBuilder();
        for (MsSpectrumDataSet dataSet : dataSets) {
            MsSpectrum spectrum = dataSet.getSpectrum();
            String spectrumString = TxtExportAlgorithm
                    .spectrumToString(spectrum);
            sb.append(spectrumString);
            sb.append("\n");
        }
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(sb.toString());
        clipboard.setContent(content);
    }

    public void handleExportMzML(Event event) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export to TXT");
        fileChooser.setSelectedExtensionFilter(
                new FileChooser.ExtensionFilter("TXT", "txt"));

        // Remember last directory
        if (lastSaveDirectory != null && lastSaveDirectory.isDirectory())
            fileChooser.setInitialDirectory(lastSaveDirectory);

        // Show the file chooser
        File file = fileChooser
                .showSaveDialog(chartNode.getScene().getWindow());

        // If nothing was chosen, quit
        if (file == null)
            return;

        // If no file extension, add it
        if (!file.getName().contains(".")) {
            String newName = file.getPath() + ".txt";
            file = new File(newName);
        }

        // Save the last open directory
        lastSaveDirectory = file.getParentFile();

        final List<MsSpectrum> spectra = Lists.newArrayList();
        for (MsSpectrumDataSet dataSet : dataSets) {
            spectra.add(dataSet.getSpectrum());
        }

        // Do the export in a new thread
        final File finalFile = file;
        new Thread(() -> {
            try {

                // Create a temporary raw data file
                DataPointStore tmpStore = DataPointStoreFactory
                        .getMemoryDataStore();
                RawDataFile tmpRawFile = MSDKObjectBuilder.getRawDataFile(
                        "Exported spectra", null, FileType.UNKNOWN, tmpStore);
                int scanNum = 1;
                for (MsSpectrumDataSet dataSet : dataSets) {
                    MsSpectrum spectrum = dataSet.getSpectrum();
                    MsScan newScan;
                    if (spectrum instanceof MsScan) {
                        newScan = MsScanUtil.clone(tmpStore, (MsScan) spectrum,
                                true);
                    } else {
                        MsFunction msf = MSDKObjectBuilder.getMsFunction(
                                MsFunction.DEFAULT_MS_FUNCTION_NAME);
                        newScan = MSDKObjectBuilder.getMsScan(tmpStore, scanNum,
                                msf);
                    }
                    tmpRawFile.addScan(newScan);
                }
                MzMLFileExportMethod exporter = new MzMLFileExportMethod(
                        tmpRawFile, finalFile);
                exporter.execute();
                tmpRawFile.dispose();
            } catch (MSDKException e) {
                MZmineGUI.displayMessage("Unable to export: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();

    }

    public void handleExportMGF(Event event) {
    }

    public void handleExportMSP(Event event) {

    }

    public void handleExportTXT(Event event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export to TXT");
        fileChooser.setSelectedExtensionFilter(
                new FileChooser.ExtensionFilter("TXT", "txt"));

        // Remember last directory
        if (lastSaveDirectory != null && lastSaveDirectory.isDirectory())
            fileChooser.setInitialDirectory(lastSaveDirectory);

        // Show the file chooser
        File file = fileChooser
                .showSaveDialog(chartNode.getScene().getWindow());

        // If nothing was chosen, quit
        if (file == null)
            return;

        // If no file extension, add it
        if (!file.getName().contains(".")) {
            String newName = file.getPath() + ".txt";
            file = new File(newName);
        }

        // Save the last open directory
        lastSaveDirectory = file.getParentFile();

        final List<MsSpectrum> spectra = Lists.newArrayList();
        for (MsSpectrumDataSet dataSet : dataSets) {
            spectra.add(dataSet.getSpectrum());
        }

        // Do the export in a new thread
        final File finalFile = file;
        new Thread(() -> {
            try {
                TxtExportAlgorithm.exportSpectra(finalFile, spectra);
            } catch (IOException e) {
                MZmineGUI.displayMessage("Unable to export: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

}