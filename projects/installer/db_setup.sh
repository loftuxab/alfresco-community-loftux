#!/bin/sh
# ---------------------------------------
# MySQL create DB command
# ---------------------------------------

echo "Creating Alfresco database..."
mysqladmin -u root create alfresco
mysql -u root -e "grant all on alfresco.* to 'alfresco'@'localhost' identified by 'alfresco' with grant option;"

