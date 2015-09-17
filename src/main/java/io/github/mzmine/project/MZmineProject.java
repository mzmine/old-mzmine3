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

package io.github.mzmine.project;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import io.github.msdk.datamodel.featuretables.FeatureTable;
import io.github.msdk.datamodel.featuretables.Sample;
import io.github.msdk.datamodel.rawdata.RawDataFile;

/**
 * Simple implementation of the MZmineProject interface.
 */
public class MZmineProject {

    private @Nullable File projectFile;

    private final List<RawDataFile> rawDataFiles = new ArrayList<>();
    private final List<FeatureTable> featureTables = new ArrayList<>();

    @Nullable
    public File getProjectFile() {
        return projectFile;
    }

    public void setProjectFile(@Nullable File projectFile) {
        this.projectFile = projectFile;
    }

    @SuppressWarnings("null")
    @Nonnull
    public List<Sample> getSamples() {
        final ArrayList<Sample> allSamples = new ArrayList<>();
        synchronized (featureTables) {
            for (FeatureTable peakList : featureTables) {
                for (Sample s : peakList.getSamples()) {
                    if (!allSamples.contains(s))
                        allSamples.add(s);
                }
            }
        }
        return ImmutableList.copyOf(allSamples);
    }

    public void addFile(final RawDataFile rawDataFile) {
        synchronized (rawDataFiles) {
            rawDataFiles.add(rawDataFile);
        }
    }

    public void removeFile(final RawDataFile rawDataFile) {
        synchronized (rawDataFiles) {
            rawDataFiles.remove(rawDataFile);
        }
    }

    @SuppressWarnings("null")
    @Nonnull
    public List<RawDataFile> getRawDataFiles() {
        synchronized (rawDataFiles) {
            return ImmutableList.copyOf(rawDataFiles);
        }
    }

    public void addFeatureTable(final FeatureTable featureTable) {
        synchronized (featureTables) {
            featureTables.add(featureTable);
        }
    }

    public void removeFeatureTable(final FeatureTable featureTable) {
        synchronized (featureTables) {
            featureTables.remove(featureTable);
        }
    }

    @SuppressWarnings("null")
    @Nonnull
    public List<FeatureTable> getFeatureTables() {
        synchronized (featureTables) {
            return ImmutableList.copyOf(featureTables);
        }
    }

}