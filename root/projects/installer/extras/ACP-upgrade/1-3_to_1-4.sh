#!/bin/sh
# Convert ACP XML files from 1.3 format to 1.4
# Expects input and output args
if [ "$2" = "" ]; then
   echo "The ACP XML Conversion utility needs input and output files specified:"
   echo "1-3_to_1-4 <13inputFile.xml> <14outputfile.xml>"
else
   # set the LIB_DIR value to point to the Alfresco lib directory
   SET LIB_DIR='../../tomcat/webapps/alfresco/WEB-INF/lib'
   SET TEMP_CLASS_PATH=%LIB_DIR%/repository.jar;%LIB_DIR%/core.jar;%LIB_DIR%/dom4j-1.6.1.jar;%LIB_DIR%/xpp3-1.1.3_8.jar;%LIB_DIR%/jug.jar
   java -cp %TEMP_CLASS_PATH% org.alfresco.repo.admin.patch.util.ImportFileUpdater $1 $2
fi
