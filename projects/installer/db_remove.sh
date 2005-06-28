#!/bin/sh
# ---------------------------------------
# MySQL create DB command
# ---------------------------------------

echo "Starting MySQL..."
~/alfresco/mysql/bin/mysqld --console
sleep 3

echo "Deleting Alfresco database..."
~/alfresco/mysql/bin/mysqladmin -u root drop alfresco

echo "Shutting down MySQL..."
~/alfresco/mysql/bin/mysqladmin -u root shutdown

echo "Deleting indexes..."
\rm -r alfresco/tomcat/alfresco/contentstore alfresco/tomcat/alfresco/lucene-indexes
\rm -r alfresco/contentstore alfresco/lucene-indexes

