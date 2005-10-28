@echo off
rem ---------------------------------------------------------------------------
rem Start script for the OpenOffice transform service
rem ---------------------------------------------------------------------------

echo Starting OpenOffice service...
"%OPENOFFICE_PATH%\soffice" "-accept=socket,host=localhost,port=8100;urp;StarOffice.ServiceManager" -nologo -headless


