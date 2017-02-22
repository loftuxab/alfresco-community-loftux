#!/bin/bash

#Set the name and build number
versionedition="Community by Loftux AB"
buildnumber="LX96"
buildAlfresco="5.2-SNAPSHOT"

# Add url in format id::layout::url, see http://maven.apache.org/plugins/maven-deploy-plugin/deploy-mojo.html
snapshotID=""
snapshotURL=""

releaseID=""
releaseURL=""

layout="default"
snapshotRepo="$snapshotID::$layout::$snapshotURL"
releaseRepo="$releaseID::$layout::$releaseURL"

# SCM Revision number -Fetch automatically

scmpath=`git config --get remote.origin.url`
scmrevision=`git log --pretty=format:'%h' -n 1`

# Set values normally set by Alfresco bamboo build system.
bamboo_planName="mvnLoftuxWrapper"
bamboo_fullBuildKey="$buildnumber $buildAlfresco"
bamboo_buildNumber=1
bamboo_repository_revision_number=$scmrevision
bamboo_custom_svn_lastchange_revision_number=$scmrevision
bamboo_planRepository_repositoryUrl=$scmpath

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
    mvn clean source:jar install -Dversion-edition="$versionedition" -Dbuild-number="$buildnumber" -Dscm-revision="$scmrevision" -Dscm-path="$scmpath" \
    -Dbamboo_planName="$bamboo_planName" -Dbamboo_fullBuildKey="$bamboo_fullBuildKey" -Dbamboo_buildNumber="$bamboo_buildNumber" -Dbamboo_repository_revision_number="$bamboo_repository_revision_number" \
    -Dbamboo_custom_svn_lastchange_revision_number="$bamboo_custom_svn_lastchange_revision_number" -D=bamboo_planRepository_repositoryUrl="$bamboo_planRepository_repositoryUrl"
}

deploy() {
    echo
    echo "Starting deploy build..."
    echo

    if [[ $buildnumber == *"SNAPSHOT"* ]]
    then
        if [ -n "$snapshotURL" ]; then
            echo "Deploy to SNAPSHOT $snapshotRepo"
            echo
            mvn clean source:jar deploy -Penv=production -DaltSnapshotDeploymentRepository="$snapshotRepo" \
            -Dmaven.distributionManagement.snapshot.url="$snapshotURL" -Dmaven.distributionManagement.snapshot.id="$snapshotID" \
            -Dversion-edition="$versionedition" -Dbuild-number="$buildnumber" -Dscm-revision="$scmrevision" -Dscm-path="$scmpath" \
            -Dbamboo_planName="$bamboo_planName" -Dbamboo_fullBuildKey="$bamboo_fullBuildKey" -Dbamboo_buildNumber="$bamboo_buildNumber" -Dbamboo_repository_revision_number="$bamboo_repository_revision_number" \
            -Dbamboo_custom_svn_lastchange_revision_number="$bamboo_custom_svn_lastchange_revision_number" -D=bamboo_planRepository_repositoryUrl="$bamboo_planRepository_repositoryUrl"        
        else
            echo "You must set the snapshotRepo url for a SNAPSHOT deployment"
        fi
    else
        if [ -n "$releaseURL" ]; then
            echo "Deploy to RELEASE $releaseRepo"
            echo
            mvn clean source:jar deploy -Penv=production -DaltReleaseDeploymentRepository="$releaseRepo" \
            -Dmaven.distributionManagement.release.url="$releaseURL" -Dmaven.distributionManagement.release.id="$releaseID" \
            -Dversion-edition="$versionedition" -Dbuild-number="$buildnumber" -Dscm-revision="$scmrevision" -Dscm-path="$scmpath" \
            -Dbamboo_planName="$bamboo_planName" -Dbamboo_fullBuildKey="$bamboo_fullBuildKey" -Dbamboo_buildNumber="$bamboo_buildNumber" -Dbamboo_repository_revision_number="$bamboo_repository_revision_number" \
            -Dbamboo_custom_svn_lastchange_revision_number="$bamboo_custom_svn_lastchange_revision_number" -D=bamboo_planRepository_repositoryUrl="$bamboo_planRepository_repositoryUrl"
        else
            echo "You must set the releaseRepo url for a RELEASE deployment"
        fi
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

    echo
    echo "Restore orginal Alfresco build number completed"
    echo

    read -e -p "Do you want to run mvn clean [y/n] (default: y)" runclean
    if [ "$runclean" != "n" ]; then
        mvn clean
    fi

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
