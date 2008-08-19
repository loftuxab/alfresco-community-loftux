#!/bin/sh
# -------
# Script for apply AMPs to installed WAR
# -------
ALF_HOME=@@ALFRESCO_DIR@@
export JAVA_HOME="@@JAVA_HOME@@"
cd "$ALF_HOME"
APPSERVER="$ALF_HOME"/tomcat
echo "This script will apply all the AMPs in ./amps to the alfresco.war file in $APPSERVER\webapps"
echo "Press control-c to stop this script . . ."
read -p "Press any other key to continue . . ."
"$JAVA_HOME/bin/java" -jar bin/alfresco-mmt.jar install ./amps $APPSERVER/webapps/alfresco.war -directory
"$JAVA_HOME/bin/java" -jar bin/alfresco-mmt.jar list $APPSERVER/webapps/alfresco.war
echo "About to clean out tomcat/webapps/alfresco directory and temporary files..."
echo "Press control-c to stop this script . . ."
read -p "Press any other key to continue . . ."
rm -rf $APPSERVER/webapps/alfresco
sh $ALF_HOME/bin/clean_tomcat.sh
