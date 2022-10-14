from pyspark.sql import SparkSession
from pyspark.sql.types import *
import json

# Kafka Parameters and Configs
appNameKafka = "Testing the Stream with Kafka"

# Cassandra TEMP
#        .config("spark.sql.extensions", "com.datastax.spark.connector.CassandraSparkExtensions") \
#        .config("spark.sql.catalog.casscatalog","com.datastax.spark.connector.datasource.CassandraCatalog") \
# com.datastax.spark:spark-cassandra-connector_2.12:3.0.0
# .config("spark.jars.packages", "org.apache.spark:spark-sql-kafka-0-10_2.12:3.0.0,com.datastax.spark:spark-cassandra-connector-assembly_2.12:3.2.0") \

def update_spark_log_level(spark):
    spark.sparkContext.setLogLevel('error')
    log4j = spark._jvm.org.apache.log4j
    logger = log4j.LogManager.getLogger("my logger")
    return logger

def writeToCassandra(writeDF, _):
    writeDF.write \
        .format("org.apache.spark.sql.cassandra")\
        .mode('append')\
        .options(table="strings", keyspace="test")\
        .save()

def stream_testing():
    # create Spark Session for Kafka
    spark: SparkSession = SparkSession \
        .builder \
        .master("spark://spark-master:7077") \
        .config("spark.cassandra.connection.host","cassandra")\
        .config("spark.cassandra.connection.port","9042")\
        .config("spark.cassandra.auth.username","cassandra")\
        .config("spark.cassandra.auth.password","cassandra")\
        .config("spark.jars.packages", "org.apache.spark:spark-sql-kafka-0-10_2.12:3.0.0,com.datastax.spark:spark-cassandra-connector_2.12:3.0.0") \
        .appName(appNameKafka) \
        .getOrCreate()
    logger = update_spark_log_level(spark)
    sc = spark.sparkContext


    logger.info("++++++Printing Schema++++++")
    # load schema
    #json_schema = {
    #    "orderNr": "12006000",
    #    "senderName": "S7-1200",
    #    "operation": "Read",
    #    "dataType": "Boolean",
    #    "data": "true"
    #}
    #json_str = json.dumps(json_schema)
#
    #rdd_json = sc.parallelize([json_str])
    #df = spark.read.json(rdd_json)
    #df.show()

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

    testData= [(1,1),(2,2)]

    # cassandra write example
    testSchema= StructType([
                StructField("sensor_id",IntegerType(),False),
                StructField("temperature",IntegerType(),False)])

    df1 = spark.createDataFrame(data=testData,schema=testSchema)
    df1.printSchema()
    df1.show(truncate=False)

    print("##########TESTING Cassandra############")
#    df1.write \
#        .format("org.apache.spark.sql.cassandra")\
#        .mode('append')\
#        .options(table="strings", keyspace="test")\
#        .save()

 #   df1.writeStream \
 #       .foreachBatch(writeToCassandra) \
 #       .outputMode("update") \
 #       .start()\
 #       .awaitTermination()
 #   df1.show()
    query = df1.write \
        .mode("append") \
        .format("org.apache.spark.sql.cassandra") \
        .options(table="test", keyspace="test") \
        .save()

    # ToDo: Create right schemata for plc
    # ToDo: create table like schema in cassandra
    # ToDo: integrate timestamps


############APPLICATION START##########
stream_testing()
