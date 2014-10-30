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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

/**
 * This class represents the chromatography information of each MS scan or
 * detected feature (peak). This class is immutable to allow passing it by
 * reference.
 */
@Immutable
public interface ChromatographyData {

    /**
     * @return Retention time in minutes
     */
    @Nonnull
    Double getRetentionTime();

    /**
     * @return Secondary retention time in minutes (for two-dimensional
     *         separations such as GCxGC-MS).
     */
    @Nullable
    Double getSecondaryRetentionTime();

    /**
     * @return Drift time in ms, for ion mobility experiments.
     */
    @Nullable
    Double getIonDriftTime();

}
