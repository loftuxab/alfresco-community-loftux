#!/bin/sh
# ---------------------------------------------------------------------------
# Start script for the OpenOffice transform service
# ---------------------------------------------------------------------------

echo "Starting OpenOffice service..."

# Comment or uncomment the appropriate location using #
# Assumes OpenOffice is installed in /opt
/opt/OpenOffice.org1.1.5/program/soffice "-accept=socket,host=localhost,port=8100;urp;StarOffice.ServiceManager" -nologo -headless -nofirststartwizard &

# If NeoOffice on Mac OS X
#/Applications/NeoOfficeJ.app/Contents/program/soffice "-accept=socket,host=localhost,port=8100;urp;StarOffice.ServiceManager" -nologo -headless -nofirststartwizard &
