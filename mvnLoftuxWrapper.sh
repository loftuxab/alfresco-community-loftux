#!/bin/bash


#Set the name and build number
versionedition="Community by Loftux AB"
buildnumber="5.0.b.LX70"

# SCM Revision number -Fetch automatically

scmpath=`git config --get remote.origin.url`
scmrevision=`git describe --tags --always HEAD`
echo
echo "Loftux Maven Wrapper. Helper script for building Alfresco with maven."
echo
echo "version-edition: $versionedition"
echo "build-number: $buildnumber"
echo "scm-revision: $scmrevision"
echo "scm-path: $scmpath"
echo "maven command: $1"
echo
echo "Press control-c to stop this script."
echo "Press any other key to continue."
read KEY
echo "Starting build..."

mvn clean source:jar javadoc:jar $1 -Dversion-edition="$versionedition" -Dbuild-number="$buildnumber" -Dscm-revision="$scmrevision" -Dscm-path="$scmpath"
