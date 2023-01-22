#!/bin/bash

./start-docker.sh

docker build -t finance-backend ../.
docker save finance-backend | gzip -c > backend.tar.gz

scp backend.tar.gz pi:~/Programming/finance/backend.tar.gz

rm backend.tar.gz

finance_dir=/home/jacobg/Programming/finance

ssh pi "cd $finance_dir && docker load < backend.tar.gz"

./start-container.sh
