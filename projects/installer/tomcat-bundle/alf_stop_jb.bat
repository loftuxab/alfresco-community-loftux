@echo off
rem ---------------------------------------------------------------------------
rem Start script for the Alfresco Server
rem ---------------------------------------------------------------------------

rem ---------------------------------------
rem Shutdown JBoss
rem ---------------------------------------
cd \alfresco\jboss
call bin\shutdown.bat -S
cd \alfresco

rem ---------------------------------------
rem Shutdown MySQL
rem ---------------------------------------
c:\mysql\bin\mysqladmin -u root shutdown

rem ---------------------------------------
rem Stop OpenOffice for transformations
rem ---------------------------------------
if exist "start_oo.bat" call "stop_oo.bat"

