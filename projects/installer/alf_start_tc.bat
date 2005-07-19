@echo off
rem ---------------------------------------------------------------------------
rem Start script for the Alfresco Server
rem ---------------------------------------------------------------------------

if not "%JAVA_HOME%" == "" goto javaOk
echo Please set the JAVA_HOME environment variable.
pause
goto end

:javaOk

rem ---------------------------------------
rem Start DB (MySQL) in a minimised console
rem ---------------------------------------

echo Starting MySQL...
start "MySQL Server" /min cmd /c c:\mysql\bin\mysqld-nt --console
rem sleep 3

rem ---------------------------------------
rem Start Tomcat
rem ---------------------------------------

echo Starting Tomcat...
cd \alfresco\tomcat
call bin\startup.bat
cd \alfresco

rem ---------------------------------------
rem Start OpenOffice for transformations
rem ---------------------------------------
if exist "start_oo.bat" call "start_oo.bat"

:end