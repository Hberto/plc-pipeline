import os
from pyspark.sql import SparkSession
from pyspark.sql.types import StringType


def update_spark_log_level(spark):
    spark.sparkContext.setLogLevel("ERROR")
    return spark._jvm.org.apache.log4j.LogManager.getLogger(__name__)


def write_to_cassandra(batch_df, _):
    batch_df.write \
        .format("org.apache.spark.sql.cassandra") \
        .mode("append") \
        .options(table="test", keyspace="test") \
        .save()


def stream_testing():
    spark = SparkSession \
        .builder \
        .master("spark://spark-master:7077") \
        .config("spark.cassandra.connection.host", os.environ.get("CASSANDRA_HOST", "cassandra")) \
        .config("spark.cassandra.connection.port", os.environ.get("CASSANDRA_PORT", "9042")) \
        .config("spark.cassandra.auth.username", os.environ.get("CASSANDRA_USER", "cassandra")) \
        .config("spark.cassandra.auth.password", os.environ.get("CASSANDRA_PASSWORD", "cassandra")) \
        .config("spark.jars.packages",
                "org.apache.spark:spark-sql-kafka-0-10_2.12:3.0.0,"
                "com.datastax.spark:spark-cassandra-connector_2.12:3.0.0") \
        .appName("PLC Data Pipeline") \
        .getOrCreate()

    logger = update_spark_log_level(spark)
    logger.info("Starting PLC data stream")

    df = spark \
        .readStream \
        .format("kafka") \
        .option("kafka.bootstrap.servers", os.environ.get("KAFKA_BOOTSTRAP_SERVERS", "kafka:9092")) \
        .option("subscribe", os.environ.get("KAFKA_TOPIC", "strings")) \
        .option("startingOffsets", "earliest") \
        .load() \
        .selectExpr(
            "CAST(key AS STRING)",
            "CAST(topic AS STRING)",
            "CAST(partition AS STRING)",
            "CAST(offset AS STRING)",
            "CAST(value AS STRING)"
        )

    logger.info("Stream started — writing to Cassandra")

    df.writeStream \
        .foreachBatch(write_to_cassandra) \
        .outputMode("update") \
        .start() \
        .awaitTermination()


if __name__ == "__main__":
    stream_testing()
