#!/bin/sh
# ---------------------------------------------------------------------------
# Start script for the Alfresco Server
# ---------------------------------------------------------------------------

# ---------------------------------------
# Shutdown JBoss
# ---------------------------------------

echo "Shutting down JBoss..."
cd ~/alfresco/jboss
. bin/shutdown.sh -S
cd ~/alfresco

# ---------------------------------------
# Shutdown MySQL
# ---------------------------------------

echo "Shutting down MySQL..."
./mysql/bin/mysqladmin -u root shutdown

# ---------------------------------------
# Stop OpenOffice for transformations
# ---------------------------------------
if [ -r "start_oo.sh" ] ; then
    . stop_oo.sh
fi

