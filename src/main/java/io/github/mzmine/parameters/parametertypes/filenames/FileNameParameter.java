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
import java.util.Collection;
import java.util.Optional;

import org.controlsfx.property.editor.PropertyEditor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.google.common.base.Strings;

import io.github.mzmine.parameters.Parameter;

public class FileNameParameter implements Parameter<File> {

    private static final String fileNameElement = "filename";
    private static final String lastOpenPathElement = "lastdirectory";

    private final String name, description, category;
    private File value;
    private File lastOpenPath;

    public FileNameParameter(String name, String description) {
        this(name, description, null, null);
    }

    public FileNameParameter(String name, String description, String category,
            File defaultValue) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.value = defaultValue;
    }

    /**
     * @see net.sf.mzmine.data.Parameter#getName()
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * @see net.sf.mzmine.data.Parameter#getDescription()
     */
    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getCategory() {
        return category;
    }

    @Override
    public Class<?> getType() {
        return String.class;
    }

    public File getValue() {
        return value;
    }

    @Override
    public void setValue(Object value) {
        this.value = (File) value;
    }

    @Override
    public FileNameParameter clone() {
        FileNameParameter copy = new FileNameParameter(name, description,
                category, value);
        return copy;
    }

    @Override
    public void loadValueFromXML(Element xmlElement) {
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
    public void saveValueToXML(Element xmlElement) {
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
    public boolean checkValue(Collection<String> errorMessages) {
        if (value == null) {
            errorMessages.add(name + " is not set properly");
            return false;
        }
        return true;
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

}
