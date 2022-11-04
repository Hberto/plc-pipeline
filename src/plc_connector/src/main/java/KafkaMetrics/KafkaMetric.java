package KafkaMetrics;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.metrics.Metrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * A simple Class to get and print Metrics from a Kafka Broker/Consumer/Producer.
 * All Metrics to print are static.
 * Possible features in the future:
 * #1 Pass Map of Metrics
 * #2 adding Metrics by their name into map
 * #3 iterate through map and compare to the prod/cons map
 * Infos about the metrics class:
 * https://kafka.apache.org/31/javadoc/org/apache/kafka/common/metrics/package-summary.html
 */
public class KafkaMetric {

    private static final Logger log = LoggerFactory.getLogger(KafkaMetric.class);

    /**
     * Prints statically Metrics from a Kafka Producer or Kafka Consumer.
     * See available Metrics here:
     * https://kafka.apache.org/documentation/#monitoring
     * https://docs.confluent.io/platform/current/kafka/monitoring.html#monitoring-ak
     * @param prod
     * @param cons
     */
    public static void printMetrics(Producer<?,?> prod, Consumer<?,?> cons) {
        //Change Metrics for own use
        Metric requestRateAvg = null;
        Metric connCount = null;
        Metric requestLatMax = null;
        Metric recordQueTimeAvg = null;
        Metric recordQueTimeMax = null;
        Metric requestLatAvg = null;

        if (prod != null) {
            for (Map.Entry<MetricName, ? extends Metric> entry : prod.metrics().entrySet()) {
                log.info("+++Reading Producer Metrics: +++");
                if ("request-latency-avg".equals(entry.getKey().name())) {
                    requestRateAvg = entry.getValue();
                    System.out.println(requestRateAvg.metricValue());
                }
                if ("connection-count".equals(entry.getKey().name())) {
                    connCount = entry.getValue();
                    System.out.println(connCount.metricValue());
                }
                if ("request-latency-max".equals(entry.getKey().name())) {
                    requestLatMax = entry.getValue();
                    System.out.println(requestLatMax.metricValue());
                }
                if ("record-queue-time-avg".equals(entry.getKey().name())) {
                    recordQueTimeAvg = entry.getValue();
                    System.out.println(recordQueTimeAvg.metricValue());
                }
                if ("record-queue-time-max".equals(entry.getKey().name())) {
                    recordQueTimeMax = entry.getValue();
                    System.out.println(recordQueTimeMax.metricValue());
                }
                if ("request-latency-avg".equals(entry.getKey().name())) {
                    requestLatAvg = entry.getValue();
                    System.out.println(requestLatAvg.metricValue());
                }
            }
        }

        if(cons != null) {
            //No Metrics defined yet..
            log.warn("No Metrics yet.");
        }
    }

}
