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

import java.io.File;
import java.util.List;

import org.controlsfx.control.PropertySheet;

import com.google.common.base.Strings;

import io.github.mzmine.parameters.ParameterEditor;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Window;

/**
 * This parameter stores filenames
 */
public class FileNameEditor extends BorderPane
        implements ParameterEditor<File> {

    private final FileNameParameter fileNameParameter;
    private final TextField textField;
    private final Button browseButton;

    public FileNameEditor(PropertySheet.Item parameter) {
        if (!(parameter instanceof FileNameParameter))
            throw new IllegalArgumentException();

        this.fileNameParameter = (FileNameParameter) parameter;

        // The text field
        this.textField = new TextField();
        setCenter(textField);

        // The Browse button
        browseButton = new Button("Browse");
        browseButton.setFocusTraversable(false);
        setRight(browseButton);
        setMargin(browseButton, new Insets(0, 0, 0, 10.0));
        browseButton.setOnAction(e -> {
            List<FileChooser.ExtensionFilter> extensions = fileNameParameter
                    .getExtensions();
            FileChooser fileChooser = new FileChooser();
            File lastOpenPath = fileNameParameter.getLastOpenPath();
            if (lastOpenPath != null)
                fileChooser.setInitialDirectory(lastOpenPath);
            fileChooser.setTitle("Find file");
            if (extensions != null) {
                fileChooser.getExtensionFilters().addAll(extensions);
            }
            Window parentWindow = this.getScene().getWindow();
            File selectedFile;

            if (fileNameParameter
                    .getFileNameParameterType() == FileNameParameter.Type.OPEN)
                selectedFile = fileChooser.showOpenDialog(parentWindow);
            else
                selectedFile = fileChooser.showSaveDialog(parentWindow);
            if (selectedFile != null) {
                textField.setText(selectedFile.getPath());
                File parentDir = selectedFile.getParentFile();
                fileNameParameter.setLastOpenPath(parentDir);
            }
        });

    }

    @Override
    public Node getEditor() {
        return this;
    }

    @Override
    public File getValue() {
        String textValue = textField.getText();
        if (!Strings.isNullOrEmpty(textValue))
            return new File(textValue);
        else
            return null;
    }

    @Override
    public void setValue(File value) {
        if (value != null)
            textField.setText(value.getPath());
        else
            textField.clear();
    }

    @Override
    public Control getMainControl() {
        return textField;
    }

}
