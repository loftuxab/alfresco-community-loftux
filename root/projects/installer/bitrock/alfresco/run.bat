@echo off
rem #Check if program argument is start or stop and call the subroutine


if ""%1"" == ""start"" goto start
if ""%1"" == ""restart"" goto restart
if ""%1"" == ""stop"" goto stop


:start
echo MySQL server....Please do not close this window
cd "@@BITROCK_ALFRESCO_INSTALLDIR@@"
call alf_start.bat
"@@BITROCK_ALFRESCO_MYSQLDIR@@\bin\mysqld.exe" --defaults-file="@@BITROCK_ALFRESCO_MYSQLDIR@@\my.ini"
goto end


:stop
"@@BITROCK_ALFRESCO_MYSQLDIR@@\bin\mysqladmin.exe" --defaults-file="@@BITROCK_ALFRESCO_MYSQLDIR@@\my.ini" shutdown
cd "@@BITROCK_ALFRESCO_INSTALLDIR@@"
call alf_stop.bat
if ""%1"" == ""stop"" goto end
ping localhost -n 4 >NUL
if ""%1"" == ""restart"" goto start

:restart
Echo Stopping MySQL server...
goto stop
goto end


:end
