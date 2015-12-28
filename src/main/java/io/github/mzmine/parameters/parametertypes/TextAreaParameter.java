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

package io.github.mzmine.parameters.parametertypes;

import java.io.File;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

import io.github.mzmine.parameters.ParameterValidator;
import javafx.stage.FileChooser;

/**
 * This parameter stores filenames
 */
public class TextAreaParameter extends AbstractParameter<String> {

    private static final String textAreaElement = "textarea";
    private static final String lastOpenPathElement = "lastdirectory";

    private final @Nonnull List<FileChooser.ExtensionFilter> extensions;
    private File lastOpenPath;

    @SuppressWarnings("null")
    public TextAreaParameter(@Nonnull String name, @Nonnull String description,
            @Nonnull String category,
            @Nullable ParameterValidator<String> validator,
            @Nonnull List<FileChooser.ExtensionFilter> extensions) {
        super(name, description, category, TextAreaEditor.class, validator);
        this.extensions = ImmutableList.copyOf(extensions);
    }

    @Override
    public @Nonnull TextAreaParameter clone() {
        TextAreaParameter copy = new TextAreaParameter(getName(),
                getDescription(), getCategory(), getValidator(), extensions);
        String value = getValue();
        if (value != null)
            copy.setValue(value);
        return copy;
    }

    @Override
    public void loadValueFromXML(@Nonnull Element xmlElement) {
        NodeList list = xmlElement.getElementsByTagName(textAreaElement);
        String value = "";
        for (int i = 0; i < list.getLength(); i++) {
            Element nextElement = (Element) list.item(i);
            String textValue = nextElement.getTextContent();
            if (!Strings.isNullOrEmpty(textValue)) {
                value += textValue;
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
        String value = getValue();
        if (value != null) {
            Element newElement = parentDocument.createElement(textAreaElement);
            newElement.setTextContent(value);
            xmlElement.appendChild(newElement);
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
