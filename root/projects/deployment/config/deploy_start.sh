#!/bin/sh
nohup java -server -Djava.ext.dirs=. org.alfresco.deployment.Main application-context.xml >>deployment.log 2>&1 &
