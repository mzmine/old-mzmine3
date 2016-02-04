/*
 * Copyright 2006-2015 The MZmine 3 Development Team
 * 
 * This file is part of MZmine 3.
 * 
 * MZmine 3 is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * MZmine 3 is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * MZmine 3; if not, write to the Free Software Foundation, Inc., 51 Franklin
 * St, Fifth Floor, Boston, MA 02110-1301 USA
 */

package io.github.mzmine.util;

import javafx.scene.Node;
import javafx.scene.paint.Color;

/**
 * JavaFX related utilities
 */
public class JavaFXUtil {

    /**
     * Convenience method for searching above comp in the component hierarchy
     * and returns the first object of class c it finds.
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <Type> Type getAncestorOfClass(Class<Type> c, Node node) {

        while (node != null) {
            if (c.isInstance(node)) {
                return (Type) node;
            }
            node = node.getParent();
        }

        return null;
    }

    public static java.awt.Color convertColorToAWT(Color col) {
        int r = (int) (col.getRed() * 255);
        int g = (int) (col.getGreen() * 255);
        int b = (int) (col.getBlue() * 255);
        return new java.awt.Color(r, g, b);
    }

    public static Color convertColorToJavaFX(java.awt.Color col) {
        return Color.rgb(col.getRed(), col.getGreen(), col.getBlue());
    }

}
