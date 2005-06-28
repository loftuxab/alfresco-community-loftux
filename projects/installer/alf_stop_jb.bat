@echo off
rem ---------------------------------------------------------------------------
rem Start script for the Alfresco Server
rem ---------------------------------------------------------------------------

rem ---------------------------------------
rem Shutdown JBoss
rem ---------------------------------------

echo Shutting down JBoss...
cd \alfresco\jboss
call bin\shutdown.bat -S
cd \alfresco

rem ---------------------------------------
rem Shutdown MySQL
rem ---------------------------------------

echo Shutting down MySQL...
c:\mysql\bin\mysqladmin -u root shutdown

rem ---------------------------------------
rem Stop OpenOffice for transformations
rem ---------------------------------------
if exist "start_oo.bat" call "stop_oo.bat"

