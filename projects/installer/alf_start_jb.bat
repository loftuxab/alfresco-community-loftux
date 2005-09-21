@echo off
rem ---------------------------------------------------------------------------
rem Start script for the Alfresco Server
rem ---------------------------------------------------------------------------

set JBOSS_HOME=C:\alfresco\jboss
set JAVA_HOME=C:\Program Files\Java\jdk1.5.0_04
set PATH=%JAVA_HOME%/bin:%PATH%

rem ---------------------------------------
rem Start JBoss
rem ---------------------------------------

echo Starting JBoss...
start "JBoss Server" cmd /c %JBOSS_HOME%\bin\run.bat

rem ---------------------------------------
rem Start OpenOffice for transformations
rem ---------------------------------------
if exist "start_oo.bat" call "start_oo.bat"

:end