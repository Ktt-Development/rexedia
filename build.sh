#!/bin/bash
Version=1.0.0
Vendor="Ktt Development"
Workspace=package
Dest="rexedia"

jpackage \
--name "rexedia" \
--icon icon.ico \
--input $Workspace \
--dest . \
--type app-image \
--main-jar rexedia-$Version.jar \
--main-class com.kttdevelopment.rexedia.Main \
--app-version $Version \
--vendor "$Vendor" \
--copyright "Copyright $Vendor 2020" \
--win-console

cp -r bin $Dest/bin
cp LICENSE $Dest/LICENSE