@echo off
rem ---------------------------------------
rem MySQL create DB command
rem ---------------------------------------
start "MySQL Server" /min cmd /c c:\mysql\bin\mysqld-nt --console
c:\mysql\bin\mysqladmin -u root create alfresco
c:\mysql\bin\mysql -u root -e "grant all on alfresco.* to 'alfresco'@'localhost' identified by 'alfresco' with grant option;"
c:\mysql\bin\mysqladmin -u root shutdown

echo Alfresco database configured.
