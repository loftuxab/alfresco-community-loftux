@echo off
rem ---------------------------------------------------------------------------
rem Start script for the Alfresco Server
rem ---------------------------------------------------------------------------

set JBOSS_HOME=C:\alfresco\jboss
set JAVA_HOME=C:\Program Files\Java\jdk1.5.0_04
set PATH=%JAVA_HOME%/bin:%PATH%

rem ---------------------------------------
rem Shutdown JBoss
rem ---------------------------------------

echo Shutting down JBoss...
call %JBOSS_HOME%\bin\shutdown.bat -S

rem ---------------------------------------
rem Stop OpenOffice for transformations
rem ---------------------------------------
if exist "start_oo.bat" call "stop_oo.bat"

