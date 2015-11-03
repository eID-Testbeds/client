#!/bin/bash

timestamp=$(date +"%Y%m%d-%H%M")

ipsmall_pom_file=IPSmallJava/pom.xml

if command -v xmlstarlet >/dev/null 2>&1; then
	# 1. ask for release version
	unset version
	echo
	read -p "Release version: " version
	echo

	# 2. update version in pom file
	echo "Editing $ipsmall_pom_file ..."
	xmlstarlet edit -L -N "x=http://maven.apache.org/POM/4.0.0" -u "/x:project/x:version" -v "$version" "$ipsmall_pom_file"

	# 3. add and commit version number
	git add $ipsmall_pom_file
	git commit -m "Updated to $version"
	
	# 4. create tag
	git tag "$version"
	
	echo "Release $version created. Perform git push git@github.com:eID-Client-Testbed/testbed.git \"$version\" to publish release to GitHub."
	
else
	echo "xmlstarlet not found. Unable to modify $ipsmall_pom_file. Edit manualy, add and commit to repo and create tag."
fi

