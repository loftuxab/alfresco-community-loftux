@echo off
rem ---------------------------------------------------------------------------
rem Start script for the OpenOffice transform service
rem ---------------------------------------------------------------------------

echo Starting OpenOffice service...
"@@BITROCK_ALFRESCO_INSTALLDIR@@\openoffice\openoffice\program\soffice" "-accept=socket,host=localhost,port=8100;urp;StarOffice.ServiceManager" -nologo -headless


