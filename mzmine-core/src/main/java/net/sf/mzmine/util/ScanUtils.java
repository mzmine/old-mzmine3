/*
 * Copyright 2006-2014 The MZmine 2 Development Team
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

package net.sf.mzmine.util;

import java.text.Format;

import net.sf.mzmine.datamodel.MsScan;
import net.sf.mzmine.main.MZmineCore;

/**
 * Scan related utilities
 */
public class ScanUtils {

    /**
     * Common utility method to be used as Scan.toString() method in various
     * Scan implementations
     * 
     * @param scan
     *            Scan to be converted to String
     * @return String representation of the scan
     */
    public static String scanToString(MsScan scan) {
	StringBuffer buf = new StringBuffer();
	Format rtFormat = MZmineCore.getConfiguration().getRTFormat();
	Format mzFormat = MZmineCore.getConfiguration().getMZFormat();
	buf.append("#");
	buf.append(scan.getScanNumber());
	buf.append(" @");
//	buf.append(rtFormat.format(scan.getRetentionTime()));
	buf.append(" MS");
	buf.append(scan.getMSLevel());

	return buf.toString();
    }

    
}
