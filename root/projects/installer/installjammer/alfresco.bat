@echo off
rem ---------------------------------------------------------------------------
rem Start script for the Alfresco Server
rem ---------------------------------------------------------------------------

rem set Alfresco home (includes trailing \  e.g. c:\alfresco\)
set ALF_HOME=%~dp0

set CATALINA_HOME=%ALF_HOME%tomcat

set JAVA_HOME=%ALF_HOME%java
set OPENOFFICE_PATH=%ALF_HOME%OpenOfficePortable


rem Set any default JVM options
set JAVA_OPTS=-Xms128m -Xmx512m -Xss64k -server -XX:CompileCommand=exclude,org/apache/lucene/index/IndexReader$1,doBody

:start
set PATH=%JAVA_HOME%/bin;%ALF_HOME%bin;%PATH%
rem ---------------------------------------
rem Start Components
rem ---------------------------------------

if not ""%1"" == ""start"" goto nostart

rem ---------------------------------------
rem Start Tomcat
rem ---------------------------------------

echo Starting Tomcat...
call "%CATALINA_HOME%\bin\startup.bat"

rem ---------------------------------------
rem Start OpenOffice for transformations
rem ---------------------------------------
if not "%OPENOFFICE_PATH%" == "" call "start_oo.bat"

goto nostop
:nostart

rem ---------------------------------------
rem Stop Components
rem ---------------------------------------

if not ""%1"" == ""stop"" goto nostop

echo Shutting down Tomcat...
call "%CATALINA_HOME%\bin\shutdown.bat" 

rem ---------------------------------------
rem Stop OpenOffice for transformations
rem ---------------------------------------
if not "%OPENOFFICE_PATH%" == "" c:\windows\system32\taskkill /f /im soffice.bin

:nostop