# About MZmine 3

MZmine 3 is an open-source software for mass-spectrometry data processing. The goals of the project is to provide a user-friendly, flexible and easily extendable software with a complete set of modules covering the entire MS data analysis workflow. All algorithms are stored in the [Mass Spectrometry Development Kit (MSDK)](https://github.com/msdk/msdk).

More information about the software can be found on the [MZmine](http://mzmine.github.io/) website.

## Java version

MZmine requires Java runtime (JRE) version 1.8 or newer.

## License

MZmine 3 is a free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either [version 2](http://www.gnu.org/licenses/gpl-2.0.html) of the License, or (at your option) any [later version](http://www.gnu.org/licenses/gpl.html).

MZmine 3 is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

## Development

### Tutorial

Please read our brief [tutorial](http://mzmine.github.io/development.html) on how to contribute new code to MZmine.

### Building

You will need the following prerequisites to build MZmine from sources:
- Java JDK version 1.8 or later
- Apache Maven version 3.2 or later

To build the MZmine package from the sources as version X.Y, run the following
commands in this directory:

mvn versions:set -DgenerateBackupPoms=false -DnewVersion=X.Y
mvn clean jfx:native

The MZmine distribution will be placed in target/jfx/native

If you encounter any problems, please contact the developers:
https://github.com/mzmine/mzmine3/issues

### Code style

* We use the Google Java Style Guide (https://github.com/google/styleguide)
* You can use the `eclipse-java-google-style.xml` file to automatically format your code in the Eclipse IDE
* Please write JavaDoc comments as full sentences, starting with a capital letter and ending with a period. Brevity is preferred (e.g., "Calculates standard deviation" instead of "This method calculates and returns a standard deviation of given set of numbers").


