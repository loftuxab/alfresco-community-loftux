@echo off
rem START or STOP Alfresco Services (MYSQL / TOMCAT)
rem ------------------------------------------------
rem Check if argument is STOP or START

if not ""%1"" == ""START"" goto stop

"@@BITROCK_ALFRESCO_INSTALLDIR@@\mysql\bin\mysqld.exe" --install alfrescomysql --defaults-file="@@BITROCK_ALFRESCO_INSTALLDIR@@/mysql/my.ini"
net start alfrescomysql
cd "@@BITROCK_ALFRESCO_INSTALLDIR@@"
call "@@BITROCK_ALFRESCO_INSTALLDIR@@\tomcat\bin\service.bat" install alfrescoTomcat
net start "alfrescoTomcat"
goto end

:stop
net stop alfrescomysql
net stop "alfrescoTomcat"

:end
