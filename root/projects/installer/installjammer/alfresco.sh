#!/bin/sh
# Start or stop Alfresco server
# Set the following to where Tomcat is installed
ALF_HOME=@@ALFRESCO_DIR@@
cd "$ALF_HOME"
APPSERVER="$ALF_HOME"/tomcat
export JAVA_HOME="$ALF_HOME"/java
# Set any default JVM values
export JAVA_OPTS='-Xms128m -Xmx512m -server -XX:CompileCommand=exclude,org/apache/lucene/index/IndexReader$1,doBody'
#
if [ "$1" = "start" ]; then
  "$APPSERVER"/bin/startup.sh
  if [ -r "$ALF_HOME"/start_oo.sh ]; then
    sh "$ALF_HOME"/start_oo.sh
  fi
elif [ "$1" = "stop" ]; then
  "$APPSERVER"/bin/shutdown.sh
  if [ -r "$ALF_HOME"/start_oo.sh ]; then
    killall soffice.bin
  fi
fi
