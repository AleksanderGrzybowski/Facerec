#!/usr/bin/env bash

docker run --rm -v `pwd`:/Facerec -u `stat -c "%u:%g" build.gradle` java:8-jdk /bin/bash -c "cd /Facerec && ./gradlew clean shadow"

# HOME needs to be overriden for npm to store caches (setting cache folder triggers more problems...)
# git config is required for git cloning by npm
docker run --rm -v `pwd`/frontend:/Facerec-frontend -e HOME=/tmp -u `stat -c "%u:%g" build.gradle` node:8.1.2 /bin/bash -c "
  git config --global user.email nobody@nobody.com &&
  git config --global user.name Nobody &&
  npm config set cache /tmp && 
  cd /Facerec-frontend &&
  npm install && 
  npm run build
"
