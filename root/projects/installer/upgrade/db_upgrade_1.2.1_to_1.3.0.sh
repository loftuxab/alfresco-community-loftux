#!/bin/sh
#---------------------------------------
# MySQL run script
#---------------------------------------

echo "Upgrading Alfresco database..."
mysql -D alfresco -u alfresco -p < scripts\AlfrescoSchemaMigrate-1.3-mysql.sql

echo "Database upgraded to 1.3.0"

