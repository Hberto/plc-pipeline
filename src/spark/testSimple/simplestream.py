import json

import pandas as pd
import requests
from pyspark.sql import SparkSession
from pyspark import SparkFiles

appName = "Testing the Stream with Kafka"
topic = "plcDataTest2"
bootstrap_server = "bla:9092"
path = "C:/Users/Herberto/Desktop/UNI/Bachelor-Arbeit/plc-pipeline/docker_containers/spark/teste.txt"
csv_url = "https://raw.githubusercontent.com/lrjoshi/webpage/master/public/post/c159s.csv"

def update_spark_log_level(spark):
    spark.sparkContext.setLogLevel('info')
    log4j = spark._jvm.org.apache.log4j
    logger = log4j.LogManager.getLogger("my logger")
    return logger


def stream_testing():
    # create Spark Session
    spark: SparkSession = SparkSession \
        .builder \
        .master("spark://127.0.0.1:7077") \
        .appName(appName) \
        .getOrCreate()


    # Alternative 2
    # sc = spark.sparkContext
    # sc.setLogLevel(sc, 'info')
    logger = update_spark_log_level(spark)
    logger.info("what")

    from pyspark.sql.types import IntegerType
    from pyspark.sql.types import StructField
    from pyspark.sql.types import StructType

    rows = [
        [1, 100],
        [2, 200],
        [3, 300],
    ]

    schema = StructType(
        [
            StructField("id", IntegerType(), True),
            StructField("salary", IntegerType(), True),
        ]
    )

    df = spark.createDataFrame(rows, schema=schema)

    highest_salary = df.agg({"salary": "max"}).collect()[0]["max(salary)"]

    second_highest_salary = (
        df.filter(f"`salary` < {highest_salary}")
        .orderBy("salary", ascending=False)
        .select("salary")
        .limit(1)
    )

    second_highest_salary.show()

    # ToDo: Check Avro Schema
    # Should be later, check the connection to kafka first

    # ToDo: create Dataframe and reading Data from Kafka
    # https://spark.apache.org/docs/latest/structured-streaming-kafka-integration.html#deploying
    # ...express streaming aggregations, event-time windows, stream-to-batch joins

    # ToDo: Do pyspark streams with spark video

    # ToDo: Monitor Streaming queries -
    #  https://spark.apache.org/docs/latest/structured-streaming-programming-guide.html#monitoring-streaming-queries

    # ToDo: Write StreamingQuery to datenbank

    ###### Useful Tips ######
    # Note, you can identify whether a DataFrame/Dataset has streaming data or not by using
    # df.isStreaming
