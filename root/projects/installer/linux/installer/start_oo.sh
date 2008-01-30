#!/bin/sh
# ---------------------------------------------------------------------------
# Start script for the OpenOffice transform service
# ---------------------------------------------------------------------------

echo "Starting OpenOffice service..."
ALF_HOME=`pwd`

# Comment or uncomment the appropriate location using #
"$ALF_HOME"/openoffice.org2.1/program/soffice "-accept=socket,host=localhost,port=8100;urp;StarOffice.ServiceManager" "-env:UserInstallation=file://$ALF_HOME/oouser" -nologo -headless -nofirststartwizard &

# If NeoOffice on Mac OS X
#/Applications/NeoOfficeJ.app/Contents/program/soffice "-accept=socket,host=localhost,port=8100;urp;StarOffice.ServiceManager" -nologo -headless &
