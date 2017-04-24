/*
 * Copyright 2006-2016 The MZmine 3 Development Team
 * 
 * This file is part of MZmine 3.
 * 
 * MZmine 3 is free software; you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * MZmine 3 is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with MZmine 3; if not,
 * write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301
 * USA
 */

package io.github.mzmine.util.jfreechart;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.SwingUtilities;

import org.apache.xmlgraphics.java2d.GraphicContext;
import org.apache.xmlgraphics.java2d.ps.EPSDocumentGraphics2D;
import org.freehep.graphicsio.emf.EMFGraphics2D;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.fx.ChartViewer;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.util.ExportUtils;

import io.github.mzmine.gui.MZmineGUI;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class JFreeChartUtils {

  private static File lastSaveDirectory;

  public enum ImgFileType {
    JPG, PNG, SVG, PDF, EMF, EPS
  };

  public static void printChart(ChartViewer chartNode) {

    // As of java 1.8.0_74, the JavaFX printing support seems to do poor
    // job. It creates pixelated, low-resolution print outs. For that
    // reason, we use the AWT PrinterJob class, until the JavaFX printing
    // support is improved.
    SwingUtilities.invokeLater(() -> {
      PrinterJob job = PrinterJob.getPrinterJob();
      PageFormat pf = job.defaultPage();
      PageFormat pf2 = job.pageDialog(pf);
      if (pf2 == pf)
        return;
      ChartPanel p = new ChartPanel(chartNode.getChart());
      job.setPrintable(p, pf2);
      if (!job.printDialog())
        return;
      try {
        job.print();
      } catch (PrinterException e) {
        e.printStackTrace();
        MZmineGUI.displayMessage("Error printing: " + e.getMessage());
      }
    });
  }

  public static void showSaveDialog(ChartViewer chartNode, ImgFileType fileType) {
    FileChooser fileChooser = new FileChooser();
    switch (fileType) {

      case JPG:
        fileChooser.setTitle("Export to JPG");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("JPEG", "*.jpg"));
        break;

      case PNG:
        fileChooser.setTitle("Export to PNG");
        fileChooser.getExtensionFilters()
            .add(new ExtensionFilter("Portable Network Graphics (PNG)", "*.png"));
        break;

      case SVG:
        fileChooser.setTitle("Export to SVG");
        fileChooser.getExtensionFilters()
            .add(new ExtensionFilter("Scalable Vector Graphics (SVG)", "*.svg"));
        break;

      case PDF:
        fileChooser.setTitle("Export to PDF");
        fileChooser.getExtensionFilters()
            .add(new ExtensionFilter("Portable Document Format (PDF)", "*.pdf"));
        break;

      case EMF:
        fileChooser.setTitle("Export to EMF");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("EMF image", "*.emf"));
        break;

      case EPS:
        fileChooser.setTitle("Export to EPS");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("EPS Image", "*.eps"));
        break;
    }

    // Remember last directory
    if (lastSaveDirectory != null && lastSaveDirectory.isDirectory())
      fileChooser.setInitialDirectory(lastSaveDirectory);

    // Show the file chooser
    File file = fileChooser.showSaveDialog(chartNode.getScene().getWindow());

    // If nothing was chosen, quit
    if (file == null)
      return;

    // Save the last open directory
    lastSaveDirectory = file.getParentFile();

    // Do the export in a new thread
    final File finalFile = file;
    new Thread(() -> {
      exportToImageFile(chartNode, finalFile, fileType);
    }).start();
  }

  public static void exportToClipboard(ChartViewer chartNode) {
    final Clipboard clipboard = Clipboard.getSystemClipboard();
    final ClipboardContent content = new ClipboardContent();
    final int width = (int) chartNode.getWidth();
    final int height = (int) chartNode.getHeight();
    WritableImage img = new WritableImage(width, height);
    SnapshotParameters params = new SnapshotParameters();
    chartNode.snapshot(params, img);
    content.putImage(img);
    clipboard.setContent(content);
  }

  public static void exportToImageFile(ChartViewer chartNode, File file, ImgFileType fileType) {

    final JFreeChart chart = chartNode.getChart();
    final int width = (int) chartNode.getWidth();
    final int height = (int) chartNode.getHeight();

    try {

      switch (fileType) {

        case JPG:
          ExportUtils.writeAsJPEG(chart, width, height, file);
          break;

        case PNG:
          ExportUtils.writeAsPNG(chart, width, height, file);
          break;

        case SVG:
          setDrawSeriesLineAsPath(chart, true);
          ExportUtils.writeAsSVG(chart, width, height, file);
          setDrawSeriesLineAsPath(chart, false);
          break;

        case PDF:
          setDrawSeriesLineAsPath(chart, true);
          ExportUtils.writeAsPDF(chart, width, height, file);
          setDrawSeriesLineAsPath(chart, false);
          break;

        case EMF:
          FileOutputStream out2 = new FileOutputStream(file);
          setDrawSeriesLineAsPath(chart, true);
          EMFGraphics2D g2d2 = new EMFGraphics2D(out2, new Dimension(width, height));
          g2d2.startExport();
          chart.draw(g2d2, new Rectangle(width, height));
          g2d2.endExport();
          setDrawSeriesLineAsPath(chart, false);
          break;

        case EPS:
          FileOutputStream out = new FileOutputStream(file);
          setDrawSeriesLineAsPath(chart, true);
          EPSDocumentGraphics2D g2d = new EPSDocumentGraphics2D(false);
          g2d.setGraphicContext(new GraphicContext());
          g2d.setupDocument(out, width, height);
          chart.draw(g2d, new Rectangle(width, height));
          g2d.finish();
          setDrawSeriesLineAsPath(chart, false);
          out.close();
          break;

      }

    } catch (IOException e) {
      MZmineGUI.displayMessage("Unable to save image: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private static void setDrawSeriesLineAsPath(JFreeChart chart, boolean usePath) {

    final XYPlot plot = chart.getXYPlot();

    for (int i = 0; i < plot.getRendererCount(); i++) {
      XYItemRenderer renderer = plot.getRenderer(i);
      if (renderer instanceof XYLineAndShapeRenderer) {
        XYLineAndShapeRenderer r = (XYLineAndShapeRenderer) renderer;
        r.setDrawSeriesLineAsPath(usePath);
      }
    }
  }

}
