import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import KafkaPLCProducer.StringFormat.StringProducer;
import KafkaConsumer.StringFormat.StringConsumer;

/**
 * Entry point for the PLC connector application.
 * Continuously generates simulated PLC data and publishes it to Kafka.
 * @author Herberto Werner
 */
public class ApplicationStarter {

    private static final Logger log = LoggerFactory.getLogger(ApplicationStarter.class);
    private static final int SEND_INTERVAL_MS = 10_000;
    private static final int VALUE_MAX = 1000;

    public static void main(String[] args) {
        try {
            fire();
        } catch (Exception e) {
            log.error("Fatal error in ApplicationStarter", e);
        }
    }

    synchronized public static void fire() throws InterruptedException {
        StringProducer prod = new StringProducer();
        StringConsumer cons = new StringConsumer();
        String topic = System.getenv().getOrDefault("KAFKA_TOPIC", "plc/data");

        while (true) {
            int value = (int) (Math.random() * VALUE_MAX);
            prod.runProducerString(topic, "plc", String.valueOf(value));
            cons.runConsumer();
            Thread.sleep(SEND_INTERVAL_MS);
        }
    }

}
