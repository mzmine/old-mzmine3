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

package net.sf.mzmine.datamodel;

import javax.annotation.concurrent.Immutable;

/**
 * A single data point of a mass spectrum (a pair of m/z and intensity values).
 * DataPoints are immutable once created, to allow passing them by reference,
 * instead of cloning each data point instance when passing them as parameters.
 */
@Immutable
public interface DataPoint {

    /**
     * @return m/z value of this data point.
     */
    double getMz();

    /**
     * @return Intensity value of this data point.
     */
    double getIntensity();

}
