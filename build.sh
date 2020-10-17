#!/bin/bash
Version=1.0.0
Vendor="Ktt Development"
Workspace=jar

cp -r bin $Workspace/bin
cp LICENSE $Workspace/LICENSE

jpackage --name "rexedia" \
--input $Workspace \
--dest . \
--type app-image \
--main-jar rexedia-$Version.jar \
--main-class com.kttdevelopment.rexedia.Main \
--app-version $Version \
--vendor "$Vendor" \
--copyright "$Vendor" \
--win-console
