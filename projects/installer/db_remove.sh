#!/bin/sh
source ./paths_tc.sh

echo "Deleting Alfresco database and user..."
$MYSQL_HOME/bin/mysql -u root -p < db_remove.sql

echo "Deleting indexes..."
\rm -rf alf_data
