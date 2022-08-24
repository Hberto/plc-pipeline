# plc-pipeline
Scalable and robust Datapipeline for plc's
# Introduction 
Programmable Logic Controllers (PLC's) are in the automation state-of-the-art. PLC's control plants and facilities and are important for the information exchange in the control level of automation hierarchy. The automation hierarchy seperates the responsibilities but restricts information sharing between the layers and plants. To get around the limitation of information sharing, there are new opportunities for Industrial IoT that include use with PLCs, cloud solutions and Big Data technologies. 

# Use-Case
Industrial PLCs are used for control in power generation and distribution plants. For this, there are some concepts and possibilities to adapt industrial PLCs for Industry 4.0 scenarios. These concepts can also be applied for power generation and distribution plants.
The Use-Case for the application is predictive and preventive analysis and maintenance to ensure reliable operations and save costs on service calls.

# Goals
The goal is to build a data pipeline for plcs. It must be able to collect and analyze a large amount of data, such as alarms, inputs and outputs from industrial PLCs for example in power generation and power distribution systems. Since not all architecture goals can be achieved, the focus for the system architecture is on scalability and robustness.

# Architecture
![Architecture](BA-GrobArchitektur.drawio.png)

# Run Datapipeline
## Prerequisites
Following programms and services should be installed and basic knowledge of:
- Docker
- Git
- Bash
- IDE for JAVA
- Change volumes of used scripts in Docker-compose file
  
## Further Descriptions
- Pushing Data from the plc to kafka is temporarily managed over IDE. Improvements can be applied with a Dockerfile running the JAR Application
- Running the python script manually in the executor Docker container is temporarily. As Improvement the script can be executed directly when starting the docker container


## Steps
1. Run all Docker-Containers to start all services
```sh
# Change Directory to the cloned Git Repo
cd yourFolder/plc-pipeline/docker_containers

docker compose up -d #d for detached mode
```
2. Check if all 9 Docker Containers are running

```sh
# Lists all running Docker containers
docker ps -a
```
3. Open Grafana Monitoring
```txt
# Open Grafana in your browser
localhost:3000 or yourIP:3000
```
4. Open Kafka Monitoring
```txt
# Open Kafdrop in your browser
localhost:9000 or yourIP:9000
```
5. Open Spark Master Monitoring
```txt
# Open Spark Master with slave in your browser
localhost:8080 or yourIP:8080
```
6. Run Application Starter of plc4x connector
- Pushes the value of plc into kafka 
- Actual version: running it in IDE
7. Execute Python script to transfer data from kafka to spark
```sh
# Enter Docker Container bash
docker exec -it executor bash
# Change directory to scripts
cd scripts/
# Run Python Script
python3 simplestream.py # your python script
```
8. Check if data is written into cassandra
- plc_data_cassandra.sh - script for creating user, password, replication, keyspace and table. Modificate here for your data

For example:
```sh
# Enter Docker Container Cassandra bash
docker exec -it cassandra bash
# Entering user credentials with user/password 
cqlsh --user cassandra --password cassandra
# Check table if was created and data is written inside cassandra
select * from test.test;
```
9. Connect Cassandra with Grafana
- Docker container installed cassandra as datasource for grafana
- Go to your browser and open grafana
- Log in with admin/yourPassword
- optional: change password
- Go to 'Datasources' and search for Cassandra
```txt
# Enter Host
Host: cassandra:9042
# Optional enter the keyspace from plc_data_cassandra script
# Optional use credentials
```
- 'Connected' should be appearing for a succesful connection

10. Modifacte for your own needs. Have fun!
