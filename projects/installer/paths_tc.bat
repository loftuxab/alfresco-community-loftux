@echo off
rem -------------------------------------------------------------------------------
rem Set the variables to the locations where the different components are installed
rem -------------------------------------------------------------------------------

rem -- Tomcat --
set CATALINA_HOME=C:\alfresco\tomcat

rem -- MySQL --
set MYSQL_HOME=C:\Program Files\MySQL\MySQL Server 4.1

rem -- Java 1.5 --
set JAVA_HOME=C:\Program Files\Java\jdk1.5.0_04

set PATH=%JAVA_HOME%/bin:%PATH%
