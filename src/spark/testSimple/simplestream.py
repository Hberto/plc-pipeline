from pyspark.sql import SparkSession

appName = "Testing the Stream with Kafka"
topic = "plcDataTest2"
bootstrap_server = "bla:9092"
path = "C:/Users/Herberto/Desktop/UNI/Bachelor-Arbeit/plc-pipeline/docker_containers/spark/teste.txt"


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
        .config("spark.jars.packages", "org.apache.spark:spark-sql-kafka-0-10_2.12:3.1.2") \
        .appName(appName) \
        .getOrCreate()

    # spark.sparkContext._jsc.addJar("/home/herberto/Schreibtisch/BA/spark-sql-kafka-0-10_2.13-3.3.0.jar")
    # Alternative 2
    logger = update_spark_log_level(spark)
    # sc = spark.sparkContext
    logger.info("++++++Starting Job++++++")

    logger.info("++++++Reading Stream from Kafka++++++")
    df = spark \
        .readStream \
        .format("kafka") \
        .option("kafka.bootstrap.servers", "localhost:29092") \
        .option("subscribe", "plcDataString") \
        .load()

    logger.info("++++++Printing from Kafka++++++")
    query = df.selectExpr("CAST(value AS STRING)")
    print(query)

    #query = df.selectExpr("CAST(value AS STRING)") \
    #    .writeStream \
    #    .format("console") \
    #    .start()
    #query.awaitTermination()
    # ToDo: Check Avro Schema
    # Should be later, check the connection to kafka first

    # ToDo: create Dataframe and reading Data from Kafka
    # https://spark.apache.org/docs/latest/structured-streaming-kafka-integration.html#deploying
    # ...express streaming aggregations, event-time windows, stream-to-batch joins

    # ToDo: Monitor Streaming queries -
    #  https://spark.apache.org/docs/latest/structured-streaming-programming-guide.html#monitoring-streaming-queries

    # ToDo: Write StreamingQuery to datenbank

    ###### Useful Tips ######
    # Note, you can identify whether a DataFrame/Dataset has streaming data or not by using
    # df.isStreaming


def run_spark_job_example(spark) -> None:
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
