#!/bin/bash
# automate run
# run kafka containers
echo "Stopping Kafka Containers...."
cd ~/Schreibtisch/BA/plc-pipeline/docker_containers/kafka/
sudo docker compose down
# run spark containers
echo "Stopping Spark Containers"
cd ~/Schreibtisch/BA/plc-pipeline/docker_containers/spark/
sudo docker compose down