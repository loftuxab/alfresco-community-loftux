#!/bin/sh
# ---------------------------------------------------------------------------
# Start script for the Alfresco Server
# ---------------------------------------------------------------------------

# ---------------------------------------
# Shutdown Tomcat
# ---------------------------------------

echo "Shutting down Tomcat..."
cd ~/alfresco/tomcat
. bin/shutdown.sh
cd ~/alfresco

# ---------------------------------------
# Shutdown MySQL
# ---------------------------------------

echo "Shutting down MySQL..."
mysqladmin -u root shutdown

# ---------------------------------------
# Stop OpenOffice for transformations
# ---------------------------------------
if [ -r "start_oo.sh" ] ; then
    . stop_oo.sh
fi

