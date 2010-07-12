@echo off
rem ---------------------------------------
rem MySQL remove DB command
rem ---------------------------------------

echo Deleting Alfresco database and user...
mysql -u root -p < mysql_remove.sql
