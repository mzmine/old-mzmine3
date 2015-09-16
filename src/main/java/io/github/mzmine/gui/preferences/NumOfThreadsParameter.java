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

package io.github.mzmine.gui.preferences;

import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.controlsfx.property.editor.PropertyEditor;
import org.w3c.dom.Element;

import com.google.common.base.Strings;

import io.github.mzmine.main.MZmineCore;
import io.github.mzmine.parameters.Parameter;

public class NumOfThreadsParameter implements Parameter<NumOfThreadsValue> {

    private static final @Nonnull String name = "Number of parallel tasks";
    private static final @Nonnull String description = "Maximum number of tasks running simultaneously";

    // Provide a default value
    private NumOfThreadsValue value = new NumOfThreadsValue(true, 4);

    public NumOfThreadsParameter() {
        this.value = null;
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

    @Override
    public NumOfThreadsValue getValue() {
        return value;
    }

    @Override
    public void setValue(@Nullable Object value) {
        this.value = (NumOfThreadsValue) value;

        if (this.value != null) {
            // Update the thread pool executor
            int threadPoolSize = this.value.getNumberOfThreads();
            MZmineCore.getTaskExecutor().setCorePoolSize(threadPoolSize);
        }

    }

    @Override
    public @Nonnull NumOfThreadsParameter clone() {
        return this;
    }

    @Override
    public void loadValueFromXML(@Nonnull Element xmlElement) {
        String attrValue = xmlElement.getAttribute("isautomatic");
        boolean automatic = true;
        if (attrValue.length() > 0) {
            automatic = Boolean.valueOf(attrValue);
        }
        int manualValue = 4;
        String textContent = xmlElement.getTextContent();
        if (!Strings.isNullOrEmpty(textContent)) {
            manualValue = Integer.valueOf(textContent);
        }
        this.value = new NumOfThreadsValue(automatic, manualValue);
    }

    @Override
    public void saveValueToXML(@Nonnull Element xmlElement) {
        if (value == null)
            return;
        xmlElement.setAttribute("isautomatic",
                String.valueOf(value.isAutomatic()));
        xmlElement.setTextContent(value.toString());
    }

    @Override
    public Optional<Class<? extends PropertyEditor<?>>> getPropertyEditorClass() {
        return Optional.of(NumOfThreadsEditor.class);
    }

    @Override
    public Class<?> getType() {
        return Integer.class;
    }

}
