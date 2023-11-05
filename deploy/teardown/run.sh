#!/bin/bash

script_path=$(realpath "$0")
script_dir=$(dirname "$script_path")

finance_dir=/home/jacobg/Programming/finance
script=cleanup-pi.sh

scp "$script_dir/$script" pi:~/Programming/finance/.

ssh pi "cd $finance_dir && ./$script"
ssh pi "cd $finance_dir && rm $script"