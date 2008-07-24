@echo off
rem ---------------------------------------------------------------------------
rem Start script for the Alfresco Server
rem ---------------------------------------------------------------------------

rem set Alfresco home (includes trailing \  e.g. c:\alfresco\)
set ALF_HOME=%~dp0
set CATALINA_HOME=%ALF_HOME%tomcat

rem Set any default JVM options
set JAVA_OPTS=-Xms128m -Xmx512m -Xss96k -XX:MaxPermSize=128m -server

rem --- If SetPaths.bat already exists - assume set by hand and use as is
set PATH=%ALF_HOME%bin;%PATH%
if not exist "SetPaths.bat" goto getpaths 
call SetPaths.bat
goto start

:getpaths
call RegPaths.exe
call SetPaths.bat
del SetPaths.bat

:start
set PATH=%JAVA_HOME%\bin;%PATH%
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
rem if exist "~dp0virtual_start.bat" call "~dp0virtual_start.bat" 

goto nostop
:nostart

rem ---------------------------------------
rem Stop Components
rem ---------------------------------------

if not ""%1"" == ""stop"" goto nostop

echo Shutting down Tomcat...
call "%CATALINA_HOME%\bin\shutdown.bat" 

rem if exist "virtual_start.bat" call virtual_stop.bat 

:nostop