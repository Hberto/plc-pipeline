import csv
import pandas as pd

file = 'test.csv'


def create_timestamp_with_header_csv():
    header = ['PLCTimestamp', 'MQTTBridgeAppTimestamp', 'KafkaTimeStamp', 'SparkTimeStamp', 'CassandraTimeStamp']

    print("Creating csv file with headers")
    with open(file, 'w', encoding='UTF8', newline='') as f:
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


def delete_all_rows():
    print("Delete all rows")
    data = pd.read_csv(file)
    data = data[0:0]
    data.to_csv(file, index=False)


def delete_row(row):
    print("Delete row")
    data = pd.read_csv(file)
    data.drop(row, inplace=True, axis=0)
    data.to_csv(file, index=False)


def write_row(data):
    print("Add Data")
    with open(file, 'a', encoding='UTF8', newline='') as f:
        writer = csv.writer(f)
        writer.writerow(data)