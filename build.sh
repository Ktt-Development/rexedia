#!/bin/bash
Version=1.1.0
Name="rexedia"
Vendor="Ktt Development"
Workspace=package
Dest=$Name

# cp -r bin $Workspace/bin
cp LICENSE $Workspace/LICENSE

jpackage \
--name "$Name" \
--icon icon.ico \
--input $Workspace \
--dest . \
--type app-image \
--main-jar $Name-$Version.jar \
--main-class com.kttdevelopment.rexedia.Main \
--app-version $Version \
--vendor "$Vendor" \
--copyright "Copyright $Vendor 2020" \
--win-console