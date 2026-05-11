package KafkaMetrics;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Utility class for reading and logging Kafka producer and consumer metrics.
 * See available metrics: https://kafka.apache.org/documentation/#monitoring
 * @author Herberto Werner
 */
public class KafkaMetric {

    private static final Logger log = LoggerFactory.getLogger(KafkaMetric.class);

    /**
     * Logs selected metrics from a Kafka Producer or Consumer.
     * @param prod the Kafka producer (may be null)
     * @param cons the Kafka consumer (may be null)
     */
    public static void printMetrics(Producer<?, ?> prod, Consumer<?, ?> cons) {
        if (prod != null) {
            log.info("Reading producer metrics");
            for (Map.Entry<MetricName, ? extends Metric> entry : prod.metrics().entrySet()) {
                String name = entry.getKey().name();
                switch (name) {
                    case "request-latency-avg":
                    case "request-latency-max":
                    case "connection-count":
                    case "record-queue-time-avg":
                    case "record-queue-time-max":
                        log.info("{}: {}", name, entry.getValue().metricValue());
                        break;
                }
            }
        }

        if (cons != null) {
            log.info("Reading consumer metrics");
            for (Map.Entry<MetricName, ? extends Metric> entry : cons.metrics().entrySet()) {
                String name = entry.getKey().name();
                switch (name) {
                    case "fetch-latency-avg":
                    case "fetch-rate":
                    case "records-consumed-rate":
                        log.info("{}: {}", name, entry.getValue().metricValue());
                        break;
                }
            }
        }
    }

}
