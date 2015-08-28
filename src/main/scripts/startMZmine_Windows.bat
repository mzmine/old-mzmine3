@echo off

:: *****************************************
:: Optional values - Please modify as needed
:: *****************************************

:: Total amount of memory in MB available to MZmine 2.
:: AUTO = automatically determined
:: Default: AUTO
set HEAP_SIZE=AUTO

:: Location where temporary files will be stored.
:: Default: %TEMP%
set TMP_FILE_DIRECTORY=%TEMP%

:: It is usually not necessary to modify the JAVA_COMMAND parameter, but if you like to run
:: a specific Java Virtual Machine, you may set the path to the java.exe command of that JVM
set JAVA_COMMAND=java.exe

:: ********************************************
:: You don't need to modify anything below here
:: ********************************************



:: ***********************************
:: Auto detection of accessible memory
:: ***********************************

:: This is necessary to access the TOTAL_MEMORY and ADDRESS_WIDTH variables inside the IF block
setlocal enabledelayedexpansion

:: Obtain the physical memory size and check if we are running on a 32-bit system.
if exist C:\Windows\System32\wbem\wmic.exe (
  echo Checking physical memory size...
  :: Get physical memory size from OS
  for /f "skip=1" %%p in ('C:\Windows\System32\wbem\wmic.exe os get totalvisiblememorysize') do if not defined TOTAL_MEMORY set /a TOTAL_MEMORY=%%p / 1024
  for /f "skip=1" %%x in ('C:\Windows\System32\wbem\wmic.exe cpu get addresswidth') do if not defined ADDRESS_WIDTH set ADDRESS_WIDTH=%%x
  echo Found !TOTAL_MEMORY! MB memory, !ADDRESS_WIDTH!-bit system
) else (
  echo Skipping memory size check, because wmic.exe could not be found
  set ADDRESS_WIDTH=32
)

:: By default we set the maximum HEAP_SIZE to 1024 MB on 32-bit systems. On 64-bit systems we 
:: either set it to half of the total memory or 2048 MB less than the total memory.
if %HEAP_SIZE%==AUTO (
  if %ADDRESS_WIDTH%==32 (
    set HEAP_SIZE=1024
  ) else (
    if %TOTAL_MEMORY% gtr 4096 (
	  set /a HEAP_SIZE=%TOTAL_MEMORY%-2048
    ) else (
	  set /a HEAP_SIZE=%TOTAL_MEMORY% / 2
    )
  )
)
echo Java maximum heap size set to %HEAP_SIZE% MB



:: **********************
:: Java specific commands
:: **********************

set JAVA_PARAMETERS=-showversion -classpath lib\* -XX:+UseParallelGC -Djava.io.tmpdir=%TMP_FILE_DIRECTORY% -Xms1024m -Xmx%HEAP_SIZE%m
set MAIN_CLASS=io.github.mzmine.main.MZmineMain

rem This command starts the Java Virtual Machine
%JAVA_COMMAND% %JAVA_PARAMETERS% %MAIN_CLASS% %*

rem If there was an error, give the user chance to see it
IF ERRORLEVEL 1 pause