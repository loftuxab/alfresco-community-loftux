#!/bin/sh
java -server -cp alfresco-deployment.jar:spring-2.0.2.jar:commons-logging-1.0.4.jar:alfresco-core.jar:jug.jar:. org.alfresco.deployment.Main shutdown-context.xml
