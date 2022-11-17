import re
import math
import time 
import datetime
import pandas as pd 
from cassandra.cluster import Cluster
from cassandra.auth import PlainTextAuthProvider
import csv
import datetime
from statistics import mean

# Docs:
## WriteTime: https://docs.datastax.com/en/cql-oss/3.3/cql/cql_using/useWritetime.html
## Cassandra Driver: https://docs.datastax.com/en/developer/python-driver/3.18/getting_started/

# Script to measure the way from Spark to Grafana
## 1.TS from Kafka Msg Arrival appended to Test.Test Table
## 2.TS WriteTime of Data into Test.Test
## 3.TS of ArrivalTime into Grafana

# Steps
#1.Step: start 'docker logs -f grafana &> /home/herb/BA/plc-pipeline/docker_containers/Logs_and_Configs/Grafana/logs/output_log/output.txt'
#2.Step: Copy text to ArrivalData.txt
#3.Step: Make sure that all data test made through

# Configs and Constants
path = 'ArrivalData.txt'
ONE_HOUR = 3600000

# Section: Get Arrival Data Time

pattern = r'(kvstore t=\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}?\.\d+?\+\d{2}:\d{2})'
with open(path, 'r') as f:
    text = f.read()
matches = re.findall(pattern, text)

print(matches)
## substring to get date
sub_string = [match[10:36] for match in matches]
print(sub_string)
## convert arrival date in ms with microsec after comma
grafana_arrival_dates_in_ms = [datetime.datetime.strptime(i,"%Y-%m-%dT%H:%M:%S.%f").timestamp()*1000 for i in sub_string]
print("Length of arrival arr: ", len(grafana_arrival_dates_in_ms))
print(grafana_arrival_dates_in_ms)

# Section: Connect & Cassandra Data Retrieve 
## 1. Connect to Cassandra Database and execute queries
## 2. Get StartTime
## 3. Get StationTime Cassandra
auth_provider = PlainTextAuthProvider(username='cassandra', password='cassandra')
cluster = Cluster(['89.58.55.209'], port=9042, auth_provider=auth_provider)
print('connecting to cassandra')
session = cluster.connect()
print('execute query')

## Get Data From Spark as Start Time
rows_start_from_spark = session.execute("select current_timestamp from test.test;")

## convert start date in ms and add 1h
spark_start_time_in_ms =[(row.current_timestamp.timestamp()*1000) + ONE_HOUR  for row in rows_start_from_spark]
print("Length of start arr: ", len(spark_start_time_in_ms))
print(spark_start_time_in_ms)

## Get Data From WriteTime At Cassandra Table
rows_writetime_val_cas = session.execute("select writetime (value) from test.test;")
print('queries executed')

## convert it to milliseconds
cassandra_writetime_station_in_ms = [row.writetime_value/1000 for row in rows_writetime_val_cas]
print("Length of cassandra station arr: ", len(cassandra_writetime_station_in_ms))
print(cassandra_writetime_station_in_ms)

# Section: Time Measurements & Results
## subtract starttime with arrivaltime
res_start_end = []
for i in range(len(spark_start_time_in_ms)):
    res_start_end.append(grafana_arrival_dates_in_ms[i] - (spark_start_time_in_ms[i]))
avg_spark_grafana = mean(res_start_end)
print("Average of latency from Spark > Cassandra in ms = ", avg_spark_grafana)
print("Minimum latency from Spark > Grafana in ms = ", min(res_start_end))
print("Maximum latency from Spark > Grafana in ms = ", max(res_start_end))

## Subtract Time Spark -> Cassandra as Write Latency
res_cassandra_spark = []
for i in range(len(spark_start_time_in_ms)):
    res_cassandra_spark.append(cassandra_writetime_station_in_ms[i] - (spark_start_time_in_ms[i]))
avg_spark_cassandra = mean(res_cassandra_spark)
print("Average of latency from Spark > Cassandra in ms = ", avg_spark_cassandra)
print("Minimum latency from Spark > Cassandra in ms = ", min(res_cassandra_spark))
print("Maximum latency from Spark > Cassandra in ms = ", max(res_cassandra_spark))

## Subtract Time Cassandra -> Grafana
res_cassandra_grafana = []
for i in range(len(spark_start_time_in_ms)):
    res_cassandra_grafana.append(grafana_arrival_dates_in_ms[i] - (cassandra_writetime_station_in_ms[i]))
avg_cassandra_grafana = mean(res_cassandra_grafana)
print("Average of latency from Spark > Cassandra in ms = ", avg_cassandra_grafana)
print("Minimum latency from Spark > Grafana in ms = ", min(res_cassandra_grafana))
print("Maximum latency from Spark > Grafana in ms = ", max(res_cassandra_grafana)) 