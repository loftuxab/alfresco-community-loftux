@echo off
rem STARTMENU : START - STOP - RESTART of mysql and tomcat
rem #Check if program argument is start or stop and call the subroutine


if ""%1"" == ""mysql"" goto mysql
if ""%1"" == ""tomcat"" goto tomcat

rem ---------------------------------
rem MYSQL PART
rem ---------------------------------

:mysql
if ""%2"" == ""stop"" goto mysql_stop
if ""%2"" == ""restart"" goto mysql_restart
goto mysql_start

:mysql_start

Echo MySQL server started....Please do not close this window
"@@BITROCK_ALFRESCO_MYSQLDIR@@\bin\mysqld.exe" --defaults-file="@@BITROCK_ALFRESCO_MYSQLDIR@@\my.ini"
goto end

:mysql_stop
echo MySQL server shutting down...
"@@BITROCK_ALFRESCO_MYSQLDIR@@\bin\mysqladmin.exe" --defaults-file="@@BITROCK_ALFRESCO_MYSQLDIR@@\my.ini" shutdown
if ""%2"" == ""stop"" goto end
ping localhost -n 4 >NUL 
if ""%2"" == ""restart"" goto mysql_start

:mysql_restart
echo MySQL server restarting...
goto mysql_stop
goto end

rem -----------------------------------
rem TOMCAT PART
rem -----------------------------------



:tomcat
cd "@@BITROCK_ALFRESCO_INSTALLDIR@@"
if ""%2"" == ""stop"" goto tomcat_stop
if ""%2"" == ""restart"" goto tomcat_restart
goto tomcat_start

:tomcat_start

call "@@BITROCK_ALFRESCO_INSTALLDIR@@/alf_start.bat"
if ""%2"" == ""start"" goto end
goto end

:tomcat_stop
call "@@BITROCK_ALFRESCO_INSTALLDIR@@/alf_stop.bat"
if ""%2"" == ""stop"" goto end
ping localhost -n 4 >NUL 
if ""%2"" == ""restart"" goto tomcat_start

:tomcat_restart
echo Tomcat server restarting...
goto tomcat_stop
goto end

:end
