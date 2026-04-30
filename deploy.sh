#!/bin/bash

echo "Building..."
./mvnw clean package -DskipTests

if [ $? -ne 0 ]; then
    echo "Build failed, aborting."
    exit 1
fi

echo "Copying jar..."
sudo cp target/homeserver-0.0.1-SNAPSHOT.jar /opt/homeserver/homeserver.jar

echo "Restarting service..."
sudo systemctl restart homeserver

echo "Waiting for server to start..."
sleep 3

echo "Checking status..."
sudo systemctl status homeserver --no-pager -l
