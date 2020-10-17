#!/bin/bash
jlink \
--module-path "target\rexedia-1.0.0.jar" \
--add-modules rexedia \
--output "/jre"