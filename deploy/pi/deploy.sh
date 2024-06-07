#!/bin/bash

force_build=$1

echo $force_build

log_prefix="Finance Backend:"
container_name=finance-backend

start_app() {
  container_id=$(docker ps -q -f name=$container_name)

  if [ -n "$container_id" ]; then
    echo "$log_prefix app is already running, restarting now."
    docker container stop $container_name > /dev/null
  fi

  docker rm $container_name > /dev/null

  container_id=`docker run -e PROFILE=docker -v /home/jacobg/Programming/finance/finance_data:/app/data -v /home/jacobg/Programming/finance/google-credentials:/app/config -p 9000:9000 -d --name finance-backend -t finance-backend:latest`

  echo "$log_prefix started with container id $container_id"
}

build() {
  ./gradlew jib jibDockerBuild --no-daemon
}

# Fetch the latest commits and refs from the remote
git fetch > /dev/null
echo "$log_prefixFetched latest commits"

# Store the current HEAD commit hash
OLD_HEAD=$(git rev-parse HEAD)
echo "$log_prefix Previous HEAD: $OLD_HEAD"

# Merge the fetched commits
git merge > /dev/null
echo "$log_prefix Merging latest commits"

# Store the new HEAD commit hash
NEW_HEAD=$(git rev-parse HEAD)
echo "$log_prefix New HEAD: $NEW_HEAD"

# Compare OLD_HEAD and NEW_HEAD
if [ -n $force_build]; then
  echo $force_build
  echo "$log_prefix forcing Docker build..."
  build
elif [ "$OLD_HEAD" = "$NEW_HEAD" ]; then
  echo "$log_prefix up to date, skipping Docker build..."
else
  echo "$log_prefix there are unbuilt changes, starting build now."
  build
fi

start_app