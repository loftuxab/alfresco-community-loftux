@echo off
rem ---------------------------------------
rem MySQL remove DB command
rem ---------------------------------------

echo Deleting Alfresco database and user...
mysql -u root -p < db_remove.sql

echo Deleting indexes...
del /s /q ../../../alf_data
