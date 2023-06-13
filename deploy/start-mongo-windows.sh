#!/bin/bash

./start-docker.sh

docker pull mongo:latest

docker run -d -p 27017:27017 --name mongodb -v /c/Users/jakeg/Documents/FinanceV3/mongo-data:/data/db mongo:latest