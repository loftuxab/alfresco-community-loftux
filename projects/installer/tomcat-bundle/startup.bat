@echo off
rem ---------------------------------------------------------------------------
rem Start script for the Alfresco Server
rem ---------------------------------------------------------------------------

echo Starting database...

rem ---------------------------------------
rem Start DB (MySQL) in a minimised console
rem ---------------------------------------
start "MySQL Server" /min cmd /c c:\mysql\bin\mysqld-nt --console
sleep 5

rem ---------------------------------------
rem Start DB (Postgres)
rem ---------------------------------------
rem cd \alfresco\tomcat
rem c:\alfresco\postgres\bin\pg_ctl start -D c:\alfresco\data -l c:\alfresco\logfile

rem ---------------------------------------
rem Start DB (HSQLDB) in a minimised console
rem ---------------------------------------
rem start "HSQL Server" /min cmd /c java -cp tomcat/lib/hsqldb.jar org.hsqldb.Server -database.0 alfresco -dbname.0 alfresco

echo Starting Tomcat...

rem ---------------------------------------
rem Start Tomcat
rem ---------------------------------------
cd \alfresco\tomcat
call bin\startup.bat
cd \alfresco
