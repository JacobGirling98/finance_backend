#!/bin/bash

container_name="finance-backend"
finance_dir=/home/jacobg/Programming/finance

ssh pi "docker container stop $container_name"
ssh pi "docker rm $container_name"
ssh pi "docker image rm $container_name"

ssh pi "cd $finance_dir && docker load < backend.tar.gz"
#ssh pi "cd $finance_dir && rm backend.tar.gz"

ssh pi 'docker run -e PROFILE=docker -e GITHUB_TOKEN=$GITHUB_TOKEN -v /home/jacobg/Programming/finance/finance_data:/app/data -p 9000:9000 -d --name finance-backend -t finance-backend'