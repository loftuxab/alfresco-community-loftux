@echo off
rem ---------------------------------------
rem MySQL remove DB command
rem ---------------------------------------

echo Deleting Alfresco database and user...
"@@BITROCK_ALFRESCO_MYSQLDIR@@\bin\mysql" -u root -p < @@BITROCK_ALFRESCO_INSTALLDIR@@\bin\db_remove.sql

echo Deleting indexes...
del /s /q @@BITROCK_ALFRESCO_INSTALLDIR@@\alf_data
