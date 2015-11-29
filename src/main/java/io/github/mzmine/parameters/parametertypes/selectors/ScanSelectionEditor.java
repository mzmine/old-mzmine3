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
 * MZmine 2; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

package io.github.mzmine.parameters.parametertypes.selectors;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Strings;
import com.google.common.collect.Range;

import io.github.msdk.datamodel.msspectra.MsSpectrumType;
import io.github.msdk.datamodel.rawdata.PolarityType;
import io.github.mzmine.main.MZmineCore;
import io.github.mzmine.parameters.Parameter;
import io.github.mzmine.parameters.ParameterEditor;
import io.github.mzmine.parameters.ParameterSet;
import io.github.mzmine.parameters.parametertypes.ComboParameter;
import io.github.mzmine.parameters.parametertypes.IntegerParameter;
import io.github.mzmine.parameters.parametertypes.StringParameter;
import io.github.mzmine.parameters.parametertypes.ranges.DoubleRangeParameter;
import io.github.mzmine.parameters.parametertypes.ranges.IntegerRangeParameter;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Control;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class ScanSelectionEditor extends HBox
        implements ParameterEditor<ScanSelection> {

    private final Button setButton, clearButton;

    private final Text restrictionsList;

    private Range<Integer> scanNumberRange;
    private Range<Double> scanRTRange;
    private Integer msLevel;
    private PolarityType polarity;
    private MsSpectrumType spectrumType;
    private String scanDefinition;

    public ScanSelectionEditor() {

        restrictionsList = new Text();

        updateRestrictionList();

        setButton = new Button("Set filters");
        setButton.setOnAction(e -> {

            final IntegerRangeParameter scanNumParameter = new IntegerRangeParameter(
                    "Scan number", "Range of included scan numbers", null,
                    scanNumberRange);
            final DoubleRangeParameter rtParameter = new DoubleRangeParameter(
                    "Retention time", "Retention time range", null);
            if (scanRTRange != null)
                rtParameter.setValue(scanRTRange);
            final IntegerParameter msLevelParameter = new IntegerParameter(
                    "MS level", "MS level", null, msLevel);
            final StringParameter scanDefinitionParameter = new StringParameter(
                    "Scan definition",
                    "Include only scans that match this scan definition. You can use wild cards, e.g. *FTMS*",
                    null, scanDefinition);
            final List<String> polarityTypes = Arrays
                    .asList(new String[] { "Any", "+", "-" });
            final ComboParameter<String> polarityParameter = new ComboParameter<>(
                    "Polarity", "Include only scans of this polarity", null,
                    polarityTypes);
            if ((polarity == PolarityType.POSITIVE)
                    || (polarity == PolarityType.NEGATIVE))
                polarityParameter.setValue(polarity.toString());
            final List<String> spectraTypes = Arrays.asList(new String[] {
                    "Any", "Centroided", "Profile", "Thresholded" });
            final ComboParameter<String> spectrumTypeParameter = new ComboParameter<>(
                    "Spectrum type", "Include only spectra of this type", null,
                    spectraTypes);
            if (spectrumType != null) {
                switch (spectrumType) {
                case CENTROIDED:
                    spectrumTypeParameter.setValue(spectraTypes.get(1));
                    break;
                case PROFILE:
                    spectrumTypeParameter.setValue(spectraTypes.get(2));
                    break;
                case THRESHOLDED:
                    spectrumTypeParameter.setValue(spectraTypes.get(3));
                    break;
                }
            }

            ParameterSet paramSet = new ParameterSet(
                    new Parameter[] { scanNumParameter, rtParameter,
                            msLevelParameter, scanDefinitionParameter,
                            polarityParameter, spectrumTypeParameter });
            ButtonType exitCode = paramSet.showSetupDialog(null);
            if (exitCode == ButtonType.OK) {
                scanNumberRange = paramSet.getParameter(scanNumParameter)
                        .getValue();
                scanRTRange = paramSet.getParameter(rtParameter).getValue();
                msLevel = paramSet.getParameter(msLevelParameter).getValue();
                scanDefinition = paramSet.getParameter(scanDefinitionParameter)
                        .getValue();
                final int selectedPolarityIndex = Arrays.asList(polarityTypes)
                        .indexOf(paramSet.getParameter(polarityParameter)
                                .getValue());
                switch (selectedPolarityIndex) {
                case 1:
                    polarity = PolarityType.POSITIVE;
                    break;
                case 2:
                    polarity = PolarityType.NEGATIVE;
                    break;
                default:
                    polarity = null;
                    break;
                }
                final int selectedSpectraTypeIndex = Arrays.asList(spectraTypes)
                        .indexOf(paramSet.getParameter(spectrumTypeParameter)
                                .getValue());
                switch (selectedSpectraTypeIndex) {
                case 1:
                    spectrumType = MsSpectrumType.CENTROIDED;
                    break;
                case 2:
                    spectrumType = MsSpectrumType.PROFILE;
                    break;
                case 3:
                    spectrumType = MsSpectrumType.THRESHOLDED;
                    break;
                default:
                    spectrumType = null;
                    break;
                }

            }
            updateRestrictionList();

        });

        clearButton = new Button("Clear filters");
        clearButton.setOnAction(e -> {
            scanNumberRange = null;
            scanRTRange = null;
            polarity = null;
            spectrumType = null;
            msLevel = null;
            scanDefinition = null;
            updateRestrictionList();

        });

        getChildren().addAll(restrictionsList, setButton, clearButton);

    }

    @Override
    public void setValue(ScanSelection newValue) {
        scanNumberRange = newValue.getScanNumberRange();
        scanRTRange = newValue.getScanRTRange();
        polarity = newValue.getPolarity();
        spectrumType = newValue.getSpectrumType();
        msLevel = newValue.getMsLevel();
        scanDefinition = newValue.getScanDefinition();

        updateRestrictionList();
    }

    @Override
    public ScanSelection getValue() {
        return new ScanSelection(scanNumberRange, scanRTRange, polarity,
                spectrumType, msLevel, scanDefinition);
    }

    private void updateRestrictionList() {

        if ((scanNumberRange == null) && (scanRTRange == null)
                && (polarity == null) && (spectrumType == null)
                && (msLevel == null) && Strings.isNullOrEmpty(scanDefinition)) {
            restrictionsList.setText("All");
            return;
        }

        StringBuilder newText = new StringBuilder("<html>");
        if (scanNumberRange != null) {
            newText.append("Scan number: " + scanNumberRange.lowerEndpoint()
                    + " - " + scanNumberRange.upperEndpoint() + "<br>");
        }
        if (scanRTRange != null) {
            NumberFormat rtFormat = MZmineCore.getConfiguration().getRTFormat();
            newText.append("Retention time: "
                    + rtFormat.format(scanRTRange.lowerEndpoint()) + " - "
                    + rtFormat.format(scanRTRange.upperEndpoint())
                    + " min.<br>");
        }
        if (msLevel != null) {
            newText.append("MS level: " + msLevel + "<br>");
        }
        if (!Strings.isNullOrEmpty(scanDefinition)) {
            newText.append("Scan definition: " + scanDefinition + "<br>");
        }
        if (polarity != null) {
            newText.append("Polarity: " + polarity.toString() + "<br>");
        }
        if (spectrumType != null) {
            newText.append(
                    "Spectrum type: " + spectrumType.toString().toLowerCase());
        }

        restrictionsList.setText(newText.toString());
    }

    @Override
    public Node getEditor() {
        return this;
    }

    @Override
    @Nullable
    public Control getMainControl() {
        return null;
    }
}
