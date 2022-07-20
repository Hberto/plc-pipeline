from pyspark.sql import SparkSession, Row
from pyspark.sql.functions import from_json, col
from pyspark.sql.types import *
import json

appName = "Testing the Stream with Kafka"

def update_spark_log_level(spark):
    spark.sparkContext.setLogLevel('error')
    log4j = spark._jvm.org.apache.log4j
    logger = log4j.LogManager.getLogger("my logger")
    return logger



def stream_teste():
    # create Spark Session
    spark: SparkSession = SparkSession \
        .builder \
        .config("spark.jars.packages", "org.apache.spark:spark-sql-kafka-0-10_2.12:3.1.0") \
        .appName(appName) \
        .getOrCreate()

    logger = update_spark_log_level(spark)

    df = spark \
        .readStream \
        .format("kafka") \
        .option("kafka.bootstrap.servers", "kafka:9092") \
        .option("subscribe", "strings") \
        .load()

    df = df.selectExpr("CAST(key AS STRING)", "CAST(topic AS STRING)", "CAST(partition AS STRING)",
                       "CAST(offset AS STRING)", "CAST(value AS STRING)")
    df.writeStream.format("console").outputMode("update").option("truncate", False).start().awaitTermination()