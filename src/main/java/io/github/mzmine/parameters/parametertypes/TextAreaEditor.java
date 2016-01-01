/*
 * Copyright 2006-2016 The MZmine 3 Development Team
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
 * MZmine 3; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

package io.github.mzmine.parameters.parametertypes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import javax.annotation.Nullable;

import org.controlsfx.control.PropertySheet;

import io.github.mzmine.parameters.ParameterEditor;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;

/**
 * This parameter stores text in a text area
 */
public class TextAreaEditor extends BorderPane
        implements ParameterEditor<String> {

    private final TextAreaParameter textAreaParameter;
    private final TextArea textArea;
    private final Button browseButton, clearButton;

    public TextAreaEditor(PropertySheet.Item parameter) {
        if (!(parameter instanceof TextAreaParameter))
            throw new IllegalArgumentException();

        this.textAreaParameter = (TextAreaParameter) parameter;

        // The text area
        this.textArea = new TextArea();
        textArea.setPrefHeight(100);
        setCenter(textArea);

        // The Add button
        browseButton = new Button("Browse");
        browseButton.setFocusTraversable(false);
        browseButton.setOnAction(e -> {
            List<FileChooser.ExtensionFilter> extensions = textAreaParameter
                    .getExtensions();
            FileChooser fileChooser = new FileChooser();
            File lastOpenPath = textAreaParameter.getLastOpenPath();
            if (lastOpenPath != null)
                fileChooser.setInitialDirectory(lastOpenPath);
            fileChooser.setTitle("Open files");
            fileChooser.getExtensionFilters().addAll(extensions);
            Window parentWindow = this.getScene().getWindow();
            List<File> selectedFiles = fileChooser
                    .showOpenMultipleDialog(parentWindow);
            if (selectedFiles != null) {
                for (File f : selectedFiles) {

                    if ((textArea.getLength() > 0)
                            && (!textArea.getText().endsWith("\n")))
                        textArea.appendText("\n");

                    try {
                        BufferedReader input = new BufferedReader(
                                new FileReader(f));
                        try {
                            String line = null;
                            while ((line = input.readLine()) != null) {
                                textArea.appendText(line);
                                textArea.appendText("\n");
                            }
                        } finally {
                            input.close();
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                    File parentDir = f.getParentFile();
                    textAreaParameter.setLastOpenPath(parentDir);
                }
            }

        });

        // The Clear button
        clearButton = new Button("Clear");
        clearButton.setOnAction(e -> {
            textArea.clear();
        });

        // The button bar
        VBox bar = new VBox(10);
        bar.getChildren().addAll(browseButton, clearButton);
        setRight(bar);
        setMargin(bar, new Insets(10.0));

    }

    @Override
    public Node getEditor() {
        return this;
    }

    @Override
    public String getValue() {
        return textArea.getText();
    }

    @Override
    public void setValue(String value) {
        textArea.clear();
        if (value != null)
            textArea.appendText(value);
    }

    @Override
    @Nullable
    public Control getMainControl() {
        return textArea;
    }

}
