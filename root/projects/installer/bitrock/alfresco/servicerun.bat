@echo off
rem START or STOP Alfresco Services (MYSQL / TOMCAT)
rem ------------------------------------------------
rem Check if argument is STOP or START

if not ""%1"" == ""START"" goto stop

rem "@@BITROCK_ALFRESCO_INSTALLDIR@@\mysql\bin\mysqld.exe" --install alfrescomysql --defaults-file="@@BITROCK_ALFRESCO_INSTALLDIR@@/mysql/my.ini"

net start alfrescomysql >NUL

set JAVA_HOME=@@BITROCK_ALFRESCO_INSTALLDIR@@\java
rem cd @@BITROCK_ALFRESCO_INSTALLDIR@@\tomcat\bin\
rem call "@@BITROCK_ALFRESCO_INSTALLDIR@@\tomcat\bin\service.bat" install alfrescoTomcat

net start "alfrescoTomcat" >NUL
goto end

:stop
net stop "alfrescoTomcat" >NUL
net stop alfrescomysql >NUL

:end
