#!/bin/bash

container_name="finance-backend"
finance_dir=/home/jacobg/Programming/finance

docker container stop $container_name
docker rm $container_name
docker image rm $container_name

cd $finance_dir
docker load < backend.tar.gz

GITHUB_TOKEN=$(awk -F 'GITHUB_TOKEN=' '{print $2}' config.txt)

docker run -e PROFILE=docker -e GITHUB_TOKEN="$GITHUB_TOKEN" -v /home/jacobg/Programming/finance/finance_data:/app/data -v /home/jacobg/Programming/finance/google-credentials:/app/config -p 9000:9000 -d --name finance-backend -t finance-backend