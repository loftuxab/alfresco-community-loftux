@echo off
rem ---------------------------------------------------------------------------
rem Start script for the OpenOffice transform service
rem ---------------------------------------------------------------------------

echo Starting OpenOffice service...
"C:\Program Files\OpenOffice.org1.1.5\program\soffice" "-accept=socket,host=localhost,port=8100;urp;StarOffice.ServiceManager" -nologo -headless


