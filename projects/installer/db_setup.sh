#!/bin/sh
source ./paths_tc.sh

echo "Creating Alfresco database and user..."
$MYSQL_HOME/bin/mysql" -u root -p < db_setup.sql

echo "Database prepared."

