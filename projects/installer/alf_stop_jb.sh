#!/bin/sh
# ---------------------------------------------------------------------------
# Start script for the Alfresco Server
# ---------------------------------------------------------------------------

# ---------------------------------------
# Shutdown JBoss
# ---------------------------------------

echo "Shutting down JBoss..."
cd ~/alfresco/jboss
sh bin/shutdown.sh -S
cd ~/alfresco

# ---------------------------------------
# Stop OpenOffice for transformations
# ---------------------------------------
if [ -r "start_oo.sh" ] ; then
    sh ./stop_oo.sh
fi

