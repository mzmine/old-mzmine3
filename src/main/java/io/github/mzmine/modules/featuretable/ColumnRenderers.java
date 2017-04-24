package io.github.mzmine.modules.featuretable;

import io.github.msdk.datamodel.featuretables.ColumnName;
import io.github.mzmine.modules.featuretable.renderers.ChromatogramRenderer;
import io.github.mzmine.modules.featuretable.renderers.ChromatographyInfoRenderer;
import io.github.mzmine.modules.featuretable.renderers.DefaultRenderer;
import io.github.mzmine.modules.featuretable.renderers.DoubleRenderer;
import io.github.mzmine.modules.featuretable.renderers.IntegerRenderer;
import io.github.mzmine.modules.featuretable.renderers.IntensityRenderer;
import io.github.mzmine.modules.featuretable.renderers.IonAnnotationRenderer;
import io.github.mzmine.modules.featuretable.renderers.MzRenderer;
import io.github.mzmine.modules.featuretable.renderers.RtRenderer;

public class ColumnRenderers {

  public static Class<?> getRenderClass(String columnName) {

    if (columnName.equals(ColumnName.ID.getName()))
      return IntegerRenderer.class;
    if (columnName.equals(ColumnName.MZ.getName()))
      return MzRenderer.class;
    if (columnName.equals(ColumnName.RT.getName()))
      return ChromatographyInfoRenderer.class;
    if (columnName.equals(ColumnName.RTSTART.getName()))
      return RtRenderer.class;
    if (columnName.equals(ColumnName.RTEND.getName()))
      return RtRenderer.class;
    if (columnName.equals(ColumnName.DURATION.getName()))
      return DoubleRenderer.class;
    if (columnName.equals(ColumnName.AREA.getName()))
      return IntensityRenderer.class;
    if (columnName.equals(ColumnName.HEIGHT.getName()))
      return IntensityRenderer.class;
    if (columnName.equals(ColumnName.CHARGE.getName()))
      return IntegerRenderer.class;
    if (columnName.equals(ColumnName.NUMBEROFDATAPOINTS.getName()))
      return IntegerRenderer.class;
    if (columnName.equals(ColumnName.FWHM.getName()))
      return DoubleRenderer.class;
    if (columnName.equals(ColumnName.TAILINGFACTOR.getName()))
      return DoubleRenderer.class;
    if (columnName.equals(ColumnName.ASYMMETRYFACTOR.getName()))
      return DoubleRenderer.class;
    if (columnName.equals(ColumnName.CHROMATOGRAM.getName()))
      return ChromatogramRenderer.class;
    if (columnName.equals("Chromatography Info"))
      return ChromatographyInfoRenderer.class;
    if (columnName.equals(ColumnName.IONANNOTATION.getName()))
      return IonAnnotationRenderer.class;

    // Default
    return DefaultRenderer.class;
  }

}
