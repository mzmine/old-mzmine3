/*
 * Copyright 2006-2015 The MZmine 3 Development Team
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

package io.github.mzmine.gui.preferences;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

@Immutable
public class NumOfThreadsValue {

  private final @Nonnull Boolean isAutomatic;
  private final @Nonnull Integer manualValue;

  public @Nonnull Boolean isAutomatic() {
    return isAutomatic;
  }

  public @Nonnull Integer getManualValue() {
    return manualValue;
  }

  public NumOfThreadsValue(@Nonnull Boolean isAutomatic, @Nonnull Integer manualValue) {
    this.isAutomatic = isAutomatic;
    this.manualValue = manualValue;
  }

  public int getNumberOfThreads() {
    if (isAutomatic || (manualValue == null))
      return Runtime.getRuntime().availableProcessors();
    else
      return manualValue;
  }

}
