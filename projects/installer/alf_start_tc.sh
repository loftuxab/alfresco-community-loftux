#!/bin/sh
# ---------------------------------------------------------------------------
# Start script for the Alfresco Server
# ---------------------------------------------------------------------------

# ---------------------------------------
# Start DB (MySQL) in a minimised console
# ---------------------------------------

echo "Starting MySQL..."
~/alfresco/mysql/bin/mysqld --console
sleep 3

# ---------------------------------------
# Start Tomcat
# ---------------------------------------

echo "Starting Tomcat..."
cd ~/alfresco/tomcat
. bin/startup.sh
cd ~/alfresco

# ---------------------------------------
# Start OpenOffice for transformations
# ---------------------------------------
if [ -r "start_oo.sh" ] ; then
    . start_oo.sh
fi

