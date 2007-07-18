#!/bin/sh
nohup java -server -cp alfresco-deployment.jar:spring-2.0.2.jar:commons-logging-1.0.4.jar:alfresco-core.jar:jug.jar:. org.alfresco.deployment.Main application-context.xml >deployment.log 2>&1 &
