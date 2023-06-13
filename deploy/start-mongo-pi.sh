#!/bin/bash

ssh pi "docker pull mongo:bionic"

ssh pi "docker run -d -p 27017:27017 --name mongodb -v /home/jacobg/Programming/finance/finance_data/mongo-data:/data/db mongo:bionic"