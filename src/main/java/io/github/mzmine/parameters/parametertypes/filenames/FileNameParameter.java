/*
 * Copyright 2006-2015 The MZmine 3 Development Team
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

package io.github.mzmine.parameters.parametertypes.filenames;

import java.io.File;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.controlsfx.property.editor.PropertyEditor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.google.common.base.Strings;

import io.github.mzmine.parameters.Parameter;
import io.github.mzmine.parameters.ParameterValidator;

public class FileNameParameter implements Parameter<File> {

    public enum Type {
        OPEN, SAVE
    }

    private static final String fileNameElement = "filename";
    private static final String lastOpenPathElement = "lastdirectory";

    private final @Nonnull String name, description;
    private final @Nonnull Type type;
    private File value;
    private File lastOpenPath;

    public FileNameParameter(@Nonnull String name, @Nonnull String description,
            @Nonnull Type type) {
        this(name, description, type, null);
    }

    public FileNameParameter(@Nonnull String name, @Nonnull String description,
            @Nonnull Type type, File defaultValue) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.value = defaultValue;
    }

    /**
     * @see net.sf.mzmine.data.Parameter#getName()
     */
    @Override
    public @Nonnull String getName() {
        return name;
    }

    /**
     * @see net.sf.mzmine.data.Parameter#getDescription()
     */
    @Override
    public @Nonnull String getDescription() {
        return description;
    }

    public Type getFileNameParameterType() {
        return type;
    }

    @Override
    public Class<?> getType() {
        return String.class;
    }

    public File getValue() {
        return value;
    }

    @Override
    public void setValue(@Nullable Object value) {
        this.value = (File) value;
    }

    @Override
    public @Nonnull FileNameParameter clone() {
        FileNameParameter copy = new FileNameParameter(name, description, type,
                value);
        return copy;
    }

    @Override
    public void loadValueFromXML(@Nonnull Element xmlElement) {
        NodeList list = xmlElement.getElementsByTagName(fileNameElement);
        for (int i = 0; i < list.getLength(); i++) {
            Element nextElement = (Element) list.item(i);
            String textValue = nextElement.getTextContent();
            if (!Strings.isNullOrEmpty(textValue))
                value = new File(textValue);
        }
        list = xmlElement.getElementsByTagName(lastOpenPathElement);
        for (int i = 0; i < list.getLength(); i++) {
            Element nextElement = (Element) list.item(i);
            lastOpenPath = new File(nextElement.getTextContent());
        }
    }

    @Override
    public void saveValueToXML(@Nonnull Element xmlElement) {
        final Document parentDocument = xmlElement.getOwnerDocument();
        if (value != null) {
            Element newElement = parentDocument.createElement(fileNameElement);
            newElement.setTextContent(value.getPath());
            xmlElement.appendChild(newElement);
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
        return Optional.of(FileNameEditor.class);
    }

    public File getLastOpenPath() {
        return lastOpenPath;
    }

    public void setLastOpenPath(File lastOpenPath) {
        this.lastOpenPath = lastOpenPath;
    }

    public ParameterValidator<File> getValidator() {
        return (val, msg) -> {
            return (val != null) && (val.exists()) && (val.canExecute());
        };
    };

}
