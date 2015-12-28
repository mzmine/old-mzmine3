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

package io.github.mzmine.gui.preferences;

import java.text.DecimalFormat;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import io.github.mzmine.main.MZmineCore;
import io.github.mzmine.modules.MZmineModule;
import io.github.mzmine.parameters.ParameterSet;
import io.github.mzmine.parameters.parametertypes.BooleanParameter;
import io.github.mzmine.parameters.parametertypes.OptionalModuleParameter;
import io.github.mzmine.parameters.parametertypes.filenames.FileNameParameter;
import javafx.scene.control.ButtonType;

public class MZminePreferences extends ParameterSet {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final NumberFormatParameter mzFormat = new NumberFormatParameter(
            "m/z value format", "Format of m/z values", "Number formatting",
            false, new DecimalFormat("0.0000"));

    public static final NumberFormatParameter rtFormat = new NumberFormatParameter(
            "Retention time format", "Format of retention time values",
            "Number formatting", false, new DecimalFormat("0.0"));

    public static final NumberFormatParameter intensityFormat = new NumberFormatParameter(
            "Intensity format", "Format of intensity values",
            "Number formatting", true, new DecimalFormat("0.0E0"));

    public static final NumOfThreadsParameter numOfThreads = new NumOfThreadsParameter();

    public static final OptionalModuleParameter proxySettings = new OptionalModuleParameter(
            "Use proxy", "Use proxy for internet connection?", "Proxy",
            new ProxySettings());

    public static final FileNameParameter rExecPath = new FileNameParameter(
            "R executable path",
            "Full R executable file path (If left blank, MZmine will try to find out automatically). On Windows, this should point to your R.exe file.",
            "R support", (value, messages) -> {
                if (value == null)
                    return true;
                if (!value.exists()) {
                    messages.add("File does not exist");
                    return false;
                }
                if (!value.canExecute()) {
                    messages.add("File is not executable");
                    return false;
                }
                return true;

            } , FileNameParameter.Type.OPEN);

    public static final BooleanParameter sendStatistics = new BooleanParameter(
            "Send statistics",
            "Allow MZmine to send anonymous statistics on the usage of its modules?",
            "Statistics", true);

    // public static final WindowSettingsParameter windowSetttings = new
    // WindowSettingsParameter();

    public MZminePreferences() {
        super(mzFormat, rtFormat, intensityFormat, numOfThreads, proxySettings,
                rExecPath, sendStatistics);
    }

    @Override
    public ButtonType showSetupDialog(@Nullable MZmineModule module) {

        ButtonType retVal = super.showSetupDialog(null);
        if (retVal == ButtonType.OK) {

            // Update system settings
            updateSystemSettings();

            // Repaint windows to update number formats
            // TODO: MZmineCore..getDesktop().getMainWindow().repaint();
        }

        return retVal;
    }

    public void loadValuesFromXML(Element xmlElement) {
        super.loadValuesFromXML(xmlElement);
        updateSystemSettings();
    }

    private void updateSystemSettings() {

        // Update system proxy settings
        Boolean proxyEnabled = getParameter(proxySettings).getValue();
        if ((proxyEnabled != null) && (proxyEnabled)) {
            ParameterSet proxyParams = getParameter(proxySettings)
                    .getEmbeddedParameters();
            String address = proxyParams
                    .getParameter(ProxySettings.proxyAddress).getValue();
            String port = proxyParams.getParameter(ProxySettings.proxyPort)
                    .getValue();
            System.setProperty("http.proxySet", "true");
            System.setProperty("http.proxyHost", address);
            System.setProperty("http.proxyPort", port);
        } else {
            System.clearProperty("http.proxySet");
            System.clearProperty("http.proxyHost");
            System.clearProperty("http.proxyPort");
        }

        NumOfThreadsValue numOfThreadsValue = getParameter(numOfThreads)
                .getValue();
        if (numOfThreadsValue != null) {
            int threadPoolSize = numOfThreadsValue.getNumberOfThreads();
            int currentThreadPoolSize = MZmineCore.getTaskExecutor()
                    .getCorePoolSize();
            if (threadPoolSize != currentThreadPoolSize) {
                logger.debug(
                        "Setting the thread pool size to " + threadPoolSize);
                MZmineCore.getTaskExecutor().setCorePoolSize(threadPoolSize);
            }
        }

    }

}
