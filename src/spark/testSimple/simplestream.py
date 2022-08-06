from pyspark.sql import SparkSession
from pyspark.sql.types import *
import json
import os
# Kafka Parameters and Configs
appNameKafka = "Testing the Stream with Kafka"

#Mongo Parameters and and Config
appNameMongoDB = "Testing the Connection to MongoDB"
input_uri = "mongodb://root:example@mongodb:27017"
output_uri = "mongodb://root:example@mongodb:27017"


def update_spark_log_level(spark):
    spark.sparkContext.setLogLevel('error')
    log4j = spark._jvm.org.apache.log4j
    logger = log4j.LogManager.getLogger("my logger")
    return logger



def stream_testing():
    # create Spark Session for Kafka
    spark: SparkSession = SparkSession \
        .builder \
        .master("spark://spark-master:7077") \
        .config("spark.mongodb.input.uri", input_uri) \
        .config("spark.mongodb.output.uri", output_uri) \
        .config("spark.jars.packages", "org.apache.spark:spark-sql-kafka-0-10_2.12:3.1.2,org.mongodb.spark:mongo-spark-connector_2.12:3.0.1") \
        .appName(appNameKafka) \
        .getOrCreate()
    logger = update_spark_log_level(spark)
    sc = spark.sparkContext

    # create Spark Session for MongoDB
 #   spark_mongo: SparkSession = SparkSession \
 #       .builder \
 #       .appName(appNameMongoDB) \
 #       .config("spark.mongodb.input.uri", input_uri) \
 #       .config("spark.mongodb.output.uri", output_uri) \
 #       .config("spark.jars.packages","org.mongodb.spark:mongo-spark-connector_2.12:3.0.1") \
 #       .getOrCreate()

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
        .option("subscribe", "strings") \
        .option("startingOffsets", "earliest") \
        .load()


    logger.info("++++++Streaming Status++++++")
    streaming_status = df.isStreaming
    print(streaming_status)
    df = df.selectExpr("CAST(key AS STRING)", "CAST(topic AS STRING)", "CAST(partition AS STRING)",
                       "CAST(offset AS STRING)", "CAST(value AS STRING)")
    #df.writeStream.format("console").outputMode("update").option("truncate", False).start().awaitTermination()

    
    # Dataframe with jsonSchema
    # df1 = df.selectExpr("CAST(value AS STRING)").select(from_json(col("value"), schema).alias("data")).select("data.*")
    # df1.printSchema()
    # df1.writeStream.format("console").outputMode("update").option("truncate", False).start().awaitTermination()

    
    # mongodb write example
    print("+++++++++++MongoDB write Example+++++++++++")
    people = spark.createDataFrame([("Bilbo Baggins",  50), ("Gandalf", 1000), ("Thorin", 195), ("Balin", 178), ("Kili", 77),
   ("Dwalin", 169), ("Oin", 167), ("Gloin", 158), ("Fili", 82), ("Bombur", None)], ["name", "age"])

    people.write \
    .format("com.mongodb.spark.sql.DefaultSource") \
    .mode("append") \
    .option("uri", output_uri)\
    .option("database", "test")\
    .option("collection", "collectionTest")\
    .save()

    people.show()
    people.printSchema()

    # mongodb read example
    logger.info("++++++MongoDB read Example++++++")
    dfMongoExample = spark.read \
    .format("com.mongodb.spark.sql.DefaultSource") \
    .option("uri", input_uri)\
    .option("database", "test")\
    .option("collection", "collectionTest")\
    .load()

    dfMongoExample.filter(dfMongoExample["age"] >= 150).show()



    # Alternative Dataframe with jsonSchema
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
    
    # ToDo: create Dataframe and reading Data from Kafka
    # https://spark.apache.org/docs/latest/structured-streaming-kafka-integration.html#deploying
    # ...express streaming aggregations, event-time windows, stream-to-batch joins

    # ToDo: Monitor Streaming queries -
    #  https://spark.apache.org/docs/latest/structured-streaming-programming-guide.html#monitoring-streaming-queries


############APPLICATION START##########
stream_testing()
