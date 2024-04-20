#!/bin/bash

container_name="finance-backend"

container_id=$(docker ps -q -f name=$container_name)

if [ -n "$container_id" ]; then
  echo "Finance Backend: app is already running, restarting now."
  docker container stop $container_name > /dev/null
fi

docker rm $container_name > /dev/null

container_id=`docker run -e PROFILE=docker -v /home/jacobg/Programming/finance/finance_data:/app/data -v /home/jacobg/Programming/finance/google-credentials:/app/config -p 9000:9000 -d --name finance-backend -t finance-backend`

echo "Finance Backend: started with container id $container_id"