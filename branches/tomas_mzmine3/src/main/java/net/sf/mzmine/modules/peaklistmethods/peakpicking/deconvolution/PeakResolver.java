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
 * MZmine 2; if not, write to the Free Software Foundation, Inc., 51 Franklin
 * St, Fifth Floor, Boston, MA 02110-1301 USA
 */

package net.sf.mzmine.modules.peaklistmethods.peakpicking.deconvolution;

import net.sf.mzmine.datamodel.Feature;
import net.sf.mzmine.modules.MZmineModule;
import net.sf.mzmine.parameters.ParameterSet;

public interface PeakResolver extends MZmineModule {

    /**
     * Resolve a peaks found within given chromatogram. For easy use, three
     * arrays (scanNumbers, retentionTimes and intensities) are provided,
     * although the contents of these arrays can also be obtained from the
     * chromatogram itself. The size of these arrays must be same, and must be
     * equal to the number of scans covered by given chromatogram.
     */
    public Feature[] resolvePeaks(Feature chromatogram,
	    int scanNumbers[], double retentionTimes[], double intensities[],
	    ParameterSet parameters);

}
