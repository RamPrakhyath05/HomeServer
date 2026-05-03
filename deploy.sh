#!/bin/bash
echo "Building..."
./mvnw clean package -DskipTests

if [ $? -ne 0 ]; then
    echo "Build failed, aborting."
    exit 1
fi

echo "Building Docker image..."
docker build -t ramprakhyath/homeserver:latest .

echo "Pushing to Docker Hub..."
docker push ramprakhyath/homeserver:latest
