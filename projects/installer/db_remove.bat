@echo off
rem ---------------------------------------
rem MySQL create DB command
rem ---------------------------------------

echo Starting MySQL...
start "MySQL Server" /min cmd /c c:\mysql\bin\mysqld-nt --console
rem sleep 3

echo Deleting Alfresco database...
c:\mysql\bin\mysqladmin -u root drop alfresco

echo Shutting down MySQL...
c:\mysql\bin\mysqladmin -u root shutdown

echo Deleting indexes...
del /s /q tomcat\alfresco\contentstore tomcat\alfresco\lucene-indexes
del /s /q jboss\alfresco\contentstore jboss\alfresco\lucene-indexes
