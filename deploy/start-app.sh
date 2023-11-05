#!/bin/bash

finance_dir=/home/jacobg/Programming/finance
container_name=finance-backend

GITHUB_TOKEN=$(ssh pi "awk -F 'GITHUB_TOKEN=' '{print $2}' ~/Programming/finance/config.txt")

ssh pi "docker container stop $container_name"
ssh pi "docker rm $container_name"
ssh pi "docker run -e PROFILE=docker -v /home/jacobg/Programming/finance/finance_data:/app/data -v /home/jacobg/Programming/finance/google-credentials:/app/config -p 9000:9000 -d --name finance-backend -t finance-backend"

