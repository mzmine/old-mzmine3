/* 
 * (C) Copyright 2015 by MSDK Development Team
 *
 * This software is dual-licensed under either
 *
 * (a) the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation
 *
 * or (per the licensee's choosing)
 *
 * (b) the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */

package io.github.msdk.datamodel.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Preconditions;

import io.github.msdk.datamodel.rawdata.FragmentationInfo;
import io.github.msdk.datamodel.rawdata.FragmentationType;

/**
 * Implementation of FragmentationInfo
 */
class SimpleFragmentationInfo implements FragmentationInfo {

    private @Nonnull FragmentationType fragmentationType = FragmentationType.UNKNOWN;
    private @Nullable Double activationEnergy;

    SimpleFragmentationInfo(@Nullable Double activationEnergy) {
        this.activationEnergy = activationEnergy;
    }

    SimpleFragmentationInfo(@Nullable Double activationEnergy,
            @Nonnull FragmentationType fragmentationType) {
        Preconditions.checkNotNull(fragmentationType);
        this.activationEnergy = activationEnergy;
        this.fragmentationType = fragmentationType;
    }

    @Override
    @Nonnull
    public FragmentationType getFragmentationType() {
        return fragmentationType;
    }

    @Override
    @Nullable
    public Double getActivationEnergy() {
        return activationEnergy;
    }

}
