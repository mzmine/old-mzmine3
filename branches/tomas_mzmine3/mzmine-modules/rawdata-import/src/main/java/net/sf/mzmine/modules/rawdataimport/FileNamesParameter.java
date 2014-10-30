package net.sf.mzmine.modules.rawdataimport;

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

import java.io.File;
import java.util.List;

import org.controlsfx.control.PropertySheet.Item;

/**
 * This parameter stores filenames for raw data importer
 * 
 */
public class FileNamesParameter implements Item {

    private List<File> value;

    @Override
    public String getName() {
	return "Raw data file names";
    }

    @Override
    public String getCategory() {
	return "File names category";
    }

    @Override
    public String getDescription() {
	return "Select files to import";
    }

    @Override
	public Class<?> getType() {
	    return List.class;
	}

    @Override
    public void setValue(Object value) {
	this.value = (List<File>) value;
    }

    @Override
    public Object getValue() {
	return value;
    }

}
