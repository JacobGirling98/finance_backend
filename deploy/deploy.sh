#!/bin/bash

./start-docker.sh

container_name=finance-backend

docker build -t $container_name ../.
docker save finance-backend | gzip -c > backend.tar.gz

scp backend.tar.gz pi:~/Programming/finance/backend.tar.gz

rm backend.tar.gz
docker image rm $container_name

finance_dir=/home/jacobg/Programming/finance

ssh pi "cd $finance_dir && docker load < backend.tar.gz"

./install-on-pi.sh
