@echo off
rem ---------------------------------------------------------------------------
rem Start script for the OpenOffice transform service
rem ---------------------------------------------------------------------------

echo Stopping OpenOffice service...

taskkill /f /im soffice.exe

