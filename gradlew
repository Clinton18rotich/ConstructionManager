#!/bin/sh
export JAVA_OPTS="-Xmx2048m"
exec ~/gradle-8.2/bin/gradle "$@"
