@echo off
rem ---------------------------------------
rem MySQL create DB command
rem ---------------------------------------
start "MySQL Server" /min cmd /c c:\mysql\bin\mysqld-nt --console
sleep 3
c:\mysql\bin\mysqladmin -u root drop alfresco
c:\mysql\bin\mysqladmin -u root shutdown

del /s /q contentstore lucene-indexes

echo Alfresco database configured.
