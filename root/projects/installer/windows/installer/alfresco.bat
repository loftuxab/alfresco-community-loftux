@echo off
rem ---------------------------------------------------------------------------
rem Start script for the Alfresco Server
rem ---------------------------------------------------------------------------

rem set Alfresco home (includes trailing \  e.g. c:\alfresco\)
set ALF_HOME=%~dp0
set ALF_HOME_URI=%ALF_HOME:\=/%

set CATALINA_HOME=%ALF_HOME%tomcat

if not exist "%ALF_HOME%java" goto nojava
if "%PROGRAMHOME(X86)%" == "" set JAVA_HOME=%ALF_HOME%java

:nojava
rem Set any default JVM options
set JAVA_OPTS=-Xms128m -Xmx512m -Xss64k -server
rem The following options are only required for Sun JVMs prior to 1.5 update 8
set JAVA_OPTS=%JAVA_OPTS% -XX:CompileCommand=exclude,org/apache/lucene/index/IndexReader$1,doBody -XX:CompileCommand=exclude,org/alfresco/repo/search/impl/lucene/index/IndexInfo$Merger,mergeIndexes -XX:CompileCommand=exclude,org/alfresco/repo/search/impl/lucene/index/IndexInfo$Merger,mergeDeletions

set OPENOFFICE_PATH=%ALF_HOME%OpenOfficePortable

:start
set PATH=%JAVA_HOME%\bin;%ALF_HOME%bin;%PATH%
rem ---------------------------------------
rem Start Components
rem ---------------------------------------

if not ""%1"" == ""start"" goto nostart

rem ---------------------------------------
rem Start Tomcat
rem ---------------------------------------

echo Starting Tomcat...
call "%CATALINA_HOME%\bin\startup.bat"

rem ---------------------------------
rem Start Virtualization if available
rem ---------------------------------
rem if exist "%~dp0virtual_start.bat" call "%~dp0virtual_start.bat" 

rem ---------------------------------------
rem Start OpenOffice for transformations
rem ---------------------------------------
if not "%OPENOFFICE_PATH%" == "" call "%OPENOFFICE_PATH%\OpenOfficePortable.exe" "-accept=socket,host=localhost,port=8100;urp;StarOffice.ServiceManager" "-env:UserInstallation=file:///%ALF_HOME_URI%oouser" -nologo -headless -nofirststartwizard

goto nostop
:nostart

rem ---------------------------------------
rem Stop Components
rem ---------------------------------------

if not ""%1"" == ""stop"" goto nostop

echo Shutting down Tomcat...
call "%CATALINA_HOME%\bin\shutdown.bat" 

rem if exist "%~dp0virtual_start.bat" call "%~dp0virtual_stop.bat" 

if not "%OPENOFFICE_PATH%" == "" "%ALF_HOME%bin\process" -k soffice.bin

:nostop
