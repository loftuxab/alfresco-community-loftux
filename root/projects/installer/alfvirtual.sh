#!/bin/sh
# Start or stop Alfresco server
# Set the following to where Tomcat is installed
APPSERVER=/opt/alfresco/virtual-tomcat
# Set any default JVM values
#
if [ "$1" = "start" ]; then
  "$APPSERVER"/bin/startup.sh
  if [ -r ./start_oo.sh ]; then
    sh ./start_oo.sh
  fi
elif [ "$1" = "stop" ]; then
  "$APPSERVER"/bin/shutdown.sh
  if [ -r ./start_oo.sh ]; then
    killall soffice.bin
  fi
fi
