from cassandra.cluster import Cluster
from cassandra.auth import PlainTextAuthProvider
import csv
import datetime

# Measurement Variables
header_mockup = ['CassandraReadTimeMeasureNr', 'TSDateformat', 'ClockTime1', 'ClockTime2', 'ReadTimeLong']
my_file_mockup = '/scripts/cassandraReadlatency.csv'


# Measurement functions
def create_timestamp_with_header_csv(my_file, header):

    print("Creating csv file with headers")
    with open(my_file, 'w', encoding='UTF8', newline='') as f:
        writer = csv.writer(f)
        writer.writerow(header)

def write_row(data, my_file):
    print("Add Data")
    with open(my_file, 'a', encoding='UTF8', newline='') as f:
        writer = csv.writer(f)
        writer.writerow(data)

def delete_all_rows(my_file):
    print("Delete all rows")
    data = pd.read_csv(my_file)
    data = data[0:0]
    data.to_csv(my_file, index=False)

def delete_row(row, my_file):
    print("Delete row")
    data = pd.read_csv(my_file)
    data.drop(row, inplace=True, axis=0)
    data.to_csv(my_file, index=False)

 # Create Measurment CSVs
create_timestamp_with_header_csv(my_file_mockup, header_mockup)

auth_provider = PlainTextAuthProvider(username='cassandra', password='cassandra')
cluster = Cluster(['10.8.0.1'], port=9042, auth_provider=auth_provider)
print('connecting to cassandra')
session = cluster.connect()
print('execute query')
tsdate1 = datetime.datetime.now()
ts1 = tsdate1.timestamp()
rows = session.execute("select * from test.test;")
tsdate2 = datetime.datetime.now()
ts2 = tsdate2.timestamp()

# Measurment of SparkProcessingTime
str_date1 = str(tsdate1)
str_date2 = str(tsdate2)
date_time_split1 = str_date1.split(" ")
date_time_split2 = str_date2.split(" ")
#Get Date format for data_cassandra
date_format1 = date_time_split1[0]

hour_format1 = date_time_split1[1]
hour_format2 = date_time_split2[1]
# Difference 
difference_read_execute_time = ts2 - ts1

# Put into csv
data_cassandra = ['1', date_format1, hour_format1 , hour_format2, difference_read_execute_time]
write_row(data_cassandra, my_file_mockup)

for row in rows:
    print (row[0], row[1], row[2])
