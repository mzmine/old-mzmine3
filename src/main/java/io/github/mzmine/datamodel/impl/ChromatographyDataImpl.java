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

package io.github.mzmine.datamodel.impl;

import io.github.mzmine.datamodel.ChromatographyData;

public class ChromatographyDataImpl implements ChromatographyData {

    private Double retentionTime, secondaryRetentionTime, ionDriftTime;

    /**
     * @return the retentionTime
     */
    @Override
    public Double getRetentionTime() {
	return retentionTime;
    }

    /**
     * @param retentionTime
     *            the retentionTime to set
     */
    @Override
    public void setRetentionTime(Double retentionTime) {
	this.retentionTime = retentionTime;
    }

    /**
     * @return the secondaryRetentionTime
     */
    @Override
    public Double getSecondaryRetentionTime() {
	return secondaryRetentionTime;
    }

    /**
     * @param secondaryRetentionTime
     *            the secondaryRetentionTime to set
     */
    @Override
    public void setSecondaryRetentionTime(Double secondaryRetentionTime) {
	this.secondaryRetentionTime = secondaryRetentionTime;
    }

    /**
     * @return the ionDriftTime
     */
    @Override
    public Double getIonDriftTime() {
	return ionDriftTime;
    }

    /**
     * @param ionDriftTime
     *            the ionDriftTime to set
     */
    @Override
    public void setIonDriftTime(Double ionDriftTime) {
	this.ionDriftTime = ionDriftTime;
    }

}
