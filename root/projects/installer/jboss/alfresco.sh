#!/bin/sh
# Start or stop Alfresco server
# Set the following to where JBoss is installed
APPSERVER=/opt/alfresco/jboss
# Set any default JVM values
export JAVA_OPTS="-Xms128m -Xmx512m -server"
#
if [ "$1" = "start" ]; then
  "$APPSERVER"/bin/run.sh
  if [ -r ./start_oo.sh ]; then
    sh ./start_oo.sh
  fi
elif [ "$1" = "stop" ]; then
  "$APPSERVER"/bin/shutdown.sh -S
  if [ -r ./start_oo.sh ]; then
    killall soffice.bin
  fi
fi
