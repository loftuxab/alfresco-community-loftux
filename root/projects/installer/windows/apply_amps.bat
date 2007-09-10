@echo off
rem -------
rem Script for apply AMPs to installed WAR
rem -------

set ALF_HOME=%~dp0
set CATALINA_HOME=%ALF_HOME%tomcat

if not exist "SetPaths.bat" goto getpaths 
call SetPaths.bat
goto start

:getpaths
call %ALF_HOME%bin\RegPaths.exe
call SetPaths.bat
del SetPaths.bat

:start
echo This script will apply all the AMPs in %ALF_HOME%amps to the alfresco.war file in %CATALINA_HOME%\webapps
echo Press control-c to stop this script . . .
pause
java -jar %ALF_HOME%bin\alfresco-mmt.jar install %ALF_HOME%amps %CATALINA_HOME%\webapps\alfresco.war -directory

call %ALF_HOME%bin\clean_tomcat.bat