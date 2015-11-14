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
 * MZmine 3; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

package io.github.mzmine.parameters.parametertypes.filenames;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
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
 * This parameter stores filenames
 */
public class FileNamesEditor extends BorderPane
        implements ParameterEditor<List<File>> {

    private final FileNamesParameter fileNamesParameter;
    private final TextArea textArea;
    private final Button browseButton, clearButton;

    public FileNamesEditor(PropertySheet.Item parameter) {
        if (!(parameter instanceof FileNamesParameter))
            throw new IllegalArgumentException();

        this.fileNamesParameter = (FileNamesParameter) parameter;

        // The text area
        this.textArea = new TextArea();
        textArea.setPrefHeight(100);
        setCenter(textArea);

        // The Add button
        browseButton = new Button("Browse");
        browseButton.setFocusTraversable(false);
        browseButton.setOnAction(e -> {
            List<FileChooser.ExtensionFilter> extensions = fileNamesParameter
                    .getExtensions();
            FileChooser fileChooser = new FileChooser();
            File lastOpenPath = fileNamesParameter.getLastOpenPath();
            if ((lastOpenPath != null) && (lastOpenPath.isDirectory()))
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
                    textArea.appendText(f.getPath());
                    File parentDir = f.getParentFile();
                    fileNamesParameter.setLastOpenPath(parentDir);
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
    public List<File> getValue() {
        List<File> list = new ArrayList<>();
        String text = textArea.getText();
        BufferedReader reader = new BufferedReader(new StringReader(text));
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                // Skip lines that only contain whitespace
                if (line.trim().length() == 0)
                    continue;
                File f = new File(line);
                list.add(f);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void setValue(List<File> value) {
        textArea.clear();
        if (value != null) {
            for (File f : value) {
                textArea.appendText(f.getPath() + "\n");
            }
        }
    }

    @Override
    @Nullable
    public Control getMainControl() {
        return textArea;
    }

}
