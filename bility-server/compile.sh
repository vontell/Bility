#!/usr/bin/env bash
set -xeuo pipefail

# First clean and build the server

cd /webserver
# ./gradlew clean
# ./gradlew build

# Then run the server
java -server -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -XX:InitialRAMFraction=2 -XX:MinRAMFraction=2 -XX:MaxRAMFraction=2 -XX:+UseG1GC -XX:MaxGCPauseMillis=100 -XX:+UseStringDeduplication -jar build/libs/bility-android-server.jar