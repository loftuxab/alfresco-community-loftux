@echo off
rem ---------------------------------------------------------------------------
rem Start script for the Alfresco Server
rem ---------------------------------------------------------------------------

rem set Alfresco home (includes trailing \ e.g. c:\alfresco\)
set ALF_HOME=%~dp0

set JBOSS_HOME=%ALF_HOME%jboss

rem Set any default JVM options
set JAVA_OPTS=-Xms128m -Xmx512m -Xss64k -server -XX:+CMSPermGenSweepingEnabled -XX:MaxPermSize=128m
rem The following options are only required for Sun JVMs prior to 1.5 update 8
set JAVA_OPTS=%JAVA_OPTS% -XX:CompileCommand=exclude,org/apache/lucene/index/IndexReader$1,doBody -XX:CompileCommand=exclude,org/alfresco/repo/search/impl/lucene/index/IndexInfo$Merger,mergeIndexes -XX:CompileCommand=exclude,org/alfresco/repo/search/impl/lucene/index/IndexInfo$Merger,mergeDeletions

rem --- If SetPaths.bat already exists - assume set by hand and use as is
if not exist "SetPaths.bat" goto getpaths 
call SetPaths.bat
goto start

:getpaths
call RegPaths.exe
call SetPaths.bat
del SetPaths.bat

:start
set PATH=%JAVA_HOME%/bin;%ALF_HOME%bin;%PATH%
rem ---------------------------------------
rem Start Components
rem ---------------------------------------

if not ""%1"" == ""start"" goto nostart

rem ---------------------------------------
rem Start JBoss
rem ---------------------------------------

echo Starting JBoss...
start "JBoss Server" cmd /c %JBOSS_HOME%\bin\run.bat

rem ---------------------------------------
rem Start OpenOffice for transformations
rem ---------------------------------------
if not "%OPENOFFICE_PATH%" == "" call "start_oo.bat"

goto nostop
:nostart

rem ---------------------------------------
rem Stop Components
rem ---------------------------------------


if not ""%1"" == ""stop"" goto nostop

echo Shutting down JBoss...
call %JBOSS_HOME%\bin\shutdown.bat -S

rem ---------------------------------------
rem Stop OpenOffice for transformations
rem ---------------------------------------
if not "%OPENOFFICE_PATH%" == "" c:\windows\system32\taskkill /f /im soffice.bin

:nostop