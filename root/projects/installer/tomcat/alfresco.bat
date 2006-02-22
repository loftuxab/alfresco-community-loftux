@echo off
rem ---------------------------------------------------------------------------
rem Start script for the Alfresco Server
rem ---------------------------------------------------------------------------

set CATALINA_HOME=C:\alfresco\tomcat

rem Set any default JVM options
set JAVA_OPTS=-Xms128m -Xmx512m -Xss64k -server

rem --- If SetPaths.bat already exists - assume set by hand and use as is
if not exist "SetPaths.bat" goto getpaths 
call SetPaths.bat
goto start

:getpaths
call RegPaths.exe
call SetPaths.bat
del SetPaths.bat

:start
set PATH=%JAVA_HOME%/bin:%PATH%
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
if exist "start_oo.bat" call "start_oo.bat"

goto nostop
:nostart

rem ---------------------------------------
rem Stop Components
rem ---------------------------------------

if not ""%1"" == ""stop"" goto nostop

echo Shutting down Tomcat...
call %CATALINA_HOME%\bin\shutdown.bat 

rem ---------------------------------------
rem Stop OpenOffice for transformations
rem ---------------------------------------
if exist "start_oo.bat" c:\windows\system32\taskkill /f /im soffice.bin

:nostop