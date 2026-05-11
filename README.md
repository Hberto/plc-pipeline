# plc-pipeline
Scalable and robust data pipeline for PLCs

## Stack

| Layer | Technology |
|---|---|
| Data ingestion | MQTT (Eclipse Mosquitto), Java MQTT-Kafka Bridge |
| Message broker | Apache Kafka + Zookeeper |
| Stream processing | Apache Spark (PySpark Structured Streaming) |
| Storage | Apache Cassandra |
| Visualization | Grafana |
| Monitoring | Kafdrop, Spark History Server |
| Infrastructure | Docker, Docker Compose |

# Introduction
Programmable Logic Controllers (PLCs) are the state-of-the-art in automation. PLCs control plants and facilities and are important for information exchange at the control level of the automation hierarchy. The automation hierarchy separates responsibilities but restricts information sharing between layers and plants. To overcome this limitation, there are new opportunities for Industrial IoT that combine PLCs with cloud solutions and Big Data technologies.

# Use-Case
Industrial PLCs are used for control in power generation and distribution plants. There are concepts and possibilities to adapt industrial PLCs for Industry 4.0 scenarios. These concepts can also be applied to various machines and industrial setups.
The use-case for this application is predictive and preventive analysis and maintenance to ensure reliable operations and reduce costs on service calls.

# Goals
The goal is to build a data pipeline for PLCs that is able to collect and analyze large amounts of data — such as alarms, inputs and outputs from industrial PLCs in power generation and distribution systems. The architecture focuses on scalability and robustness.

# Architecture
The following architecture uses an Event-Driven SOA approach.

## Technical Context
The technical context shows an industrial plant sending alarms and sensor/actuator data to the data pipeline prototype. The PLC is connected via the internet, and since it is a scalable system, additional PLCs can be added as data sources.

