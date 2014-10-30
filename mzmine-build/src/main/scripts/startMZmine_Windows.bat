@echo off

rem Obtain the physical memory size
IF EXIST 'C:\Windows\System32\wbem\wmic os get totalvisiblememorysize' (
  rem Get physical memory size from OS
  for /f "skip=1" %%p in ('C:\Windows\System32\wbem\wmic os get totalvisiblememorysize') do if not defined TOTAL_MEMORY set TOTAL_MEMORY=%%p 
) ELSE (
  rem Set 2GB as default - heap size will be set to 1G
  set TOTAL_MEMORY=2000000
)

rem The HEAP_SIZE variable defines the Java heap size in MB. 
rem That is the total amount of memory available to MZmine 2.
rem By default we set this to the half of the physical memory 
rem size, but feel free to adjust according to your needs. 
set /a HEAP_SIZE=%TOTAL_MEMORY% / 1024 / 2

rem Check if we are running on a 32-bit system. 
rem If yes, force the heap size to 1024 MB.
for /f "skip=1" %%x in ('wmic cpu get addresswidth') do if not defined ADDRESS_WIDTH set ADDRESS_WIDTH=%%x
if %ADDRESS_WIDTH%==32 (
  set HEAP_SIZE=1024
) 

rem The TMP_FILE_DIRECTORY parameter defines the location where temporary 
rem files (parsed raw data) will be placed. Default is %TEMP%, which 
rem represents the system temporary directory.
set TMP_FILE_DIRECTORY=%TEMP%

rem It is usually not necessary to modify the JAVA_COMMAND parameter, but 
rem if you like to run a specific Java Virtual Machine, you may set the 
rem path to the java command of that JVM
set JAVA_COMMAND=java

rem It is not necessary to modify the following section
set JAVA_PARAMETERS=-showversion -Djava.io.tmpdir=%TMP_FILE_DIRECTORY% -Xms%HEAP_SIZE%m -Xmx%HEAP_SIZE%m -Djava.library.path="%JRI_LIB_PATH%"
set CLASS_PATH=lib\*
set MAIN_CLASS=net.sf.mzmine.main.MZmineCore

rem This command starts the Java Virtual Machine
%JAVA_COMMAND% %JAVA_PARAMETERS% -classpath %CLASS_PATH% %MAIN_CLASS% %*

rem If there was an error, give the user chance to see it
IF ERRORLEVEL 1 pause
