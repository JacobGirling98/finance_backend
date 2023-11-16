#!/bin/bash

container_name="finance-backend"

container_id=$(docker ps -q -f name=$container_name)

if [ -n "$container_id" ]; then
  docker container stop $container_name
fi

docker rm $container_name

docker run -e PROFILE=docker -v /home/jacobg/Programming/finance/finance_data:/app/data -v /home/jacobg/Programming/finance/google-credentials:/app/config -p 9000:9000 -d --name finance-backend -t finance-backend