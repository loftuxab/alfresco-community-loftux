#!/bin/bash
set -e

[ "$DEBUG" ] && set -x

# set current working directory to the directory of the script
cd "$(dirname "$0")"

nicebranch=`echo "$bamboo_planRepository_1_branch" | sed 's/\//_/'`
dockerImage="docker-internal.alfresco.com/platform:$bamboo_maven_version"
echo "Building $dockerImage from $nicebranch using version $bamboo_maven_version"

docker build --build-arg alfVer=5.2 --build-arg alfInstallerVer=$bamboo_maven_version -t $dockerImage target
echo "Publishing $dockerImage..."
docker push "$dockerImage"
echo "Docker SUCCESS"

#Locally you can do
#export bamboo_planRepository_1_branch=local
#export bamboo_maven_version=9.9.9-SNAPSHOT