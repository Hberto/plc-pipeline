import csv
import pandas as pd
from cassandra.cluster import Cluster
from cassandra.auth import PlainTextAuthProvider
from statistics import mean

filePLCArrival = 'MQTTArrival.csv'
fileKafkaArrival = 'KafkaMsgArrival.csv'

# Section: Read from csv and extract time in ms data
with open(filePLCArrival, mode='r') as file:
    csvmqtt = csv.reader(file)
    str_data_mqtt_ms = [row[2] for row in csvmqtt][1:]
    data_mqtt_ms = [int(row) for row in str_data_mqtt_ms]

with open(fileKafkaArrival, mode='r') as file:
    csvkafka = csv.reader(file)
    str_data_kafka_ms = [row[1] for row in csvkafka][1:]
    data_kafka_ms = [int(row) for row in str_data_kafka_ms]


# Section: Connect & Cassandra Data Retrieve 
## 1. Connect to Cassandra Database and execute queries
auth_provider = PlainTextAuthProvider(username='cassandra', password='cassandra')
cluster = Cluster(['89.58.55.209'], port=9042, auth_provider=auth_provider)
print('connecting to cassandra')
session = cluster.connect()
print('execute query')










df = pd.read_csv(filePLCArrival)
saved_column = df.ArrivalTimeInMS1


df2 = pd.read_csv(fileKafkaArrival)

