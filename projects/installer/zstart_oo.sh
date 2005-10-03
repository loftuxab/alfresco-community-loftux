#!/bin/sh
# ---------------------------------------------------------------------------
# Start script for the OpenOffice transform service
# ---------------------------------------------------------------------------
OPENOFFICE=~qall/OpenOffice.org1.1.4/program
echo "Starting OpenOffice service..."
# Comment or uncomment the appropriate location using #
# Assumes OpenOffice is installed in /opt
/home/qall/OpenOffice.org1.1.4/program/soffice "-accept=socket,host=localhost,port=8100;urp;StarOffice.ServiceManager" -nologo -headless &
# If installed in user's home
#~/soffice "-accept=socket,host=localhost,port=8100;urp;StarOffice.ServiceManager" -nologo -headless &
# If NeoOffice on Mac OS X
#/Applications/NeoOfficeJ.app/Contents/program/soffice "-accept=socket,host=localhost,port=8100;urp;StarOffice.ServiceManager" -nologo -headless &
