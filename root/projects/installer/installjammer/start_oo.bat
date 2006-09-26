@echo off
rem ---------------------------------------------------------------------------
rem Start script for the OpenOffice transform service
rem ---------------------------------------------------------------------------

echo Starting OpenOffice service...
rem "%OPENOFFICE_PATH%\soffice" "-accept=socket,host=localhost,port=8100;urp;StarOffice.ServiceManager" -nologo -headless
"%OPENOFFICE_PATH%\OpenOfficePortable.exe" "-accept=socket,host=localhost,port=8100;urp;StarOffice.ServiceManager" -nologo -headless


