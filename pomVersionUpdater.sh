#!/bin/bash
Echo "Replace with correct version in pom.xml"
Echo "Replacing $1 with $2"

find . -name "pom.xml" -type f -exec sed -i '' "s/\<version\>$1\<\/version\>/\<version\>$2\<\/version\>/g" {} \;
#find . -name "pom.xml" -type f -exec sed -i '' "s/\<groupId\>org.alfresco\<\/groupId\>/\<groupId\>se.loftux.alfresco\<\/groupId\>/g" {} \;
#Replace in mvnLoftuxWrapper.sh
sed -i '' "s/$1/$2/g" mvnLoftuxWrapper.sh
