@echo off
rem ---------------------------------------
rem MySQL remove DB command
rem ---------------------------------------

echo Upgrading Alfresco database and user...
mysql -D alfresco -u alfresco -p < scripts\AlfrescoSchemaMigrate-1.3-mysql.sql

echo Database upgraded to 1.3.0

