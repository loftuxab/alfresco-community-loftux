#!/bin/sh
# Start or stop Alfresco server
# Set the following to where Tomcat is installed
ALF_HOME=.
cd "$ALF_HOME"
APPSERVER="${ALF_HOME}/tomcat"
# Set any default JVM values
export JAVA_OPTS='-Xms512m -Xmx1024m -Xss1024k -XX:MaxPermSize=256m -XX:NewSize=256m -server'
export JAVA_OPTS="${JAVA_OPTS} -Dalfresco.home=${ALF_HOME} -Dcom.sun.management.jmxremote"
#
if [ "$1" = "start" ]; then
  "${APPSERVER}/bin/startup.sh"
#  if [ -r ./virtual_start.sh ]; then
#    sh ./virtual_start.sh
#  fi
#  if [ -r ./start_oo.sh ]; then
#    sh ./start_oo.sh
#  fi
elif [ "$1" = "stop" ]; then
  "${APPSERVER}/bin/shutdown.sh"
#  if [ -r ./virtual_start.sh ]; then
#    sh ./virtual_stop.sh
#  fi
#  if [ -r ./start_oo.sh ]; then
#    killall soffice.bin
#  fi
fi
