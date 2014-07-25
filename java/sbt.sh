#!/bin/sh
java -DPropsLoader.branch=master -Xss2m -Xms2g -Xmx2g -jar project/strap/gruj_vs_sbt-launch-0.13.x.jar "$*"
