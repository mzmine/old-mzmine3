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

package io.github.mzmine.parameters;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javafx.scene.control.ButtonType;

/**
 * Simple storage for the parameters. A typical MZmine module will inherit this class and define the
 * parameters for the constructor.
 */
public class ParameterSet implements Iterable<Parameter<?>>, Cloneable {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private static final String parameterElement = "parameter";
  private static final String nameAttribute = "name";

  private final List<Parameter<?>> parameters = new ArrayList<>();

  public ParameterSet(Parameter<?>... items) {
    for (Parameter<?> p : items) {
      parameters.add(p);
    }
  }

  /**
   * Represent method's parameters and their values in human-readable format
   */
  public String toString() {

    StringBuilder s = new StringBuilder();
    for (int i = 0; i < parameters.size(); i++) {
      Parameter<?> param = parameters.get(i);
      Object value = param.getValue();
      s.append(param.getName());
      s.append(": ");
      s.append(String.valueOf(value));
      if (i < parameters.size() - 1)
        s.append(", ");
    }
    return s.toString();
  }

  /**
   * Make a deep copy
   */
  public ParameterSet clone() {
    // Do not make a new instance of ParameterSet, but instead
    // clone the runtime class of this instance - runtime type may be
    // inherited class. This is important in order to keep the proper
    // behavior of showSetupDialog(xxx) method for cloned classes

    ParameterSet newSet;
    try {
      newSet = this.getClass().newInstance();
    } catch (InstantiationException | IllegalAccessException e) {
      e.printStackTrace();
      return null;
    }
    for (Parameter<?> param : parameters) {
      Object value = param.getValue();
      Parameter<?> newParam = newSet.getParameter(param);
      if (newParam == null)
        throw new IllegalStateException("Cannot clone parameter set of type " + this.getClass());
      newParam.setValue(value);
    }
    return newSet;
  }

  public List<Parameter<?>> getParameters() {
    return parameters;
  }

  public ButtonType showSetupDialog(@Nullable String title) {
    ParameterSetupDialog dialog = new ParameterSetupDialog(this, title);
    Optional<ButtonType> result = dialog.showAndWait();
    return result.get();
  }

  @SuppressWarnings("unchecked")
  public <T extends Parameter<?>> T getParameter(T parameter) {
    for (Parameter<?> p : parameters) {
      if (p.getName().equals(parameter.getName()))
        return (T) p;
    }
    throw new IllegalArgumentException("Parameter " + parameter.getName() + " does not exist");
  }

  public void loadValuesFromXML(Element xmlElement) {
    NodeList list = xmlElement.getElementsByTagName(parameterElement);
    for (int i = 0; i < list.getLength(); i++) {
      Element nextElement = (Element) list.item(i);
      String paramName = nextElement.getAttribute(nameAttribute);
      for (Parameter<?> param : parameters) {
        if (param.getName().equals(paramName)) {
          try {
            param.loadValueFromXML(nextElement);
          } catch (Exception e) {
            logger.warn("Error while loading parameter values for " + param.getName(), e);
          }
        }
      }
    }
  }

  public void saveValuesToXML(Element xmlElement) {
    Document parentDocument = xmlElement.getOwnerDocument();
    for (Parameter<?> param : parameters) {
      Element paramElement = parentDocument.createElement(parameterElement);
      paramElement.setAttribute(nameAttribute, param.getName());
      xmlElement.appendChild(paramElement);
      param.saveValueToXML(paramElement);
    }
  }

  @Override
  public Iterator<Parameter<?>> iterator() {
    return parameters.iterator();
  }

}
