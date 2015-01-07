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

import net.sf.mzmine.datamodel.IsotopePattern;
import net.sf.mzmine.datamodel.IsotopePatternType;
import net.sf.mzmine.datamodel.MassSpectrumType;

import org.openscience.cdk.interfaces.IMolecularFormula;

/**
 * Simple implementation of IsotopePattern interface
 */
public class IsotopePatternImpl extends SpectrumImpl implements IsotopePattern {

    private @Nonnull IsotopePatternType status = IsotopePatternType.UNKNOWN;
    private @Nonnull String description = "";

    IsotopePatternImpl(@Nonnull DataPointStoreImpl dataPointStore) {
	super(dataPointStore);
    }

    @Override
    public @Nonnull String getDescription() {
	return description;
    }

    @Override
    public String toString() {
	return "Isotope pattern: " + description;
    }

    @Override
    public void setDescription(@Nonnull String description) {
	this.description = description;
    }

    @Override
    public MassSpectrumType getSpectrumType() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public IsotopePatternType getType() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public IMolecularFormula getChemicalFormula() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public void setChemicalFormula(IMolecularFormula formula) {
	// TODO Auto-generated method stub

    }

}