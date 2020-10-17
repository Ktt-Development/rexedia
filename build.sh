#!/bin/bash
VERSION=1.0.0
cd $(dirname "$0")
cp -r bin jar/bin
jpackage --name "rexedia2" \
--input jar \
--app-version $VERSION \
--main-jar rexedia-$VERSION.jar \
--main-class com.kttdevelopment.rexedia.Main \
--dest . \
--type app-image \
--vendor "Ktt Development" \
--copyright "Ktt Development 2020"
