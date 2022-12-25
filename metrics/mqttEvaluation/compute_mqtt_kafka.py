import csv
import pandas as pd
from cassandra.cluster import Cluster
from cassandra.auth import PlainTextAuthProvider
from statistics import mean

filePLCArrival = 'MQTTArrival.csv'
fileKafkaArrival = 'KafkaMsgArrival.csv'
ONE_HOUR = 3600000

# Section: Read from csv and extract time in ms data
with open(filePLCArrival, mode='r') as file:
    csvmqtt = csv.reader(file)
    str_data_mqtt_ms = [row[2] for row in csvmqtt][1:]
    data_mqtt_ms = [int(row) for row in str_data_mqtt_ms]

with open(fileKafkaArrival, mode='r') as file:
    csvkafka = csv.reader(file)
    str_data_kafka_ms = [row[1] for row in csvkafka][1:]
    data_kafka_ms = [int(row) for row in str_data_kafka_ms]

print("Length of start data_mqtt_arr: ", len(data_mqtt_ms))
# Section: Connect & Cassandra Data Retrieve 
## 1. Connect to Cassandra Database and execute queries
auth_provider = PlainTextAuthProvider(username='cassandra', password='cassandra')
cluster = Cluster(['89.58.55.209'], port=9042, auth_provider=auth_provider)
print('connecting to cassandra')
session = cluster.connect()
print('execute query')

## Get Arrival Data from Kafka
rows_start_from_spark = session.execute("select current_timestamp from test.test;")


## convert start date in ms and add 1h
spark_start_time_in_ms =[(row.current_timestamp.timestamp()*1000) + ONE_HOUR  for row in rows_start_from_spark]
print("Length of start arr: ", len(spark_start_time_in_ms))
#print(spark_start_time_in_ms)

# Section: Time Measurements & Results
## Get the latest rows
latest_spark_time = []
for i in range(len(spark_start_time_in_ms)):
    latest_spark_time.append(spark_start_time_in_ms[i])
print("Length of arr: ", len(latest_spark_time))

## subtract mqtt time with spark arrivaltime
res_mqtt_kafka = []
for i in range(len(latest_spark_time)):
    res_mqtt_kafka.append(latest_spark_time[i] - data_mqtt_ms[i])
print(res_mqtt_kafka)
avg_mqtt_spark = mean(res_mqtt_kafka)
print("Average of latency from MQTT > Kafka > Spark in ms = ", avg_mqtt_spark)
print("Minimum latency from MQTT > Kafka > Spark in ms = ", min(res_mqtt_kafka))
print("Maximum latency from MQTT > Kafka > Spark in ms = ", max(res_mqtt_kafka))

## subtract mqtt time with kafka consume at kafka bridge
res_mqtt_kafka_consumer = []
for i in range(len(latest_spark_time)):
    res_mqtt_kafka_consumer.append(data_kafka_ms[i] - latest_spark_time[i])
print(res_mqtt_kafka_consumer)
print("Average of latency from MQTT > Kafka > Spark > MQTT in ms = ",mean(res_mqtt_kafka_consumer))
print("Minimum latency from MQTT > Kafka > Spark > MQTT in ms = ", min(res_mqtt_kafka_consumer))
print("Maximum latency from MQTT > Kafka > Spark > MQTT in ms = ", max(res_mqtt_kafka_consumer))