@echo off
rem -- Check if argument is INSTALL or REMOVE

set JAVA_HOME=@@BITROCK_ALFRESCO_INSTALLDIR@@\java
cd @@BITROCK_ALFRESCO_INSTALLDIR@@\tomcat\bin\

if not ""%1"" == ""INSTALL"" goto remove

"@@BITROCK_ALFRESCO_MYSQLDIR@@\bin\mysqld.exe" --install alfrescomysql --defaults-file="@@BITROCK_ALFRESCO_MYSQLDIR@@\my.ini"
call "@@BITROCK_ALFRESCO_INSTALLDIR@@\tomcat\bin\service.bat" install alfrescoTomcat
rem net start alfrescomysql >NUL
rem net start alfrescoTomcat >NUL
goto end

:remove
rem -- STOP SERVICES BEFORE REMOVING

net stop alfrescoTomcat >NUL
call "@@BITROCK_ALFRESCO_INSTALLDIR@@\tomcat\bin\service.bat" remove alfrescoTomcat

net stop alfrescomysql >NUL
"@@BITROCK_ALFRESCO_MYSQLDIR@@\bin\mysqld.exe" --remove alfrescomysql

:end
