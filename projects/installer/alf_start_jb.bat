@echo off
rem ---------------------------------------------------------------------------
rem Start script for the Alfresco Server
rem ---------------------------------------------------------------------------

rem ---------------------------------------
rem Start DB (MySQL) in a minimised console
rem ---------------------------------------

echo Starting MySQL...
start "MySQL Server" /min cmd /c c:\mysql\bin\mysqld-nt --console

rem ---------------------------------------
rem Start JBoss
rem ---------------------------------------

echo Starting JBoss...
cd \alfresco\jboss
start "JBoss Server" cmd /c bin\run.bat
cd \alfresco

rem ---------------------------------------
rem Start OpenOffice for transformations
rem ---------------------------------------
if exist "start_oo.bat" call "start_oo.bat"
