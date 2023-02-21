import csv
import datetime

import pandas as pd

file = 'test.csv'


def create_timestamp_with_header_csv(my_file, header):

    print("Creating csv file with headers")
    with open(my_file, 'w', encoding='UTF8', newline='') as f:
        writer = csv.writer(f)
        writer.writerow(header)


def create_throughput_with_header_csv():
    header = ['Count', 'Time in ms']
    print("Creating csv file with headers")
    with open(file, 'w', encoding='UTF8', newline='') as f:
        writer = csv.writer(f)
        writer.writerow(header)


def delete_column(col):
    print("Delete column")
    data = pd.read_csv(file)
    data.drop(col, inplace=True, axis=1)
    data.to_csv(file, index=False)


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


def write_row(data, my_file):
    print("Add Data")
    with open(my_file, 'a', encoding='UTF8', newline='') as f:
        writer = csv.writer(f)
        writer.writerow(data)


def start_it():
    my_file = 'test.csv'
    header = ['QueueTSMeasurementNr', 'TSDateformat', 'TSHourFormat', 'TSLongformat']
    create_timestamp_with_header_csv(my_file, header)

    ts_Kafka_Queue = datetime.datetime.now()
    ts_Kafka_Queue_long = ts_Kafka_Queue.timestamp()

    # String Separation Operations
    str_date = str(ts_Kafka_Queue)
    date_time_split = str_date.split(" ")
    date_format = date_time_split[0]
    hour_format = date_time_split[1]

    # Put into csv
    data_kafka = ['1', date_format, hour_format , ts_Kafka_Queue_long]
    write_row(data_kafka, my_file)


data = ['bla0', 'bla0', 'bla0', 'bla0', 'h0']
data1 = ['bla1', 'bla1', 'bla1', 'bla1', 'h1']
data2 = ['bla2', 'bla2', 'bla2', 'bla2', 'h2']
# write_row(data)
# delete_row(0)
#delete_all_rows()
# create_timestamp_with_header_csv()
# delete_column('KafkaTimeStamp')

start_it()
