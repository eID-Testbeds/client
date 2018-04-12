#!/bin/bash

timestamp=$(date +"%Y%m%d-%H%M")

echo "*** Cleaning up ***"
rm -rf build/

mkdir -p build/

# get version
if command -v xmlstarlet >/dev/null 2>&1; then
	testbed_version=Testbed_$(xmlstarlet.exe select -N "x=http://maven.apache.org/POM/4.0.0" -t -v "/x:project/x:version" "IPSmallJava/pom.xml")
else
	testbed_version=$(basename "$PWD")
fi

echo "*** Build Dependecies ... ***"
cd BouncyCastleTlsPatched
mvn clean install
cp target/*.zip ../build/
cd ..

cd CommonTestbedUtilities
mvn clean install
cp target/*.zip ../build/
cd ..

echo "*** Build Testbed ... ***"
cd IPSmallJava
mvn clean install
cp target/*.zip ../build/
cd ..

echo "*** Build Browsersimulator ... ***"
cd IPSmallBrowserSimulator
mvn clean install
cp target/*.zip ../build/
cd ..

echo "*** Packaging Testbed ... ***"
testbed_name="$testbed_version"_$timestamp.zip
cd build
zip -rq $testbed_name IPSmallJava_*.zip
rm -f IPSmallJava_*.zip
zip -rq $testbed_name browsersimulator_*.zip
rm -f browsersimulator_*.zip
zip -rq $testbed_name CardSimControl_*.zip
rm -f CardSimControl_*.zip
cd ..
