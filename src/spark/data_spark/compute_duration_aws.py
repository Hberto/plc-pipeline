import csv
from statistics import mean
import datetime

fileArrival = r'C:\Users\Herberto\Desktop\UNI\Bachelor-Arbeit\plc-pipeline\src\plc_connector\src\main\java\aws_iot_test_latency\timestamp_measurement\sync_test2_10000\arrival.csv'
fileStart = r'C:\Users\Herberto\Desktop\UNI\Bachelor-Arbeit\plc-pipeline\src\plc_connector\src\main\java\aws_iot_test_latency\timestamp_measurement\sync_test2_10000\start.csv'
fileS3Arrival =r'C:\Users\Herberto\Desktop\UNI\Bachelor-Arbeit\plc-pipeline\src\plc_connector\src\main\java\aws_iot_test_latency\timestamp_measurement\sync_test2_10000\arrivalS3.csv'
fileArrivalTimestream = r'C:\Users\Herberto\Desktop\UNI\Bachelor-Arbeit\plc-pipeline\src\plc_connector\src\main\java\aws_iot_test_latency\timestamp_measurement\sync_test2_10000\arrivalTimestream.csv'

ONE_HOUR = 3600000

with open(fileArrival, mode='r') as file:
    csv_arrival = csv.reader(file)
    str_data_arrival_ms = [row[0] for row in csv_arrival][1:]
    data_arrival_ms = [int(row) for row in str_data_arrival_ms]

with open(fileStart, mode='r') as file2:
    csv_start = csv.reader(file2)
    str_data_start_ms = [row[0] for row in csv_start][1:]
    data_start_ms = [int(row) for row in str_data_start_ms]

with open(fileS3Arrival, mode='r') as file3:
    csv_S3 = csv.reader(file3)
    str_arrivalS3_ms = [row[0] for row in csv_S3][1:]
    data_arrivalS3_ms = [int(row) for row in str_arrivalS3_ms]


# RTT to AWS IoT
res_diff = []
for i in range(len(data_start_ms)):
    res_diff.append(data_arrival_ms[i] - data_start_ms[i])

print("Average of latency from MockUp > AWS IoT Core in ms = ", mean(res_diff))
print("Minimum latency from MockUp > AWS IoT Core in ms = ", min(res_diff))
print("Maximum latency from MockUp > AWS IoT Core in ms = ", max(res_diff))

# S3 and Starttime
res_diff_s3 = []
for i in range(len(data_start_ms)):
    res_diff_s3.append(data_arrivalS3_ms[i] - data_start_ms[i])

print("Average of latency from MockUp > AWS S3 in ms = ", mean(res_diff_s3))
print("Minimum latency from MockUp > AWS S3 in ms = ", min(res_diff_s3))
print("Maximum latency from MockUp > AWS S3 in ms = ", max(res_diff_s3))

# Convert into timestamp with ms and extract arrival time
with open(fileArrivalTimestream, mode='r') as file4:
    csv_Timestream = csv.reader(file4)
    str_arrivalTime_ms = sorted([row[0] for row in csv_Timestream][1:])
    sub_str_arrivalTime_ms = [row[:26] for row in str_arrivalTime_ms]
    dates_timestream = [(datetime.datetime.strptime(i,"%Y-%m-%d %H:%M:%S.%f").timestamp()*1000) + ONE_HOUR for i in sub_str_arrivalTime_ms]


res_diff_timestream = []
for i in range(len(data_start_ms)):
    res_diff_timestream.append(dates_timestream[i] - data_start_ms[i])

print("Average of latency from MockUp > AWS Timestream in ms = ", mean(res_diff_timestream))
print("Minimum latency from MockUp > AWS Timestream in ms = ", min(res_diff_timestream))
print("Maximum latency from MockUp > AWS Timestream in ms = ", max(res_diff_timestream))
