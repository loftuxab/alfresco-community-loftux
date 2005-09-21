@echo off
rem ---------------------------------------
rem MySQL create DB command
rem ---------------------------------------

call paths_tc.bat

echo Creating Alfresco database and user...
"%MYSQL_HOME%\bin\mysql" -u root -p < db_setup.sql

echo Database prepared.

