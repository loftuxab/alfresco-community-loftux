@echo off
rem ---------------------------------------
rem MySQL create DB command
rem ---------------------------------------

echo Creating Alfresco database and user...
"@@BITROCK_ALFRESCO_MYSQLDIR@@\bin\mysql" -u root -p < @@BITROCK_ALFRESCO_INSTALLDIR@@\bin\db_setup.sql

echo Database prepared.

