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

package io.github.mzmine.parameters.parametertypes.selectors;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import io.github.msdk.datamodel.featuretables.FeatureTable;
import io.github.msdk.datamodel.featuretables.FeatureTableColumn;
import io.github.mzmine.util.TextUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

@Immutable
public class FeatureTableColumnsSelection implements Cloneable {

  private final ObservableList<String> namePatterns;

  public FeatureTableColumnsSelection() {
    namePatterns = FXCollections.observableArrayList();
    namePatterns.add("*");
  }

  public List<FeatureTableColumn<?>> getMatchingColumns(FeatureTable table) {

    List<FeatureTableColumn<?>> allColumns = table.getColumns();

    ArrayList<FeatureTableColumn<?>> matchingColumns = new ArrayList<>();

    for (FeatureTableColumn<?> col : allColumns) {
      final String colName = col.getName();

      for (String namePattern : namePatterns) {
        final String regex = TextUtils.createRegexFromWildcards(namePattern);
        if (colName.matches(regex) && (!matchingColumns.contains(col))) {
          matchingColumns.add(col);
        }
      }
    }
    return matchingColumns;

  }

  public ObservableList<String> getNamePatterns() {
    return namePatterns;
  }

  public String toString() {
    return namePatterns.toString();
  }

  public FeatureTableColumnsSelection clone() {
    FeatureTableColumnsSelection newSelection = new FeatureTableColumnsSelection();
    newSelection.getNamePatterns().addAll(namePatterns);
    return newSelection;
  }

}
