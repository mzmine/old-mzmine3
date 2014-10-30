
This is an attempt to build a multi-module maven configuration where each MZmine module
would be completely separated with its own pom.xml file, source files, resources, tests etc.
Major annoyances
- building the final distribution of MZmine requries additional module (mzmine-build), which 
  has to keep dependencies on all other modules, despide the fact these are already listed in
  the main (parent) pom.xml
- version numbers of all modules have to match and must be specified in each pom.xml and
  cannot be inherited from parent pom.xml
- each module is treated as a separate project in Eclipse, and making a Run configuration is hell

  

