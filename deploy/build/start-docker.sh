#!/bin/bash

if (! docker stats --no-stream &> /dev/null ); then
 "C:\Program Files\Docker\Docker\Docker Desktop.exe"&
 #Wait until Docker daemon is running and has completed initialisation
while (! docker stats --no-stream &> /dev/null ); do
  echo "Waiting for Docker to launch..."
  sleep 1
done
fi