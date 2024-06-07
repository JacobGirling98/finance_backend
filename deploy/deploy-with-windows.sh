#!/bin/bash

forceBuild=$1

if [ -n "$forceBuild" ]; then
  ssh pi "cd /home/Jacob/programming/finance/finance_backend && ./deploy/pi/deploy.sh force"
else
  ssh pi "cd /home/Jacob/programming/finance/finance_backend && ./deploy/pi/deploy.sh"
fi