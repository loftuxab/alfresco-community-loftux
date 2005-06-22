@echo off
rem ---------------------------------------------------------------------------
rem Start script for the Alfresco Server
rem ---------------------------------------------------------------------------

rem ---------------------------------------
rem Shutdown Tomcat
rem ---------------------------------------
cd \alfresco\tomcat
call bin\shutdown.bat
cd \alfresco

rem ---------------------------------------
rem Shutdown MySQL
rem ---------------------------------------
c:\mysql\bin\mysqladmin -u root shutdown


