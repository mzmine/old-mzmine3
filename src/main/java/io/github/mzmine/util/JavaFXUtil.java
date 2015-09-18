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
 * MZmine 2; if not, write to the Free Software Foundation, Inc., 51 Franklin
 * St, Fifth Floor, Boston, MA 02110-1301 USA
 */

package io.github.mzmine.util;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * JavaFX related utilities
 */
public class JavaFXUtil {

    /**
     * Convenience method for searching above comp in the component hierarchy
     * and returns the first object of class c it finds.
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <Type> Type getAncestorOfClass(Class<Type> c, Node node) {

        while (node != null) {
            if (c.isInstance(node)) {
                return (Type) node;
            }
            node = node.getParent();
        }

        return null;
    }

    public static Node addZoomSupport(XYChart<Number, Number> chart) {

        final Rectangle rect = new Rectangle();
        rect.setManaged(false);
        rect.setFill(Color.LIGHTBLUE.deriveColor(0, 1, 1, 0.5));
        final NumberAxis yAxis = (NumberAxis) chart.getYAxis();
        final NumberAxis xAxis = (NumberAxis) chart.getXAxis();
        yAxis.setForceZeroInRange(true);
        xAxis.setForceZeroInRange(false);
        StackPane pane = new StackPane();
        pane.getChildren().addAll(chart, rect);

        chart.setOnMousePressed(event -> {
            if (event.getButton() != MouseButton.PRIMARY)
                return;
            rect.setX(event.getX());
            rect.setY(event.getY());
        });
        chart.setOnMouseDragged(event -> {
            if (event.getButton() != MouseButton.PRIMARY)
                return;
            double mouseX = event.getX();
            double mouseY = event.getY();
            double rectX = rect.getX();
            double rectY = rect.getY();
            if ((mouseX <= rectX) || (mouseY <= rectY)) {
                rect.setVisible(false);
            } else {
                rect.setWidth(mouseX - rectX);
                rect.setHeight(mouseY - rectY);
                rect.setVisible(true);
            }
        });

        chart.setOnMouseReleased(event -> {
            if (event.getButton() != MouseButton.PRIMARY)
                return;

            rect.setVisible(false);

            double mouseX = event.getX();
            double mouseY = event.getY();
            double rectX = rect.getX();
            double rectY = rect.getY();
            if (mouseX < rectX && mouseY < rectY) {
                xAxis.setAutoRanging(true);
                yAxis.setAutoRanging(true);
                return;
            }

            if ((rect.getWidth() < 5) || (rect.getHeight() < 5)) {
                return;
            }

            xAxis.setAutoRanging(false);
            yAxis.setAutoRanging(false);
            Point2D xAxisInScene = xAxis.localToScene(0, 0);
            Point2D mouseInScene = chart.localToScene(rect.getX(), rect.getY());

            double xOffset = mouseInScene.getX() - xAxisInScene.getX();
            double yOffset = xAxisInScene.getY() - mouseInScene.getY();
            if (xOffset < 0)
                xOffset = 0;
            if (yOffset < 0)
                yOffset = 0;
            if (xOffset > xAxis.getWidth())
                xOffset = xAxis.getWidth();
            if (yOffset > yAxis.getHeight())
                yOffset = yAxis.getHeight();

            double xAxisScale = xAxis.getScale();
            double yAxisScale = yAxis.getScale();

            double newXLowerBound = xAxis.getLowerBound()
                    + xOffset / xAxisScale;
            double newXUpperBound = newXLowerBound
                    + rect.getWidth() / xAxisScale;
            double newYLowerBound = yAxis.getLowerBound()
                    - (yOffset - rect.getHeight()) / yAxisScale;
            double newYUpperBound = yAxis.getLowerBound()
                    - yOffset / yAxisScale;
            xAxis.setLowerBound(newXLowerBound);
            xAxis.setUpperBound(newXUpperBound);
            yAxis.setLowerBound(newYLowerBound);
            yAxis.setUpperBound(newYUpperBound);

            xAxis.setTickUnit((newXUpperBound - newXLowerBound) / 10);
            yAxis.setTickUnit((newYUpperBound - newYLowerBound) / 10);
            
            rect.setWidth(0);
            rect.setHeight(0);


        });

        return pane;

    }

}
