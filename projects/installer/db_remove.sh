#!/bin/sh
# ---------------------------------------
# MySQL create DB command
# ---------------------------------------

echo "Deleting Alfresco database..."
mysqladmin -u root drop alfresco

echo "Deleting indexes..."
\rm -r alfresco/tomcat/alfresco/contentstore alfresco/tomcat/alfresco/lucene-indexes
\rm -r alfresco/contentstore alfresco/lucene-indexes

