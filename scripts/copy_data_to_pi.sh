#!/bin/bash

env="prod"
location=C:/Users/jakeg/Programming/finance/data/${env}

files=("accounts" "categories" "description_mappings" "income_sources" "logins" "payees" "reminders" "standing_orders" "transactions")

copy_to_pi() {
    local filename="$1.csv"
    scp "$location/$filename" pi:/home/jacobg/Programming/finance/finance_data/prod/$filename
    echo "Copied $filename to pi."
}

# Loop over the files and call the function
for file in "${files[@]}"; do
    copy_to_pi "$file"
done

echo "All files copied successfully!"