from pyspark.sql import SparkSession
from pyspark.sql.types import *
import csv
import datetime
import pandas as pd

# Kafka Parameters and Configs
appNameKafka = "Testing the Stream with Kafka"

# Measurement Parameters

# Kafka Measurements Variables
header_kafka = ['QueueTSMeasurementNr','TSDateformat', 'TSHourFormat', 'TSLongformat']
my_file_kafka = '/scripts/kafkaQueue.csv'

# Spark Measurement Variables
header_spark = ['SparkProcessingTimeMeasureNr', 'TSDateformat', 'ClockTime1', 'ClockTime2', 'ProcessingTimeLong']
my_file_spark = '/scripts/sparkProcess.csv'



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

# Updating Log Level
def update_spark_log_level(spark):
    spark.sparkContext.setLogLevel('error')
    log4j = spark._jvm.org.apache.log4j
    logger = log4j.LogManager.getLogger("my logger")
    return logger


# Connection Configs to Cassandra
def writeToCassandra(writeDF, epochId):
    writeDF.write \
        .format("org.apache.spark.sql.cassandra")\
        .mode('append')\
        .options(table="test", keyspace="test")\
        .save()

# Function for experiments
def stream_testing():

    # Create Measurment CSVs
    create_timestamp_with_header_csv(my_file_kafka, header_kafka)
    create_timestamp_with_header_csv(my_file_spark,header_spark)

    # Create Spark Session for Kafka
    spark: SparkSession = SparkSession \
        .builder \
        .master("spark://spark-master:7077") \
        .config("spark.cassandra.connection.host","cassandra")\
        .config("spark.cassandra.connection.port","9042")\
        .config("spark.cassandra.auth.username","cassandra")\
        .config("spark.cassandra.auth.password","cassandra")\
        .config("spark.eventLog.enabled","true")\
        .config("spark.eventLog.dir","file:///spark-events")\
        .config("spark.history.fs.logDirectory","file:///spark-events")\
        .config("spark.jars.packages", "org.apache.spark:spark-sql-kafka-0-10_2.12:3.3.1,com.datastax.spark:spark-cassandra-connector_2.12:3.0.0,ch.cern.sparkmeasure:spark-measure_2.12:0.19")\
        .appName(appNameKafka) \
        .getOrCreate()
    logger = update_spark_log_level(spark)
    sc = spark.sparkContext
    
    # Measurement 1 Spark Processing time
    tsSparkprocessDate1 = datetime.datetime.now()
    tsSpark1 = tsSparkprocessDate1.timestamp()


    # Read from Kafka Stream und save into dataframe
    logger.info("++++++Reading Stream from Kafka++++++")
    df = spark \
        .readStream \
        .format("kafka") \
        .option("kafka.bootstrap.servers", "kafka:9092") \
        .option("subscribe", "12003800_test") \
        .option("startingOffsets", "earliest") \
        .load()

    # For Latency Measurement: Writing back to Kafka
    query_toKafka = df \
        .selectExpr("CAST(key AS STRING)","CAST(value AS STRING)") \
        .writeStream \
        .format("kafka") \
        .option("kafka.bootstrap.servers", "kafka:9092") \
        .option("topic", "12003800_test2") \
        .option("checkpointLocation", "/tmp") \
        .start()
    
    # Measurment of KafkaQueueTS
    ts_Kafka_Queue = datetime.datetime.now()
    ts_Kafka_Queue_long = ts_Kafka_Queue.timestamp()

    str_date = str(ts_Kafka_Queue)
    date_time_split = str_date.split(" ")
    date_format = date_time_split[0]
    hour_format = date_time_split[1]

    # Put into csv
    data_kafka = ['1', date_format, hour_format , ts_Kafka_Queue_long]
    write_row(data_kafka, my_file_kafka)


    logger.info("++++++Streaming Status++++++")
    streaming_status = df.isStreaming
    print(streaming_status)

    logger.info("++++++Select and Processing Section++++++")
    df = df.selectExpr("CAST(topic as STRING)","CAST(value AS STRING)", "CAST(timestamp AS Timestamp)")
    df.printSchema()
    
    # Change Column Type
    df_new = df.withColumn("value", df["value"].cast(IntegerType()))
    df_new.printSchema()

    print("++Running Kafka-Spark-Cassandra Stream++")
    query = df_new.writeStream \
        .trigger(processingTime="0 seconds") \
        .outputMode("append") \
        .foreachBatch(writeToCassandra) \
        .start()

    # Measurement 2 Spark Processing time
    tsSparkprocessDate2 = datetime.datetime.now()
    tsSpark2 = tsSparkprocessDate2.timestamp()

    # Measurment of SparkProcessingTime
    str_date1 = str(tsSparkprocessDate1)
    str_date2 = str(tsSparkprocessDate2)
    date_time_split1 = str_date1.split(" ")
    date_time_split2 = str_date2.split(" ")
    date_format1 = date_time_split1[0]

    hour_format1 = date_time_split1[1]
    hour_format2 = date_time_split2[1]
    difference_execute_time = tsSpark2 - tsSpark1

    # Put into csv
    data_spark = ['1', date_format1, hour_format1 , hour_format2, difference_execute_time]
    write_row(data_spark, my_file_spark)
    print("++Running Query Stream..waiting for Termination++")
    query.awaitTermination()


############APPLICATION START##########
stream_testing()
