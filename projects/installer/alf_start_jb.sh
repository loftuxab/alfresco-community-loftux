#!/bin/sh
# ---------------------------------------------------------------------------
# Start script for the Alfresco Server
# ---------------------------------------------------------------------------

# ---------------------------------------
# Start JBoss
# ---------------------------------------

echo "Starting JBoss..."
cd ~/alfresco/jboss
sh bin/run.sh
cd ~/alfresco

# ---------------------------------------
# Start OpenOffice for transformations
# ---------------------------------------
if [ -r "start_oo.sh" ] ; then
    sh ./start_oo.sh
fi

