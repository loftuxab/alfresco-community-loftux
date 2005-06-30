#!/bin/sh
# ---------------------------------------------------------------------------
# Start script for the Alfresco Server
# ---------------------------------------------------------------------------

# ---------------------------------------
# Shutdown Tomcat
# ---------------------------------------

echo "Shutting down Tomcat..."
cd ~/alfresco/tomcat
sh bin/shutdown.sh
cd ~/alfresco

# ---------------------------------------
# Stop OpenOffice for transformations
# ---------------------------------------
if [ -r "start_oo.sh" ] ; then
    sh ./stop_oo.sh
fi

