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
 * MZmine 3; if not, write to the Free Software Foundation, Inc., 51 Franklin
 * St, Fifth Floor, Boston, MA 02110-1301 USA
 */

package io.github.mzmine.parameters.parametertypes.selectors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

import io.github.msdk.datamodel.rawdata.RawDataFile;
import io.github.mzmine.gui.MZmineGUI;
import io.github.mzmine.main.MZmineCore;
import io.github.mzmine.util.TextUtils;

@Immutable
public class RawDataFilesSelection implements Cloneable {

    private final RawDataFilesSelectionType selectionType;
    private final List<RawDataFile> specificFiles;
    private final String namePattern;

    public RawDataFilesSelection(List<RawDataFile> specificFiles) {
        this(RawDataFilesSelectionType.SPECIFIC_FILES, specificFiles, null);
    }

    public RawDataFilesSelection(RawDataFilesSelectionType selectionType,
            List<RawDataFile> specificFiles, String namePattern) {

        Preconditions.checkNotNull(selectionType);
        this.selectionType = selectionType;

        if (specificFiles != null)
            this.specificFiles = ImmutableList.copyOf(specificFiles);
        else
            this.specificFiles = null;

        this.namePattern = namePattern;
    }

    public List<RawDataFile> getMatchingRawDataFiles() {

        switch (selectionType) {

        case GUI_SELECTED_FILES:
            return MZmineGUI.getSelectedRawDataFiles();
        case ALL_FILES:
            return MZmineCore.getCurrentProject().getRawDataFiles();
        case SPECIFIC_FILES:
            if (specificFiles == null)
                return Collections.emptyList();
            else
                return specificFiles;
        case NAME_PATTERN:
            if (Strings.isNullOrEmpty(namePattern))
                return Collections.emptyList();
            ArrayList<RawDataFile> matchingDataFiles = new ArrayList<RawDataFile>();
            List<RawDataFile> allDataFiles = MZmineCore.getCurrentProject()
                    .getRawDataFiles();

            fileCheck: for (RawDataFile file : allDataFiles) {

                final String fileName = file.getName();

                final String regex = TextUtils
                        .createRegexFromWildcards(namePattern);

                if (fileName.matches(regex)) {
                    if (matchingDataFiles.contains(file))
                        continue;
                    matchingDataFiles.add(file);
                    continue fileCheck;
                }
            }
            return matchingDataFiles;
        case BATCH_LAST_FILES:
            return Collections.emptyList();
        }

        throw new IllegalStateException("This code should be unreachable");

    }

    public RawDataFilesSelectionType getSelectionType() {
        return selectionType;
    }

    public List<RawDataFile> getSpecificFiles() {
        return specificFiles;
    }

    public String getNamePattern() {
        return namePattern;
    }

    public String toString() {
        StringBuilder str = new StringBuilder();
        List<RawDataFile> files = getMatchingRawDataFiles();
        for (int i = 0; i < files.size(); i++) {
            if (i > 0)
                str.append("\n");
            str.append(files.get(i).getName());
        }
        return str.toString();
    }

}
