#!/bin/bash
# automate run
# run kafka containers
echo "Start Kafka Containers...."
cd ~/Schreibtisch/BA/plc-pipeline/docker_containers/kafka/
sudo docker compose up -d
# run spark containers
echo "Start Spark Containers"
cd ~/Schreibtisch/BA/plc-pipeline/docker_containers/spark/
sudo docker compose up -d