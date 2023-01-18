#!/bin/bash

finance_dir=/home/jacobg/Programming/finance
script=to-run-on-pi.sh

scp $script pi:~/Programming/finance/.

ssh pi "cd $finance_dir && ./$script"

ssh pi "cd $finance_dir && rm $script"
