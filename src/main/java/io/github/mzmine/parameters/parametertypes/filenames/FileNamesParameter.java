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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.controlsfx.property.editor.PropertyEditor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

import io.github.mzmine.parameters.Parameter;
import javafx.stage.FileChooser;

/**
 * This parameter stores filenames
 */
public class FileNamesParameter implements Parameter<List<File>> {

    private static final String fileNameElement = "filename";
    private static final String lastOpenPathElement = "lastdirectory";

    private List<File> value;

    private final @Nonnull String name, description;
    private final List<FileChooser.ExtensionFilter> extensions;
    private File lastOpenPath;

    public FileNamesParameter(@Nonnull String name, @Nonnull String description,
            List<FileChooser.ExtensionFilter> extensions) {
        this.name = name;
        this.description = description;
        this.extensions = ImmutableList.copyOf(extensions);
    }

    @Override
    public @Nonnull String getName() {
        return name;
    }

    @Override
    public @Nonnull String getDescription() {
        return description;
    }

    @Override
    public Class<?> getType() {
        return List.class;
    }

    @Override
    public List<File> getValue() {
        return value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setValue(@Nullable Object newValue) {
        this.value = (List<File>) newValue;
    }

    @Override
    public @Nonnull FileNamesParameter clone() {
        FileNamesParameter copy = new FileNamesParameter(name, description,
                extensions);
        if (value != null)
            copy.setValue(ImmutableList.copyOf(value));
        return copy;
    }

    @Override
    public void loadValueFromXML(@Nonnull Element xmlElement) {
        NodeList list = xmlElement.getElementsByTagName(fileNameElement);
        this.value = new ArrayList<>();
        for (int i = 0; i < list.getLength(); i++) {
            Element nextElement = (Element) list.item(i);
            String textValue = nextElement.getTextContent();
            if (!Strings.isNullOrEmpty(textValue)) {
                File newFile = new File(textValue);
                value.add(newFile);
            }
        }
        list = xmlElement.getElementsByTagName(lastOpenPathElement);
        for (int i = 0; i < list.getLength(); i++) {
            Element nextElement = (Element) list.item(i);
            String textValue = nextElement.getTextContent();
            if (!Strings.isNullOrEmpty(textValue)) {
                lastOpenPath = new File(textValue);
            }
        }
    }

    @Override
    public void saveValueToXML(@Nonnull Element xmlElement) {
        final Document parentDocument = xmlElement.getOwnerDocument();
        if (value != null) {
            for (File f : value) {
                Element newElement = parentDocument
                        .createElement(fileNameElement);
                newElement.setTextContent(f.getPath());
                xmlElement.appendChild(newElement);
            }
        }
        if (lastOpenPath != null) {
            Element newElement = parentDocument
                    .createElement(lastOpenPathElement);
            newElement.setTextContent(lastOpenPath.getPath());
            xmlElement.appendChild(newElement);
        }

    }

    @Override
    public Optional<Class<? extends PropertyEditor<?>>> getPropertyEditorClass() {
        return Optional.of(FileNamesEditor.class);
    }

    public List<FileChooser.ExtensionFilter> getExtensions() {
        return extensions;
    }

    public File getLastOpenPath() {
        return lastOpenPath;
    }

    public void setLastOpenPath(File lastOpenPath) {
        this.lastOpenPath = lastOpenPath;
    }

}
