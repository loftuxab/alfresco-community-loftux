#!/bin/sh
# Start or stop Alfresco server
# Set the following to where Tomcat is installed
APPSERVER=/opt/alfresco/virtual-tomcat
# Set any default JVM values
#
if [ "$1" = "start" ]; then
  "$APPSERVER"/bin/startup.sh
elif [ "$1" = "stop" ]; then
  "$APPSERVER"/bin/shutdown.sh
fi
