@echo off
rem ---------------------------------------
rem MySQL create DB command
rem ---------------------------------------

echo Starting MySQL...
start "MySQL Server" /min cmd /c c:\mysql\bin\mysqld-nt --console
rem sleep 3

echo Creating Alfresco database...
c:\mysql\bin\mysqladmin -u root create alfresco
c:\mysql\bin\mysql -u root -e "grant all on alfresco.* to 'alfresco'@'localhost' identified by 'alfresco' with grant option;"

echo Shutting down MySQL...
c:\mysql\bin\mysqladmin -u root shutdown

