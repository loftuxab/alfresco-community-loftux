#!/bin/sh
# Start or stop Alfresco server
# Set the following to where Tomcat is installed
ALF_HOME=@@ALFRESCO_DIR@@
cd "$ALF_HOME"
APPSERVER="$ALF_HOME"/tomcat
export JAVA_HOME="$ALF_HOME"/java
# Set any default JVM values
export JAVA_OPTS='-Xms128m -Xmx512m -server'
# Following only needed for Sun JVMs before to 1.5 update 8
export JAVA_OPTS="${JAVA_OPTS} -XX:CompileCommand=exclude,org/apache/lucene/index/IndexReader\$1,doBody -XX:CompileCommand=exclude,org/alfresco/repo/search/impl/lucene/index/IndexInfo\$Merger,mergeIndexes -XX:CompileCommand=exclude,org/alfresco/repo/search/impl/lucene/index/IndexInfo\$Merger,mergeDeletions"
#
if [ "$1" = "start" ]; then
  "$APPSERVER"/bin/startup.sh
#  if [ -r ./virtual_start.sh ]; then
#    sh ./virtual_start.sh
#  fi
  if [ -r "$ALF_HOME"/start_oo.sh ]; then
    sh "$ALF_HOME"/start_oo.sh
  fi
elif [ "$1" = "stop" ]; then
  "$APPSERVER"/bin/shutdown.sh
#  if [ -r ./virtual_start.sh ]; then
#    sh ./virtual_stop.sh
#  fi
  if [ -r "$ALF_HOME"/start_oo.sh ]; then
    killall soffice.bin
  fi
fi
