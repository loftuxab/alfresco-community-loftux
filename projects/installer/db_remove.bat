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
rem del /s /q contentstore lucene-indexes
del /s /q c:\alfresco\tomcat\alfresco\contentstore c:\alfresco\tomcat\alfresco\lucene-indexes
del /s /q c:\alfresco\contentstore c:\alfresco\lucene-indexes

