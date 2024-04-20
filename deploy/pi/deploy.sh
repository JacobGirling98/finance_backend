#!/bin/bash

log_prefix="Finance Backend:"
container_name=finance-backend

start_app() {
  container_id=$(docker ps -q -f name=$container_name)

  if [ -n "$container_id" ]; then
    echo "$log_prefix app is already running, restarting now."
    docker container stop $container_name > /dev/null
  fi

  docker rm $container_name > /dev/null

  container_id=`docker run -e PROFILE=docker -v /home/jacobg/Programming/finance/finance_data:/app/data -v /home/jacobg/Programming/finance/google-credentials:/app/config -p 9000:9000 -d --name finance-backend -t finance-backend`

  echo "$log_prefix started with container id $container_id"
}

# Fetch the latest commits and refs from the remote
git fetch

# Store the current HEAD commit hash
OLD_HEAD=$(git rev-parse HEAD)

# Merge the fetched commits
git merge > /dev/null

# Store the new HEAD commit hash
NEW_HEAD=$(git rev-parse HEAD)

# Compare OLD_HEAD and NEW_HEAD
if [ "$OLD_HEAD" = "$NEW_HEAD" ]; then
  echo "$log_prefix up to date, skipping Docker build..."

  start_app

  exit 0
fi

echo "$log_prefix there are unbuilt changes, starting build now."

docker build -t $container_name .

start_app