@echo off
rem #Check if program argument is start or stop and call the subroutine


if ""%1"" == ""start"" goto start
if ""%1"" == ""restart"" goto restart
if ""%1"" == ""stop"" goto stop


:start
echo MySQL server....Please do not close this window
cd "@@BITROCK_ALFRESCO_INSTALLDIR@@\bin"
start "Alfresco Server" cmd /c alfresco.bat start
"@@BITROCK_ALFRESCO_MYSQLDIR@@\bin\mysqld.exe" --defaults-file="@@BITROCK_ALFRESCO_MYSQLDIR@@\my.ini"
goto end


:stop
cd "@@BITROCK_ALFRESCO_INSTALLDIR@@\bin"
call alfresco.bat stop
"@@BITROCK_ALFRESCO_MYSQLDIR@@\bin\mysqladmin.exe" --defaults-file="@@BITROCK_ALFRESCO_MYSQLDIR@@\my.ini" shutdown
if ""%1"" == ""stop"" goto end
ping localhost -n 4 >NUL
if ""%1"" == ""restart"" goto start

:restart
Echo Stopping MySQL server...
goto stop
goto end


:end
