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

rem ---------------------------------------
rem Shutdown Postgres
rem ---------------------------------------
rem c:\alfresco\postgres\bin\pg_ctl stop -D c:\alfresco\data

rem ---------------------------------------
rem Shutdown HSQLDB
rem ---------------------------------------
rem java -cp tomcat/lib/hsqldb.jar  org.hsqldb.util.SqlTool shutdown
rem java -jar tomcat/lib/hsqldb.jar --noinput --sql 'shutdown;' localhost-sa



