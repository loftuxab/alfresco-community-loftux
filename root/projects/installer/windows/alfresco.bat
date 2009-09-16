rem @echo off
rem ---------------------------------------------------------------------------
rem Start script for the Alfresco Server
rem ---------------------------------------------------------------------------

rem set Alfresco home (includes trailing \  e.g. c:\alfresco\)
set ALF_HOME=%~dp0
set CATALINA_HOME=%ALF_HOME%tomcat

rem Set any default JVM options
set JAVA_OPTS=-Xms128m -Xmx512m -Xss96k -XX:MaxPermSize=160m -server -Dalfresco.home=%ALF_HOME% -Dcom.sun.management.jmxremote

rem --- If SetPaths.bat already exists - assume set by hand and use as is
set PATH=%ALF_HOME%bin;%PATH%
if not exist "SetPaths.bat" goto getpaths 
call SetPaths.bat
goto start

:getpaths
call RegPaths.exe
call SetPaths.bat
del SetPaths.bat

:start
rem --- Test for Java settings
set BASEDIR=%CATALINA_HOME%
call "%CATALINA_HOME%\bin\setclasspath.bat"
if errorlevel 1 goto error
set PATH=%JAVA_HOME%\bin;%PATH%
rem ---------------------------------------
rem Start Components
rem ---------------------------------------

if not ""%1"" == ""start"" goto stop

if not exist "%ALF_HOME%mysql\my.ini" goto tomcat
rem ---------------------------------------
rem Start MySQL
rem ---------------------------------------
echo Starting MySQL...
start "MySQL" "%ALF_HOME%mysql\bin\mysqld" --defaults-file="%ALF_HOME%mysql\my.ini" --basedir="%ALF_HOME%mysql" --datadir="%ALF_HOME%alf_data\mysql" --console

rem Uncomment below to pause for 5 seconds before starting Tomcat
rem Change 5000 to 1000 x the number of seconds delay required
rem ping 1.0.0.0 -n 1 -w 5000 >NUL

:tomcat
rem ---------------------------------------
rem Start Tomcat
rem ---------------------------------------

echo Starting Tomcat...
call "%CATALINA_HOME%\bin\startup.bat"

rem ---------------------------------
rem Start Virtualization if available
rem ---------------------------------
rem if exist "~dp0virtual_start.bat" call "~dp0virtual_start.bat" 

goto end

:stop

rem ---------------------------------------
rem Stop Components
rem ---------------------------------------

if not ""%1"" == ""stop"" goto nostop

echo Shutting down Tomcat...
call "%CATALINA_HOME%\bin\shutdown.bat" 

if not exist "%ALF_HOME%mysql\my.ini" goto nextstop
set /P pause="Please wait until Tomcat has shut down, then press ENTER to continue..."
echo Stopping MySQL...
call "%ALF_HOME%mysql\bin\mysqladmin" -u root shutdown

:nextstop
rem if exist "virtual_start.bat" call virtual_stop.bat 

goto end

:error
set /P pause="Press ENTER to continue..."

:end