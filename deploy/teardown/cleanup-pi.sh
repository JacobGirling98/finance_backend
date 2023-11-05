#!/bin/bash

container_name="finance-backend"
finance_dir=/home/jacobg/Programming/finance

if [ ! -f "$finance_dir/backend.tar.gz" ]; then
  echo "The docker image does not exist, please build and transfer it first."
  exit 1
fi

docker container stop $container_name
docker rm $container_name
docker image rm $container_name

cd $finance_dir || exit
docker load < backend.tar.gz || exit

rm backend.tar.gz