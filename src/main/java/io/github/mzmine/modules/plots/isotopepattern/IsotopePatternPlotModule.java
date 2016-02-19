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

package io.github.mzmine.modules.plots.isotopepattern;

import java.util.Collection;

import javax.annotation.Nonnull;

import com.google.common.base.Preconditions;

import io.github.msdk.datamodel.msspectra.MsSpectrum;
import io.github.msdk.spectra.isotopepattern.IsotopePatternGeneratorAlgorithm;
import io.github.mzmine.modules.MZmineRunnableModule;
import io.github.mzmine.modules.plots.msspectrum.MsSpectrumPlotModule;
import io.github.mzmine.parameters.ParameterSet;
import io.github.mzmine.project.MZmineProject;
import javafx.concurrent.Task;

/**
 * MS spectrum plot
 */
public class IsotopePatternPlotModule implements MZmineRunnableModule {

    private static final @Nonnull String MODULE_NAME = "Isotope pattern plot";
    private static final @Nonnull String MODULE_DESCRIPTION = "Isotope pattern plot";

    @Override
    public @Nonnull String getName() {
        return MODULE_NAME;
    }

    @Override
    public @Nonnull String getDescription() {
        return MODULE_DESCRIPTION;
    }

    @Override
    public void runModule(@Nonnull MZmineProject project,
            @Nonnull ParameterSet parameters,
            @Nonnull Collection<Task<?>> tasks) {

        final String formula = parameters
                .getParameter(IsotopePatternPlotParameters.formula).getValue();
        final Double mzTolerance = parameters
                .getParameter(IsotopePatternPlotParameters.mzTolerance)
                .getValue();
        final Double minAbundance = parameters
                .getParameter(IsotopePatternPlotParameters.minAbundance)
                .getValue();
        final Double normalizedIntensity = parameters
                .getParameter(IsotopePatternPlotParameters.normalizedIntensity)
                .getValue();

        Preconditions.checkNotNull(formula);
        Preconditions.checkNotNull(mzTolerance);
        Preconditions.checkNotNull(minAbundance);
        Preconditions.checkNotNull(normalizedIntensity);

        final MsSpectrum pattern = IsotopePatternGeneratorAlgorithm
                .generateIsotopes(formula, minAbundance,
                        normalizedIntensity.floatValue(), mzTolerance);

        MsSpectrumPlotModule.showNewSpectrumWindow(pattern, formula, false);

    }

    @Override
    public @Nonnull Class<? extends ParameterSet> getParameterSetClass() {
        return IsotopePatternPlotParameters.class;
    }

}