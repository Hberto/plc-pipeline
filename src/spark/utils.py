import os
from pyspark.conf import SparkConf
from pyspark.sql import SparkSession

# src: https://github.com/bitnami/bitnami-docker-spark/issues/18#issuecomment-700628676
# src2: https://github.com/leriel/pyspark-easy-start/blob/master/read_file.py
def get_spark_context(app_name: str) -> SparkSession:
    conf = SparkConf()

    conf.setAll(
        [
            (
                "spark.master",
                "spark://spark-master:7077",
            ),  # <--- this host must be resolvable by the driver in this case pyspark (whatever it is located,
            # same server or remote) in our case the IP of server
            ("spark.driver.host",
             os.environ.get("SPARK_DRIVER_HOST", "local[*]")
             ),  # <--- this host is the
            # resolvable IP for the host that is running the driver, and it must be reachable by the master and
            # master must be able to reach it (in our case the IP of the container where we are running pyspark
            ("spark.submit.deployMode", "client"),
            ("spark.driver.bindAddress", "0.0.0.0"),  # <--- this host is the IP where pyspark will bind the service
            # running the driver (normally 0.0.0.0)
            ("spark.app.name", app_name),
        ]
    )

    return SparkSession.builder.config(conf=conf).getOrCreate()
