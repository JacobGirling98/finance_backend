#!/bin/bash

force_build=$1

log_prefix="Finance Backend:"
container_name=finance-backend

start_app() {
  container_id=$(docker ps -q -f name=$container_name)

  if [ -n "$container_id" ]; then
    echo "$log_prefix App is already running, restarting now."
    docker container stop $container_name > /dev/null
  fi

  docker rm $container_name > /dev/null

  container_id=`docker run -e PROFILE=docker -v /home/Jacob/programming/finance/finance_data:/app/data -v /home/Jacob/programming/finance/google-credentials:/app/config -p 9000:9000 -d --name finance-backend -t finance-backend:latest`

  echo "$log_prefix Started with container id $container_id"
}

build() {
  ./gradlew jibDockerBuild --no-daemon
}

# Fetch the latest commits and refs from the remote
git fetch > /dev/null

# Store the current HEAD commit hash
OLD_HEAD=$(git rev-parse HEAD)

# Merge the fetched commits
git merge > /dev/null

# Store the new HEAD commit hash
NEW_HEAD=$(git rev-parse HEAD)

if [ -n "$force_build" ]; then
  echo "$log_prefix Forcing Docker build..."
  build
elif [ "$OLD_HEAD" = "$NEW_HEAD" ]; then
  echo "$log_prefix Up to date, skipping Docker build..."
else
  echo "$log_prefix There are unbuilt changes, starting build now."
  build
fi

start_app