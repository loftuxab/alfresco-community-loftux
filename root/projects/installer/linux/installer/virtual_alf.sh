#!/bin/sh
# Start or stop Alfresco server
# Set the following to where Tomcat is installed
ALF_HOME=@@ALF_HOME@@
cd "$ALF_HOME"
APPSERVER="$ALF_HOME"/virtual-tomcat
export JAVA_HOME="@@JAVA_HOME@@"

# Start virtual-tomcat
#
if [ "$1" = "start" ]; then
  "$APPSERVER"/bin/startup.sh
elif [ "$1" = "stop" ]; then
  "$APPSERVER"/bin/shutdown.sh
fi
