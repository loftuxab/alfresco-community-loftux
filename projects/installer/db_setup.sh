#!/bin/sh
# ---------------------------------------
# MySQL create DB command
# ---------------------------------------

echo "Initializing MySQL..."
~/alfresco/mysql/bin/mysql_install_db

echo "Starting MySQL..."
~/alfresco/mysql/bin/mysqld --console
sleep 3

echo "Creating Alfresco database..."
~/alfresco/mysql/bin/mysqladmin -u root create alfresco
~/alfresco/mysql/bin/mysql -u root -e "grant all on alfresco.* to 'alfresco'@'localhost' identified by 'alfresco' with grant option;"

echo "Shutting down MySQL..."
~/alfresco/mysql/bin/mysqladmin -u root shutdown

