#!/bin/sh
# ---------------------------------------------------------------------------
# Start script for the Alfresco Server
# ---------------------------------------------------------------------------

# ---------------------------------------
# Start DB (MySQL) in a minimised console
# ---------------------------------------

echo "Starting MySQL..."
mysqld --console

# ---------------------------------------
# Start JBoss
# ---------------------------------------

echo "Starting JBoss..."
cd ~/alfresco/jboss
. bin/run.sh
cd ~/alfresco

# ---------------------------------------
# Start OpenOffice for transformations
# ---------------------------------------
if [ -r "start_oo.sh" ] ; then
    . start_oo.sh
fi

