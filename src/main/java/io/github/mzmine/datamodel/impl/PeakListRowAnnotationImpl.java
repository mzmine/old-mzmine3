/*
 * Copyright 2006-2015 The MZmine 3 Development Team
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

import java.net.URL;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecularFormula;

import io.github.mzmine.datamodel.PeakListRowAnnotation;

/**
 * Simple PeakIdentity implementation;
 */
public class PeakListRowAnnotationImpl implements PeakListRowAnnotation {

    @Override
    public IAtomContainer getChemicalStructure() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setChemicalStructure(IAtomContainer structure) {
        // TODO Auto-generated method stub

    }

    @Override
    public IMolecularFormula getFormula() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setFormula(IMolecularFormula formula) {
        // TODO Auto-generated method stub

    }

    @Override
    public String getDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setDescription(String description) {
        // TODO Auto-generated method stub

    }

    @Override
    public String getIdentificationMethod() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setIdentificationMethod(String idMethod) {
        // TODO Auto-generated method stub

    }

    @Override
    public String getDataBaseId() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setDataBaseId(String dbId) {
        // TODO Auto-generated method stub

    }

    @Override
    public URL getAccessionURL() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setAccessionURL(URL dbURL) {
        // TODO Auto-generated method stub

    }

}
