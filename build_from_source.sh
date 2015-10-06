#!/bin/bash

echo "*** Cleaning up ***"
rm -rf build/

mkdir -p build/

echo "*** Packaging sourcecode ... ***"
zip -rq build/sourcecode.zip CommonTestbedUtilities/
zip -rq build/sourcecode.zip IPSmallJava/
zip -rq build/sourcecode.zip IPSmallBrowserSimulator/
zip -rq build/sourcecode.zip XML_TR-3124/
zip -rq build/sourcecode.zip build_from_source.sh
zip -rq build/sourcecode.zip readme_build_from_source.txt

echo "*** Build Dependecies ... ***"
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
