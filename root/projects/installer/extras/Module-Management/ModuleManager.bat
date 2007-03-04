@echo off
rem Install an AMP file into an Alfresco WAR

rem set the LIB_DIR value to point to the Alfresco lib directory
SET LIB_DIR=C:\work\jakarta-tomcat-5.5.9\webapps\alfresco\WEB-INF\lib

SET TEMP_CLASS_PATH=%LIB_DIR%\alfresco-repository.jar;%LIB_DIR%\alfresco-core.jar;%LIB_DIR%\truezip.jar;%LIB_DIR%\log4j-1.2.8.jar;%LIB_DIR%\spring.jar;%LIB_DIR%\commons-logging.jar

java -cp "%TEMP_CLASS_PATH%" org.alfresco.repo.module.tool.ModuleManagementTool "%1" "%2" "%3" "%4" "%5" "%6" "%7"
