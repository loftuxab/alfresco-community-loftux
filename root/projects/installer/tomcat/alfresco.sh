#!/bin/sh
# Start or stop Alfresco server
# Set the following to where Tomcat is installed
APPSERVER=/opt/alfresco/tomcat
# Set any default JVM values
export JAVA_OPTS='-Xms128m -Xmx512m -server -XX:CompileCommand=exclude,org/apache/lucene/index/IndexReader\$1,doBody'
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
