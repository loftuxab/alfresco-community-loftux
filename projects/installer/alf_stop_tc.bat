@echo off
rem ---------------------------------------------------------------------------
rem Start script for the Alfresco Server
rem ---------------------------------------------------------------------------

call paths_tc.bat

rem ---------------------------------------
rem Shutdown Tomcat
rem ---------------------------------------

echo Shutting down Tomcat...
call "%CATALINA_HOME%\bin\shutdown.bat"

rem ---------------------------------------
rem Stop OpenOffice for transformations
rem ---------------------------------------
if exist "start_oo.bat" call "stop_oo.bat"

