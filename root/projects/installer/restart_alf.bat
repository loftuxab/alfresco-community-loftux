@echo off
call %~dp0alf_stop.bat
echo Waiting 10 seconds for shutdown before starting again...
rem The following are required because the sleep command is not available on all platforms by default
ping 127.0.0.1 -n 2 -w 1000 > nul
ping 127.0.0.1 -n 10 -w 1000 > nul
call %~dp0alf_start.bat
