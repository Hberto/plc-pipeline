from cassandra.cluster import Cluster
from cassandra.auth import PlainTextAuthProvider
from statistics import mean
import time

ANZAHL = 10000

auth_provider = PlainTextAuthProvider(username='cassandra', password='cassandra')
cluster = Cluster(['89.58.55.209'], port=9042, auth_provider=auth_provider)
print('connecting to cassandra')
session = cluster.connect()

print('execute query and measure')
ts1_arr = []
ts2_arr = []
ges_diff = []
for i in range(ANZAHL):
    ts1_arr.append(int(time.time() * 1000))
    rows = session.execute("SELECT topic, CAST(value as double), timestamp FROM test.test")
    ts2_arr.append(int(time.time() * 1000))
    ges_diff.append(ts2_arr[i] -  ts1_arr[i])

print("Average of Read latency from Cassandra in ms = ",  mean(ges_diff))
print("Minimum Read latency from Cassandra in ms = ", min(ges_diff))
print("Maximum Read latency from Cassandra in ms = ", max(ges_diff))