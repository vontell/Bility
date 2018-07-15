#!/bin/bash

echo "Waiting artifactory to launch on 8146..."

while ! timeout 1 bash -c "echo > /dev/tcp/localhost/8080"; do   
  sleep 1
done

cd code
chmod +x gradlew
./gradlew :ama:uploadArchives

echo "AMA launched into artifactory"