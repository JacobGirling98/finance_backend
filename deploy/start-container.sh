#!/bin/bash

ssh pi "docker run -e PROFILE=docker -v /home/jacobg/Programming/finance/finance_data/prod:/app/data -p 9000:9000 -d --name finance-backend -t finance-backend"