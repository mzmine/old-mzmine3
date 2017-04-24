/*
 * Copyright 2006-2016 The MZmine 3 Development Team
 * 
 * This file is part of MZmine 3.
 * 
 * MZmine 3 is free software; you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * MZmine 3 is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with MZmine 3; if not,
 * write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301
 * USA
 */

package io.github.mzmine.util;

import java.io.File;
import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.io.Files;

/**
 * File name utilities
 */
public class FileNameUtil {

  public static @Nonnull String findCommonPrefix(@Nonnull List<File> fileNames) {
    if (fileNames.size() < 2)
      return "";

    String firstName = fileNames.get(0).getName();

    for (int prefixLen = 0; prefixLen < firstName.length(); prefixLen++) {
      char c = firstName.charAt(prefixLen);
      for (int i = 1; i < fileNames.size(); i++) {
        String ithName = fileNames.get(i).getName();
        if (prefixLen >= ithName.length() || ithName.charAt(prefixLen) != c) {
          // Mismatch found
          return ithName.substring(0, prefixLen);
        }
      }
    }
    return firstName;
  }

  public static @Nonnull String findCommonSuffix(@Nonnull List<File> fileNames) {

    if (fileNames.isEmpty())
      return "";

    if (fileNames.size() == 1) {
      // Return file extension
      String ext = Files.getFileExtension(fileNames.get(0).getAbsolutePath());
      return "." + ext;
    }

    String firstName = fileNames.get(0).getName();

    for (int suffixLen = 0; suffixLen < firstName.length(); suffixLen++) {
      char c = firstName.charAt(firstName.length() - 1 - suffixLen);
      for (int i = 1; i < fileNames.size(); i++) {
        String ithName = fileNames.get(i).getName();
        if (suffixLen >= ithName.length()
            || ithName.charAt(ithName.length() - 1 - suffixLen) != c) {
          // Mismatch found
          return ithName.substring(ithName.length() - suffixLen);
        }
      }
    }
    return firstName;
  }
}
