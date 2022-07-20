from pyspark.sql import SparkSession
from pyspark.sql.types import *
import json
import os
appName = "Testing the Stream with Kafka"

# Set spark environments
#os.environ['PYSPARK_PYTHON'] = '/usr/bin/python3'
#os.environ['PYSPARK_DRIVER_PYTHON'] = '/usr/bin/python3'

def update_spark_log_level(spark):
    spark.sparkContext.setLogLevel('error')
    log4j = spark._jvm.org.apache.log4j
    logger = log4j.LogManager.getLogger("my logger")
    return logger


# 3.1.2 geht auch
def stream_testing():
    # create Spark Session
    spark: SparkSession = SparkSession \
        .builder \
        .master("spark://spark-master:7077") \
        .config("spark.jars.packages", "org.apache.spark:spark-sql-kafka-0-10_2.12:3.1.0") \
        .appName(appName) \
        .getOrCreate()
    logger = update_spark_log_level(spark)
    sc = spark.sparkContext

    logger.info("++++++Printing Schema++++++")
    # load schema
    json_schema = {
        "orderNr": "12006000",
        "senderName": "S7-1200",
        "operation": "Read",
        "dataType": "Boolean",
        "data": "true"
    }
    json_str = json.dumps(json_schema)

    rdd_json = sc.parallelize([json_str])
    df = spark.read.json(rdd_json)
    df.show()

    ###############################

    schema = StructType() \
        .add("orderNr", StringType()) \
        .add("senderName", StringType()) \
        .add("operation", StringType()) \
        .add("dataType", StringType()) \
        .add("data", StringType())

    logger.info("++++++Reading Stream from Kafka++++++")
    df = spark \
        .readStream \
        .format("kafka") \
        .option("kafka.bootstrap.servers", "kafka:9092") \
        .option("subscribe", "plcDataString") \
        .option("startingOffsets", "earliest") \
        .load()
    logger.info("++++++Streaming Status++++++")
    streaming_status = df.isStreaming
    print(streaming_status)
    df = df.selectExpr("CAST(key AS STRING)", "CAST(topic AS STRING)", "CAST(partition AS STRING)",
                       "CAST(offset AS STRING)", "CAST(value AS STRING)")
    df.writeStream.format("console").outputMode("update").option("truncate", False).start().awaitTermination()

    # df1 = df.selectExpr("CAST(value AS STRING)").select(from_json(col("value"), schema).alias("data")).select("data.*")
    # df1.printSchema()
    # df1.writeStream.format("console").outputMode("update").option("truncate", False).start().awaitTermination()

    # df.printSchema()
    # logger.info("++++++Streaming Status++++++")
    # streaming_status = df.isStreaming
    # print(streaming_status)

    # Struct type oder Map Type
    # plc_df = df.selectExpr("CAST(value AS STRING)")
    # plc_data = plc_df.select(from_json(col("value").cast("string"), schema)).alias("data").select("*")
    # logger.info("++++++Printing from Kafka++++++")
    # plc_data.writeStream.format("console").outputMode("append").start().awaitTermination()
    # query = df.selectExpr("CAST(value AS STRING)") \
    #    .writeStream \
    #    .format("console") \
    #    .start()
    # query.awaitTermination()
    # ToDo: Check Avro Schema
    # Should be later, check the connection to kafka first

    # ToDo: create Dataframe and reading Data from Kafka
    # https://spark.apache.org/docs/latest/structured-streaming-kafka-integration.html#deploying
    # ...express streaming aggregations, event-time windows, stream-to-batch joins

    # ToDo: Monitor Streaming queries -
    #  https://spark.apache.org/docs/latest/structured-streaming-programming-guide.html#monitoring-streaming-queries


############APPLICATION START##########
stream_testing()
