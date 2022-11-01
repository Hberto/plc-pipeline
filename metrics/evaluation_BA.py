import csv
import pandas as pd
import datetime


# Change File for manipulating
file = 'test.csv'

# Measurement functions
def create_timestamp_with_header_csv(my_file, header):

    print("Creating csv file with headers")
    with open(my_file, 'w', encoding='UTF8', newline='') as f:
        writer = csv.writer(f)
        writer.writerow(header)


def create_throughput_with_header_csv(my_file, header):
    print("Creating csv file with headers")
    with open(my_file, 'w', encoding='UTF8', newline='') as f:
        writer = csv.writer(f)
        writer.writerow(header)


def delete_column(my_file, col):
    print("Delete column")
    data = pd.read_csv(my_file)
    data.drop(col, inplace=True, axis=1)
    data.to_csv(file, index=False)

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

##########Cassandra####################
#Execute CQSLSH: select WRITETIME (value) from test.test;
def save_measurement_from_Cassandra():
    # printing microseconds in date time object (CASSANDRA)
    timestamp_microseconds = 1667317488899000 # change here
    timestamp_milliseconds = timestamp_microseconds/1000
    timestamp_seconds = timestamp_microseconds/1000000
    dobj = datetime.datetime.fromtimestamp(timestamp_seconds)
    print(dobj.isoformat())

    print('Writing to Cassandra Evaluation Table')
    my_file = './cassandraEvaluation/cassandraTable.csv'
    header = ['cassandraWriteMilli','cassandraWriteDate']
    create_timestamp_with_header_csv(my_file, header)
    data = [timestamp_milliseconds, dobj]
    write_row(data, my_file)
    print('Writing to Cassandra Evaluation Table done')

### Start App
save_measurement_from_Cassandra()