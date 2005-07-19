#!/bin/sh
# ---------------------------------------------------------------------------
# Start script for the OpenOffice transform service
# ---------------------------------------------------------------------------

echo "Starting OpenOffice service..."
~/OpenOffice.org1.1.4/bin/soffice "-accept=socket,host=localhost,port=8100;urp;StarOffice.ServiceManager" -nologo -headless &

