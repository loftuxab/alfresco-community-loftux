@echo off
rem ---------------------------------------
rem MySQL remove DB command
rem ---------------------------------------

call paths_tc.bat

echo Deleting Alfresco database and user...
"%MYSQL_HOME%\bin\mysql" -u root -p < db_remove.sql

echo Deleting indexes...
del /s /q alf_data
