#!/bin/bash
Version=1.1.0
Name="rexedia"
Vendor="Ktt Development"
Workspace=package
Dest=$Name

cp -r app/bin $Workspace/bin
cp LICENSE $Workspace/LICENSE

# package

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

# installer

jpackage
--name "$Name" \
--license-file "LICENSE" \
--win-dir-chooser \
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