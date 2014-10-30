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

package net.sf.mzmine.datamodel.impl;

import javax.annotation.Nonnull;

import net.sf.mzmine.datamodel.MassList;
import net.sf.mzmine.datamodel.MassSpectrumType;
import net.sf.mzmine.datamodel.MsScan;

/**
 * This class represent detected masses (ions) in one mass spectrum
 */
public class MassListImpl extends SpectrumImpl implements MassList {

    private @Nonnull String name = "Mass list";
    private @Nonnull MsScan scan;

    public MassListImpl(@Nonnull DataPointStoreImpl dataFile, @Nonnull MsScan scan) {
	super(dataFile);
	this.scan = scan;
    }

    @Override
    public @Nonnull String getName() {
	return name;
    }

    @Override
    public void setName(@Nonnull String name) {
	this.name = name;
    }

    @Override
    public @Nonnull MsScan getScan() {
	return scan;
    }

    @Override
    public void setScan(@Nonnull MsScan scan) {
	this.scan = scan;
    }

    @Override
    public String toString() {
	return name;
    }

    @Override
    public MassSpectrumType getMassSpectrumType() {
	// TODO Auto-generated method stub
	return null;
    }

}
