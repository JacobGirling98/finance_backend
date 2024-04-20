#!/bin/bash

git_pull_output=$(git pull 2>&1)

# if [ $? -eq 0 ] || [ $git_pull_output =~ "Already up-to-date" ]; then
#   echo "Finance Backend: up to date, skipping Docker build..."

#   ./pi/start-app.sh

#   exit 0
# fi


echo "Finance Backend: there are unbuilt changes, starting build now."

docker build -t finance-backend ../.

./pi/start-app.sh