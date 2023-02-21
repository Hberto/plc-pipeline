# plc-pipeline (prototype)
Scalable and robust Datapipeline for plc's
# Introduction 
Programmable Logic Controllers (PLC's) are in the automation state-of-the-art. PLC's control plants and facilities and are important for the information exchange in the control level of automation hierarchy. The automation hierarchy seperates the responsibilities but restricts information sharing between the layers and plants. To get around the limitation of information sharing, there are new opportunities for Industrial IoT that include use with PLCs, cloud solutions and Big Data technologies. 

# Use-Case
Industrial PLCs are used for control in power generation and distribution plants. For this, there are some concepts and possibilities to adapt industrial PLCs for Industry 4.0 scenarios. These concepts can also be applied for several machines, industrial setups.
The Use-Case for the application is predictive and preventive analysis and maintenance to ensure reliable operations and save costs on service calls.

# Goals
The goal is to build a data pipeline for plcs. It must be able to collect and analyze a large amount of data, such as alarms, inputs and outputs from industrial PLCs for example in power generation and power distribution systems. Since not all architecture goals can be achieved, the focus for the system architecture is on scalability and robustness.

# Architecture
The following architecture of the prototype uses a Event-Driven SOA approach.

## Technical Context
The technical context shows that an industrial plant sends alarms, informations of the sensors and actuators etc. to the system - the datapipeline prototype. The plc is located somewhere in the world and sends the data via internet to the system. Since its a scalabe system, other plcs can be also added as data sources. 
![Architecture](https://github.com/Hberto/plc-pipeline/blob/main/images/tech_kontext_open_source.png)

## Component Diagram
The following Figure shows level 0 of the setup. On the left side is the PLC.
On the right side is the data pipeline application. There is also a bidirectional data exchange.
![Architecture](https://github.com/Hberto/plc-pipeline/blob/main/images/component_vogel.png)

### Level 1: Data pipeline application
The figure shows a deeper level of the open source data pipeline application.
Level 1 shows the use of the APIs and the dependencies between the components. The components MQTT_Kafka_Bridge, Eclipse Mosquitto, Kafka, Spark, PySpark Executor, Cassandra, Grafana.

![Architecture](https://github.com/Hberto/plc-pipeline/blob/main/images/Component_OpenSource.png)

**MQTT_Kafka_Bridge:** The component is responsible for the communication between the PLC and Kafka.
PLC and Kafka. It is a bridge between MQTT and Kafka. It uses
the Eclipse Paho API. With the help of the interface, an MQTT client is created and con-
gurated. Methods for publishing and subscribing to MQTT messages are also used.
are also used. For the experiment, messages from the PLC are forwarded to Kafka via the bridge. the bridge to Kafka. Furthermore, it also accepts messages from Kafka and sends them back to the PLC. 
The component also uses the Kafka API. With the help of the interface, connections are made to Kafka, Kafka consumers and Kafka producers are created. Via the API, the consumers and producers are modified to measure latency.

**Eclipse Mosquitto:** The component provides an MQTT broker for data exchange between PLC and open-source data pipeline. This enables
component enables a PLC to be used as a data source.
can be used.

**Kafka:** The component provides a connection between the components MQTT_-.
Kafka_Bridge, Kafka and PySpark Executor. On the one hand, Kafka forwards messages
are forwarded to Spark. On the other hand, the PySpark Executor sends messages to Spark by using the
data back to the MQTT_Kafka_Bridge by using the Spark-SQL-Kafka API.

**PySpark Executor:** The component uses the Spark-SQL-Kafka API, Spark-CassandraConnector API and Spark Structured Streaming API from other components. By using Spark Structured Streaming API, there is a
continuous stream of data that is processed per batch. It also sends data to Cassandra database.

**Spark:** The component is connected to the PySpark Executor. It receives tasks from the PySpark Executor. The Spark Master assigns the tasks to Spark Workers to process per batch.

**Cassandra:** The component receives data from the PySpark Executor. The data can be stored and queried by a plugin from Grafana. The database has a table for storing the data.

**Grafana:** The component can query data from Cassandra via a plugin. Cassandra is entered as the data source.

#### Class Diagram of MQTT_Kafka_Bridge component
The Figure shows a deeper level of the MQTT_Kafka_Bridge component. It
The classes MQTT_Kafka_Bridge.java, Kafka_StringConsumer.java, Application_pipeline.java, Kafka_StringProducer.java and the interface MqttCallback can be seen.

![Architecture](https://github.com/Hberto/plc-pipeline/blob/main/images/cd%20mqtt_bridge.png)

**MQTT_Kafka_Bridge.java** The class MQTT_Kafka_Bridge.java implements the methods of the Mqtt the methods of the MqttCallback interface and uses the Eclipse Mosquitto Paho API and the Kafka API.
It is the MQTT client that receives the messages from the PLC and forwards them to Kafka. With each incoming message, a callback is invoked which
is called, which forwards the message directly to Kafka. For this purpose, the Kafka_-
StringProducer.java is used. The messages from the PySpark Executor are forwarded by the
Kafka_StringConsumer.java and sent back to the PLC.

**Application_pipeline.java** The class is the entry point. The host name and port
can be set. It is the connection point to the MQTT broker of the open source server.

**Kafka_StringConsumer.java** The class creates a Kafka consumer and connects to the Kafka broker.

**Kafka_StringProducer.java** The class creates a Kafka producer and connects to the Kafka Broker.


## Deployment
ToDo

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
