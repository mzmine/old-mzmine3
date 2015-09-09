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

package io.github.msdk.io.rawdataimport.nativeformats;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Text processing utilities
 */
class TextUtils {

    /**
     * Reads a line of text from a given input stream or null if the end of the
     * stream is reached.
     */
    static String readLineFromStream(InputStream in) throws IOException {
        byte buf[] = new byte[1024];
        int pos = 0;
        while (true) {
            int ch = in.read();
            if ((ch == '\n') || (ch < 0))
                break;
            buf[pos++] = (byte) ch;
            if (pos == buf.length)
                buf = Arrays.copyOf(buf, pos * 2);
        }
        if (pos == 0)
            return null;

        return new String(Arrays.copyOf(buf, pos), "UTF-8");
    }

}
