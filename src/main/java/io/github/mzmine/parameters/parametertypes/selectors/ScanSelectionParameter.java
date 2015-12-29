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

import javax.annotation.Nonnull;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.google.common.collect.Range;

import io.github.msdk.datamodel.msspectra.MsSpectrumType;
import io.github.msdk.datamodel.rawdata.PolarityType;
import io.github.mzmine.parameters.parametertypes.AbstractParameter;

public class ScanSelectionParameter extends AbstractParameter<ScanSelection> {

    public ScanSelectionParameter() {
        this("Scans", "Select scans that should be included.", null, null);
    }

    public ScanSelectionParameter(ScanSelection defaultValue) {
        this("Scans", "Select scans that should be included.", null,
                defaultValue);
    }

    public ScanSelectionParameter(String name, String description,
            String category, ScanSelection defaultValue) {
        super(name, description, category, ScanSelectionEditor.class, null);
    }

    @Override
    public @Nonnull ScanSelectionParameter clone() {
        ScanSelectionParameter copy = new ScanSelectionParameter(getName(),
                getDescription(), getCategory(), getValue());
        return copy;
    }

    @Override
    public void loadValueFromXML(Element xmlElement) {

        Range<Integer> scanNumberRange = null;
        Range<Double> scanRTRange = null;
        PolarityType polarity = null;
        MsSpectrumType spectrumType = null;
        Integer msLevel = null;
        String scanDefinition = null;

        NodeList items = xmlElement.getElementsByTagName("scan_numbers");
        for (int i = 0; i < items.getLength(); i++) {
            if (items.item(i).getChildNodes().getLength() != 2)
                continue;
            String minText = items.item(i).getChildNodes().item(0)
                    .getTextContent();
            String maxText = items.item(i).getChildNodes().item(1)
                    .getTextContent();
            scanNumberRange = Range.closed(Integer.valueOf(minText),
                    Integer.valueOf(maxText));
        }

        items = xmlElement.getElementsByTagName("retention_time");
        for (int i = 0; i < items.getLength(); i++) {
            if (items.item(i).getChildNodes().getLength() != 2)
                continue;
            String minText = items.item(i).getChildNodes().item(0)
                    .getTextContent();
            String maxText = items.item(i).getChildNodes().item(1)
                    .getTextContent();
            scanRTRange = Range.closed(Double.valueOf(minText),
                    Double.valueOf(maxText));
        }

        items = xmlElement.getElementsByTagName("ms_level");
        for (int i = 0; i < items.getLength(); i++) {
            msLevel = Integer.valueOf(items.item(i).getTextContent());
        }

        items = xmlElement.getElementsByTagName("polarity");
        for (int i = 0; i < items.getLength(); i++) {
            try {
                polarity = PolarityType.valueOf(items.item(i).getTextContent());
            } catch (Exception e) {
                polarity = null;
            }
        }

        items = xmlElement.getElementsByTagName("spectrum_type");
        for (int i = 0; i < items.getLength(); i++) {
            spectrumType = MsSpectrumType
                    .valueOf(items.item(i).getTextContent());
        }

        items = xmlElement.getElementsByTagName("scan_definition");
        for (int i = 0; i < items.getLength(); i++) {
            scanDefinition = items.item(i).getTextContent();
        }

        ScanSelection newValue = new ScanSelection(scanNumberRange, scanRTRange,
                polarity, spectrumType, msLevel, scanDefinition);
        setValue(newValue);
    }

    @Override
    public void saveValueToXML(Element xmlElement) {
        ScanSelection value = getValue();
        if (value == null)
            return;
        Document parentDocument = xmlElement.getOwnerDocument();

        final Range<Integer> scanNumberRange = value.getScanNumberRange();
        final Range<Double> scanRetentionTimeRange = value.getScanRTRange();
        final PolarityType polarity = value.getPolarity();
        final MsSpectrumType spectrumType = value.getSpectrumType();
        final Integer msLevel = value.getMsLevel();
        final String scanDefinition = value.getScanDefinition();

        if (scanNumberRange != null) {
            Element scanNumElement = parentDocument
                    .createElement("scan_numbers");
            xmlElement.appendChild(scanNumElement);
            Element newElement = parentDocument.createElement("min");
            newElement.setTextContent(
                    String.valueOf(scanNumberRange.lowerEndpoint()));
            scanNumElement.appendChild(newElement);
            newElement = parentDocument.createElement("max");
            newElement.setTextContent(
                    String.valueOf(scanNumberRange.upperEndpoint()));
            scanNumElement.appendChild(newElement);
        }

        if (scanRetentionTimeRange != null) {
            Element scanRtElement = parentDocument
                    .createElement("retention_time");
            xmlElement.appendChild(scanRtElement);
            Element newElement = parentDocument.createElement("min");
            newElement.setTextContent(
                    String.valueOf(scanRetentionTimeRange.lowerEndpoint()));
            scanRtElement.appendChild(newElement);
            newElement = parentDocument.createElement("max");
            newElement.setTextContent(
                    String.valueOf(scanRetentionTimeRange.upperEndpoint()));
            scanRtElement.appendChild(newElement);
        }

        if (polarity != null) {
            Element newElement = parentDocument.createElement("polarity");
            newElement.setTextContent(polarity.toString());
            xmlElement.appendChild(newElement);
        }

        if (spectrumType != null) {
            Element newElement = parentDocument.createElement("spectrum_type");
            newElement.setTextContent(spectrumType.toString());
            xmlElement.appendChild(newElement);
        }

        if (msLevel != null) {
            Element newElement = parentDocument.createElement("ms_level");
            newElement.setTextContent(String.valueOf(msLevel));
            xmlElement.appendChild(newElement);
        }

        if (scanDefinition != null) {
            Element newElement = parentDocument
                    .createElement("scan_definition");
            newElement.setTextContent(scanDefinition);
            xmlElement.appendChild(newElement);
        }

    }

}
