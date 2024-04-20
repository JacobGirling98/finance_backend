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

git_pull_output=$(git pull 2>&1)

if [ $? -eq 0 ] || [ $git_pull_output =~ "Already up-to-date" ]; then
  echo "$log_prefix up to date, skipping Docker build..."

  start_app

  exit 0
fi

echo "$log_prefix there are unbuilt changes, starting build now."

docker build -t $container_name ../.

start_app