![Architecture](https://github.com/Hberto/plc-pipeline/blob/main/images/tech_kontext_open_source.png)

## Component Diagram
The following figure shows level 0 of the setup. On the left side is the PLC; on the right side is the data pipeline application. There is also a bidirectional data exchange.

![Architecture](https://github.com/Hberto/plc-pipeline/blob/main/images/component_vogel.png)

### Level 1: Data pipeline application
Level 1 shows the APIs and dependencies between components: MQTT_Kafka_Bridge, Eclipse Mosquitto, Kafka, Spark, PySpark Executor, Cassandra, and Grafana.

![Architecture](https://github.com/Hberto/plc-pipeline/blob/main/images/Component_OpenSource.png)

**MQTT_Kafka_Bridge:** Responsible for communication between the PLC and Kafka. It is a bridge between MQTT and Kafka using the Eclipse Paho API. An MQTT client is created and configured with methods for publishing and subscribing to MQTT messages. Messages from the PLC are forwarded to Kafka via the bridge, and messages from Kafka are sent back to the PLC.
The component also uses the Kafka API to create connections, consumers, and producers.

**Eclipse Mosquitto:** Provides an MQTT broker for data exchange between the PLC and the open-source data pipeline, enabling a PLC to be used as a data source.

**Kafka:** Connects the MQTT_Kafka_Bridge and PySpark Executor. Kafka forwards messages to Spark, and the PySpark Executor sends processed data back to the MQTT_Kafka_Bridge using the Spark-SQL-Kafka API.

**PySpark Executor:** Uses the Spark-SQL-Kafka API, Spark-CassandraConnector API, and Spark Structured Streaming API. Spark Structured Streaming provides a continuous stream of data processed per micro-batch. It also writes processed data to Cassandra and sends data back to Kafka.

**Spark:** Connected to the PySpark Executor. The Spark Master assigns tasks to Spark Workers to process per batch.

**Cassandra:** Receives data from the PySpark Executor. Data can be stored and queried by a Grafana plugin.

**Grafana:** Queries data from Cassandra via a plugin configured as the data source.

#### Class Diagram of MQTT_Kafka_Bridge component
Shows the classes MQTT_Kafka_Bridge.java, StringConsumer.java, Application_pipeline.java, StringProducer.java and the MqttCallback interface.

![Architecture](https://github.com/Hberto/plc-pipeline/blob/main/images/cd-mqtt-bridge.png)

**MQTT_Kafka_Bridge.java** Implements the MqttCallback interface using the Eclipse Mosquitto Paho API and the Kafka API. It is the MQTT client that receives messages from the PLC and forwards them to Kafka. With each incoming message, a callback is invoked that forwards the message directly to Kafka via StringProducer.java. Messages from the PySpark Executor are forwarded by StringConsumer.java and sent back to the PLC.

**Application_pipeline.java** The entry point. Host name and port can be configured here. It is the connection point to the MQTT broker.

**StringConsumer.java** Creates a Kafka consumer and connects to the Kafka broker.

**StringProducer.java** Creates a Kafka producer and connects to the Kafka broker.


## Deployment
The deployment view is divided into three parts: data extraction, processing, and visualisation. All containers are synchronised with the server clock via Docker volumes.


### Deployment: Data extraction

![Architecture](https://github.com/Hberto/plc-pipeline/blob/main/images/OS_Deployment_Extrahierung.png)

Containers: Kafka, Eclipse Mosquitto, Kafdrop, Zookeeper.

The PLC connects as an MQTT client to the MQTT broker via the host IP on port `1883` (unencrypted). The Java application running on the server is also connected as an MQTT client and forwards data to Kafka via port `29092`.

Kafdrop is a monitoring tool for Kafka, accessible on port `9000`. Zookeeper connects to Kafka via hostname `zookeeper1` on port `2181`.


### Deployment: Data processing

![Architecture](https://github.com/Hberto/plc-pipeline/blob/main/images/OS_Deployment_Verarbeitung.png)

Containers: Spark Master, Spark Worker, Cassandra, PySpark Executor.

The PySpark Executor connects to Kafka (`kafka:9092`), Cassandra (`cassandra:9042`), and the Spark Master (`spark-master:7077`). The Python script is synchronised via a Docker volume and downloads the required JAR packages for Cassandra, Spark, and Kafka at runtime.

Three streams run concurrently: one reads from Kafka and stores timestamped data in Cassandra, one sends data back to Kafka (and on to the PLC), and one handles stream processing metrics.

Spark Master is accessible on port `8080`, the Spark History Server on port `18080`. Additional Spark Worker containers can be added — limited only by host hardware. Spark is configurable via the `spark-defaults.conf` Docker volume.

Cassandra is accessible on port `9042`. Additional Cassandra nodes can be added. Configuration is done via the `cqlshrc.sample` Docker volume.


### Deployment: Data visualization

![Architecture](https://github.com/Hberto/plc-pipeline/blob/main/images/OS_Deployment_Visu.png)

Container: Grafana, accessible on port `3000`.

Cassandra is configured as the data source via hostname `cassandra` and port `9042`. Grafana queries data at configurable intervals. The dashboard is persistently stored via Docker volume provisioning and configured via `grafana.ini`.



# Run the Pipeline

## Prerequisites
The following should be installed:
- Docker and Docker Compose
- Git
- A Java IDE (for running the MQTT-Kafka Bridge)
- Basic knowledge of Docker and Bash

## Steps

1. Configure your host IP
```sh
cp docker/.env.example docker/.env
# Edit docker/.env and set HOST_IP to your server's IP
```
This is needed so external clients (e.g. the PLC) can reach Kafka from outside Docker.

2. Start all Docker containers
```sh
cd docker
docker compose up -d
```

3. Verify all 9 containers are running
```sh
docker ps -a
```

4. Open Grafana
```
http://localhost:3000
```

5. Open Kafka monitoring (Kafdrop)
```
http://localhost:9000
```

6. Open Spark Master monitoring
```
http://localhost:8080
```

7. Run the MQTT-Kafka Bridge
   - Open `src/plc_connector` in your Java IDE
   - Run `Application_pipeline.java`
   - This pushes PLC values into Kafka

8. Start the PySpark stream
```sh
docker exec -it executor bash
cd scripts/
python3 simplestream.py
```

9. Verify data is written to Cassandra

The `cassandra-init` container runs automatically on startup and creates the keyspace and table. To inspect:
```sh
docker exec -it cassandra cqlsh -u cassandra -p cassandra -e "SELECT * FROM test.test;"
```

10. Connect Cassandra with Grafana
    - Open Grafana in your browser and log in
    - Go to **Configuration → Data Sources** and search for Cassandra
    - Set the host to `cassandra:9042`
    - *Connected* should appear on a successful connection

11. Adapt for your own needs. Have fun!
