#!/bin/sh
# -------
# Script for apply AMPs to installed WAR
# -------
export CATALINA_HOME=tomcat
echo "This script will apply all the AMPs in ./amps to the alfresco.war and share.war files in $CATALINA_HOME/webapps"
echo "Press control-c to stop this script . . ."
echo "Press any other key to continue . . ."
read RESP
java -jar bin/alfresco-mmt.jar install ./amps $CATALINA_HOME/webapps/alfresco.war -directory
java -jar bin/alfresco-mmt.jar list $CATALINA_HOME/webapps/alfresco.war
java -jar bin/alfresco-mmt.jar install ./amps-share $CATALINA_HOME/webapps/share.war -directory
java -jar bin/alfresco-mmt.jar list $CATALINA_HOME/webapps/share.war
echo "About to clean out tomcat/webapps/alfresco and share directories and temporary files..."
echo "Press control-c to stop this script . . ."
echo "Press any other key to continue . . ."
read DUMMY
rm -rf $CATALINA_HOME/webapps/alfresco
rm -rf $CATALINA_HOME/webapps/share
sh ./bin/clean_tomcat.sh
