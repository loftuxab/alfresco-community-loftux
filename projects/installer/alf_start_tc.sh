#!/bin/sh
# ---------------------------------------------------------------------------
# Start script for the Alfresco Server
# ---------------------------------------------------------------------------

# ---------------------------------------
# Start Tomcat
# ---------------------------------------

echo "Starting Tomcat..."
cd ~/alfresco/tomcat
sh bin/startup.sh
cd ~/alfresco

# ---------------------------------------
# Start OpenOffice for transformations
# ---------------------------------------
if [ -r "start_oo.sh" ] ; then
    sh ./start_oo.sh
fi

