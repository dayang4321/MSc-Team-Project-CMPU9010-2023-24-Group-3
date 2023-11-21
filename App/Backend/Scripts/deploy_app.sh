#!/bin/bash

# Check if any process is running on port 8080
PROCESS=$(sudo lsof -t -i:8080)

if [ ! -z "$PROCESS" ]; then
    echo "Process found on port 8080, killing it..."
    sudo kill -9 $PROCESS
    echo "Process on port 8080 has been terminated."
else
    echo "No process found on port 8080. Ready to deploy the application."
fi

# Optional: Sleep for a short duration to ensure the port is completely freed
sleep 2

# Deploying the application
echo "Deploying the Java application..."
nohup java -jar /home/ec2-user/actions-runner/_work/MSc-Team-Project-CMPU9010-2023-24-Group-3/MSc-Team-Project-CMPU9010-2023-24-Group-3/App/Backend/target/backend_application_01.jar --spring.config.location=/home/ec2-user/config/> output.log 2>&1 &

echo "Java application deployment initiated."
