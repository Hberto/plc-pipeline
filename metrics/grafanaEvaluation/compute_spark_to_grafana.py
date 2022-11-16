import re
import math
import time 
import datetime
import pandas as pd 
from cassandra.cluster import Cluster
from cassandra.auth import PlainTextAuthProvider
import csv
import datetime

path = 'ArrivalData.txt'

pattern = r'(kvstore t=\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}?\.\d+?\+\d{2}:\d{2})'
with open(path, 'r') as f:
    text = f.read()
matches = re.findall(pattern, text)

print(matches)
# substring to get date
sub_string = [match[10:36] for match in matches]
print(sub_string)
# convert arrival date in ms
grafana_arrival_dates_in_ms = [datetime.datetime.strptime(i,"%Y-%m-%dT%H:%M:%S.%f").timestamp()*1000 for i in sub_string]
print(grafana_arrival_dates_in_ms)
# get starttime from cassandra database
auth_provider = PlainTextAuthProvider(username='cassandra', password='cassandra')
cluster = Cluster(['89.58.55.209'], port=9042, auth_provider=auth_provider)
print('connecting to cassandra')
session = cluster.connect()
print('execute query')
rows = session.execute("select current_ts_kafka from test.test;")
#convert it to milliseconds

# subtract starttime with arrivaltime


