#!/bin/sh
export JAVA_OPTS='-Xms128m -Xmx512m -XX:MaxPermSize=128m -server'
sh "@@ALFRESCO_DIR@@/tomcat/bin/startup.sh"
while [ ! -s "@@ALFRESCO_DIR@@/tomcat/logs/catalina.out" ]
  do
  printf "%10s \r" "waiting for log to be created..."
  sleep 1
done
tail -f "@@ALFRESCO_DIR@@/tomcat/logs/catalina.out"
