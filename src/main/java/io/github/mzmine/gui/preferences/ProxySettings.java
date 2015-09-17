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

import io.github.mzmine.parameters.ParameterSet;
import io.github.mzmine.parameters.ParameterValidator;
import io.github.mzmine.parameters.parametertypes.StringParameter;

/**
 * Proxy server settings
 */
public class ProxySettings extends ParameterSet {

    public static final StringParameter proxyAddress = new StringParameter(
            "Proxy address", "Internet address of a proxy server", "Proxy",
            ParameterValidator.createNonEmptyValidator());

    public static final StringParameter proxyPort = new StringParameter(
            "Proxy port", "TCP port of proxy server", "Proxy",
            ParameterValidator.createNonEmptyValidator());

    public ProxySettings() {
        super(proxyAddress, proxyPort);
    }

}
