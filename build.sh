#!/bin/bash
jpackage --name "rexedia" \
--input target \
--main-jar rexedia.jar \
--dest . \
--type app-image \
--copyright "Ktt Development 2020"
