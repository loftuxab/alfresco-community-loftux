#!/bin/bash

#
# Simple redeploy shell-script for AWS mini-dev env
#

export BAMBOO_USERNAME=changme_ldap_un

export DEPLOY_SHARE=true
export DEPLOY_ALFRESCO=true

export DEPLOY_SOLR=false
export SOLR_ALF_HOST=localhost

export BUILD=build-$1

rm -rf /mnt/vol1/software/$BUILD

mkdir /mnt/vol1/software/$BUILD
cd /mnt/vol1/software/$BUILD

export ARTIFACTS=

if [ "$DEPLOY_SHARE" = "true" ]; then
   export ARTIFACTS="$ARTIFACTS https://bamboo.alfresco.com/bamboo/artifact/THOR-THOR1/JOB1/$BUILD/Share_WARS/share.war"
fi

if [ "$DEPLOY_ALFRESCO" = "true" ]; then
   export ARTIFACTS="$ARTIFACTS https://bamboo.alfresco.com/bamboo/artifact/THOR-THOR1/JOB1/$BUILD/Alfresco_WARS/alfresco.war https://bamboo.alfresco.com/bamboo/artifact/THOR-THOR1/JOB1/$BUILD/aws-context.xml.sample/aws-context.xml.sample" 
fi

if [ "$DEPLOY_SOLR" = "true" ]; then
   export ARTIFACTS="$ARTIFACTS https://bamboo.alfresco.com/bamboo/artifact/THOR-THOR1/JOB1/$BUILD/Solr-Zip/alfresco-enterprise-solr-4.0.0beta.zip"
fi

wget --user $BAMBOO_USERNAME --ask-password $ARTIFACTS

killall -q -9 java soffice.bin

# ---------------------------------------------

# some cleanup - note: currently leaves any existing logs, caching content store files, solr indexes etc

cd /mnt/vol1/devthor1
rm -rf solr

cd /mnt/vol1/devthor1/tomcat

rm -rf webapps/alfresco webapps/alfresco.war webapps/share webapps/share.war webapps/solr
rm -rf work/*
rm -rf temp/*

# ---------------------------------------------

if [ "$DEPLOY_SHARE" = "true" ]; then
   echo "Deploying Share WAR"

   cp /mnt/vol1/software/$BUILD/share.war webapps
else
   echo "Not deploying Share WAR !"
fi

# ---------------------------------------------

if [ "$DEPLOY_ALFRESCO" = "true" ]; then
   echo "Deploying Alfresco WAR and custom extension"

   cp /mnt/vol1/software/$BUILD/alfresco.war webapps
   mkdir -p /mnt/vol1/devthor1/tomcat/shared/classes/alfresco/extension
   cp /mnt/vol1/software/$BUILD/aws-context.xml.sample shared/classes/alfresco/extension/aws-context.xml
else
   echo "Not deploying Alfresco WAR and custom extension !"
fi

# ---------------------------------------------

if [ "$DEPLOY_SOLR" = "true" ]; then
   echo "Deploying Solr"

   # if it does not already exist then create data directory (for solr indexes)
   mkdir -p /mnt/vol1/devthor1/data
   
   cd /mnt/vol1/devthor1
   unzip /mnt/vol1/software/$BUILD/alfresco-enterprise-solr-4.0.0beta.zip -d solr
   
   cd /mnt/vol1/devthor1/tomcat/webapps
   unzip /mnt/vol1/devthor1/solr/apache-solr-1.4.1.war  -d solr
  
   sed -i 's/data.dir.root=@@ALFRESCO_SOLR_DIR@@/data.dir.root=\/mnt\/vol1\/devthor1\/data/g' /mnt/vol1/devthor1/solr/archive-SpacesStore/conf/solrcore.properties
   sed -i 's/alfresco.host=localhost/alfresco.host=$SOLR_ALF_HOST/g' /mnt/vol1/devthor1/solr/archive-SpacesStore/conf/solrcore.properties

   sed -i 's/data.dir.root=@@ALFRESCO_SOLR_DIR@@/data.dir.root=\/mnt\/vol1\/devthor1\/data/g' /mnt/vol1/devthor1/solr/workspace-SpacesStore/conf/solrcore.properties
   sed -i 's/alfresco.host=localhost/alfresco.host=$SOLR_ALF_HOST/g' /mnt/vol1/devthor1/solr/workspace-SpacesStore/conf/solrcore.properties
   
   cp /mnt/vol1/devthor1/solr/solr-tomcat-context.xml /mnt/vol1/devthor1/tomcat/conf/Catalina/localhost/solr.xml
   sed -i 's/@@ALFRESCO_SOLR_DIR@@/\/mnt\/vol1\/devthor1\/solr/g' /mnt/vol1/devthor1/tomcat/conf/Catalina/localhost/solr.xml
else
   echo "Not deploying Solr !"
fi

# ---------------------------------------------

cd

