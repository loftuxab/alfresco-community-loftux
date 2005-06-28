#!/bin/sh
# ---------------------------------------
# MySQL create DB command
# ---------------------------------------

echo "Starting MySQL..."
mysqld --console
sleep 3

echo "Creating Alfresco database..."
mysqladmin -u root create alfresco
mysql -u root -e "grant all on alfresco.* to 'alfresco'@'localhost' identified by 'alfresco' with grant option;"

echo "Shutting down MySQL..."
mysqladmin -u root shutdown

