#!/bin/sh

# *****************************************
# Optional values - Please modify as needed
# *****************************************

# Total amount of memory in MB available to MZmine 3.
# AUTO = automatically determined
# Default: AUTO
HEAP_SIZE=AUTO

# Location where temporary files will be stored.
# Default: /tmp
TMP_FILE_DIRECTORY=/tmp

# It is usually not necessary to modify the JAVA_COMMAND parameter, but if you like to run
# a specific Java Virtual Machine, you may set the path to the java command of that JVM
JAVA_COMMAND=java

# ********************************************
# You don't need to modify anything below here
# ********************************************



# ***********************************
# Auto detection of accessible memory
# ***********************************

# By default we set the maximum HEAP_SIZE to 1024 MB on 32-bit systems. On 64-bit systems we 
# either set it to half of the total memory or 2048 MB less than the total memory.
echo "Checking physical memory size..."
TOTAL_MEMORY=`free -b | awk '/Mem:/ { print int($2 / 1024^2) }'`
echo "Found $TOTAL_MEMORY MB memory"

if [ "$HEAP_SIZE" = "AUTO" ]; then
  if [ "$TOTAL_MEMORY" -gt 4096 ]; then
	HEAP_SIZE=`expr $TOTAL_MEMORY - 2048`
  else
	HEAP_SIZE=`expr $TOTAL_MEMORY / 2`
  fi
fi
echo Java maximum heap size set to $HEAP_SIZE MB



# **********************
# Java specific commands
# **********************

JAVA_PARAMETERS="-showversion -classpath lib/\* -XX:+UseG1GC -Djava.io.tmpdir=$TMP_FILE_DIRECTORY -Xms256m -Xmx${HEAP_SIZE}m
MAIN_CLASS=io.github.mzmine.main.MZmineMain

# Make sure we are in the correct directory
SCRIPTDIR=`dirname "$0"`
cd "$SCRIPTDIR"

# This command starts the Java Virtual Machine
echo "$JAVA_PARAMETERS" $MAIN_CLASS "$@" | xargs $JAVA_COMMAND