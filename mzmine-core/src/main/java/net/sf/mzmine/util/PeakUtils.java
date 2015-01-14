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

package net.sf.mzmine.util;

import java.text.Format;

import net.sf.mzmine.datamodel.Feature;
import net.sf.mzmine.main.MZmineCore;

/**
 * Utilities for peaks and peak lists
 * 
 */
public class PeakUtils {

    /**
     * Common utility method to be used as Peak.toString() method in various
     * Peak implementations
     * 
     * @param peak
     *            Peak to be converted to String
     * @return String representation of the peak
     */
    public static String peakToString(Feature peak) {
	StringBuffer buf = new StringBuffer();
	Format mzFormat = MZmineCore.getConfiguration().getMZFormat();
	Format timeFormat = MZmineCore.getConfiguration().getRTFormat();
	buf.append(mzFormat.format(peak.getMZ()));
	buf.append(" m/z @");
//	buf.append(timeFormat.format(peak.getRT()));
	//buf.append(" [" + peak.getDataFile().getName() + "]");
	return buf.toString();
    }
    
}
