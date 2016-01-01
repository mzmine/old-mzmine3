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

package io.github.mzmine.parameters.parametertypes.filenames;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

import io.github.mzmine.parameters.ParameterValidator;
import io.github.mzmine.parameters.parametertypes.AbstractParameter;
import javafx.stage.FileChooser;

/**
 * This parameter stores filenames
 */
public class FileNamesParameter extends AbstractParameter<List<File>> {

    private static final String fileNameElement = "filename";
    private static final String lastOpenPathElement = "lastdirectory";

    private final @Nonnull List<FileChooser.ExtensionFilter> extensions;
    private File lastOpenPath;

    @SuppressWarnings("null")
    public FileNamesParameter(@Nonnull String name, @Nonnull String description,
            @Nonnull String category,
            @Nullable ParameterValidator<List<File>> validator,
            @Nonnull List<FileChooser.ExtensionFilter> extensions) {
        super(name, description, category, FileNamesEditor.class, validator);
        this.extensions = ImmutableList.copyOf(extensions);
    }

    @Override
    public @Nonnull FileNamesParameter clone() {
        FileNamesParameter copy = new FileNamesParameter(getName(),
                getDescription(), getCategory(), getValidator(), extensions);
        List<File> value = getValue();
        if (value != null)
            copy.setValue(ImmutableList.copyOf(value));
        return copy;
    }

    @Override
    public void loadValueFromXML(@Nonnull Element xmlElement) {
        NodeList list = xmlElement.getElementsByTagName(fileNameElement);
        List<File> value = new ArrayList<>();
        for (int i = 0; i < list.getLength(); i++) {
            Element nextElement = (Element) list.item(i);
            String textValue = nextElement.getTextContent();
            if (!Strings.isNullOrEmpty(textValue)) {
                File newFile = new File(textValue);
                value.add(newFile);
            }
        }
        setValue(value);
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
        List<File> value = getValue();
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
