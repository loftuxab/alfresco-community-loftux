@echo off
rem ---------------------------------------
rem MySQL remove DB command
rem ---------------------------------------

echo Deleting Alfresco database and user...
@@BITROCK_ALFRESCO_MYSQLDIR@@\bin\mysql -u root -p < db_remove.sql

echo Deleting indexes...
del /s /q alf_data
