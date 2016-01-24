#!/bin/bash

#Set the name and build number
versionedition="Community by Loftux AB"
buildnumber="LX90-SNAPSHOT"
buildAlfresco="5.1-SNAPSHOT"

# SCM Revision number -Fetch automatically

scmpath=`git config --get remote.origin.url`
scmrevision=`git log --pretty=format:'%h' -n 1`

echo
echo "Loftux Maven Wrapper. Helper script for building Alfresco with maven."
echo
echo "version-edition: $versionedition"
echo "build-number: $buildnumber"
echo "build-Alfresco: $buildAlfresco"
echo "scm-revision: $scmrevision"
echo "scm-path: $scmpath"
echo "maven command: $1"
echo
echo "Java Environment"
echo $JAVA_HOME
echo $JAVA_OPTS
echo
echo "Press control-c to stop this script."
echo "Press any other key to continue."
read KEY

install() {
    echo "Starting local install build..."
    mvn clean source:jar $1 -Dversion-edition="$versionedition" -Dbuild-number="$buildnumber" -Dscm-revision="$scmrevision" -Dscm-path="$scmpath"
}

deploy() {
    echo
    echo "Starting deploy build..."
    echo

    if [[ $buildnumber == *"SNAPSHOT"* ]]
    then
        echo "Deploy to SNAPSHOT"
        mvn clean deploy source:jar -Penv=production -DaltDeploymentRepository=loftux-snapshots::default::http://artifacts.loftux.net/nexus/content/repositories/snapshots -Dversion-edition="$versionedition" -Dbuild-number="$buildnumber" -Dscm-revision="$scmrevision" -Dscm-path="$scmpath"
    else
        echo "Deploy to Release"
        mvn clean deploy source:jar -Penv=production -DaltDeploymentRepository=loftux-releases::default::http://artifacts.loftux.net/nexus/content/repositories/releases -Dversion-edition="$versionedition" -Dbuild-number="$buildnumber" -Dscm-revision="$scmrevision" -Dscm-path="$scmpath"
    fi

}

setup () {

    echo
    echo "Updating with Loftux build Number in pom.xml"
    echo "Replacing $buildAlfresco with $buildnumber"
    echo

    find . -name "pom.xml" -type f -exec sed -i '' "s/\<version\>$buildAlfresco\<\/version\>/\<version\>$buildnumber\<\/version\>/g" {} \;
    find . -name "pom.xml" -type f -exec sed -i '' "s/\<alfresco.platform.version\>$buildAlfresco\<\/alfresco.platform.version\>/\<alfresco.platform.version\>$buildnumber\<\/alfresco.platform.version\>/g" {} \;
}

cleanup() {

    echo
    echo "Restoring Loftux build Number to Alfresco build in pom.xml"
    echo "Replacing $buildnumber with $buildAlfresco "
    echo

    find . -name "pom.xml" -type f -exec sed -i '' "s/\<version\>$buildnumber\<\/version\>/\<version\>$buildAlfresco\<\/version\>/g" {} \;
    find . -name "pom.xml" -type f -exec sed -i '' "s/\<alfresco.platform.version\>$buildnumber\<\/alfresco.platform.version\>/\<alfresco.platform.version\>$buildAlfresco\<\/alfresco.platform.version\>/g" {} \;

}

case "$1" in
  install)
        setup
        install
        cleanup
        ;;
  deploy)
        setup
        deploy
        cleanup
        ;;
  *)
        echo "You need to supply an argument: $0 {install|deploy}"
        exit 1
esac

exit $RETVAL
