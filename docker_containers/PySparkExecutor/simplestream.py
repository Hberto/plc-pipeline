from pyspark.sql import SparkSession
from pyspark.sql.types import *
from pyspark.sql import functions as F

# Kafka Parameters and Configs
appNameKafka = "Testing the Stream with Kafka"

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

# For Experiments: save kafka timestamp to cassandra table
# Comment it when measuring read/write latency of cassandra
def saveKafkaTSToCassandra(writeDF, epochId):
    writeDF.write \
        .format("org.apache.spark.sql.cassandra")\
        .mode('append')\
        .options(table="kafka", keyspace="test")\
        .save()

# For Experiments: measure read latency of cassandra
def readFromCassandra(readDf, epochId):
    readDf.read \
        .format("org.apache.spark.sql.cassandra")\
        .mode('append')\
        .options(table="test", keyspace="test")\
        .load()\
        .show()\

# Function for experiments
def stream_testing():

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
    
    # Read from Kafka Stream und save into dataframe
    logger.info("++++++Reading Stream from Kafka++++++")
    df = spark \
        .readStream \
        .format("kafka") \
        .option("kafka.bootstrap.servers", "kafka:9092") \
        .option("subscribe", "12003800_test") \
        .option("startingOffsets", "earliest") \
        .load() \
        .withColumn("current_timestamp", F.current_timestamp())
        

    # For Latency Measurement: Writing back to Kafka
    query_toKafka = df \
        .selectExpr("CAST(key AS STRING)","CAST(value AS STRING)") \
        .writeStream \
        .format("kafka") \
        .option("kafka.bootstrap.servers", "kafka:9092") \
        .option("topic", "12003800_test2") \
        .option("checkpointLocation", "/tmp") \
        .start()

    
    print("++++++Select and Processing Section++++++")
    # Measurement of KafkaQueueTS
    df_kafka_ts = df.selectExpr("CAST(topic as STRING)","CAST(value AS STRING)","CAST(current_timestamp AS Timestamp)")

    # Filter Data for Cassandra
    df = df.selectExpr("CAST(topic as STRING)","CAST(value AS STRING)", "CAST(timestamp AS Timestamp)", "CAST(current_timestamp AS Timestamp)")
    # Change Column Type
    df_new = df.withColumn("value", df["value"].cast(IntegerType()))

    # Write Streams into Cassandra
    print("++Running Kafka-Spark-Cassandra Stream++")
    query = df_new.writeStream \
        .trigger(processingTime="0 seconds") \
        .outputMode("append") \
        .foreachBatch(writeToCassandra) \
        .start()

    # Comment when measuring Spark jobs duration 
    #kafka_ts_query = df_kafka_ts.writeStream \
    #    .trigger(processingTime="0 seconds") \
    #    .outputMode("append") \
    #    .foreachBatch(saveKafkaTSToCassandra) \
    #    .start()
    
    print("++Running Query Stream..waiting for Termination++")
    query.awaitTermination()


############APPLICATION START##########
stream_testing()
