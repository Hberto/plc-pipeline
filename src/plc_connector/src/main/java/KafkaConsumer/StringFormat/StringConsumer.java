package KafkaConsumer.StringFormat;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.Collections;
import java.time.Duration;

/**
 * A simple Kafka Consumer with configurable connection parameters.
 * Configure via environment variables: KAFKA_BOOTSTRAP_SERVERS, KAFKA_TOPIC, KAFKA_GROUP_ID.
 * @author Herberto Werner
 */
public class StringConsumer {

    private static final String BOOTSTRAP_SERVERS = System.getenv().getOrDefault("KAFKA_BOOTSTRAP_SERVERS", "localhost:29092");
    private static final String GROUP_ID = System.getenv().getOrDefault("KAFKA_GROUP_ID", "plcpipeline");
    private static final String TOPIC = System.getenv().getOrDefault("KAFKA_TOPIC", "plc/data");
    private static final Logger log = LoggerFactory.getLogger(StringConsumer.class);

    private Consumer<String, String> consumer = null;
    private String val;

    /**
     * Constructor — creates a new Kafka Consumer.
     */
    public StringConsumer() {
        consumer = createConsumer();
    }

    /**
     * Polls for new records and processes them.
     */
    public void runConsumer() {
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
        for (ConsumerRecord<String, String> record : records) {
            log.info("Received record — topic: {}, size: {} bytes", record.topic(), record.serializedValueSize());
            setRecordValue(record.value());
        }
    }

    /**
     * Creates a Kafka Consumer with configured properties.
     * @return configured Consumer instance
     */
    private static Consumer<String, String> createConsumer() {
        final Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, "1");

        Consumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(TOPIC));
        return consumer;
    }

    public void setRecordValue(String val) {
        this.val = val;
    }

    public String getRecordValue() {
        return val;
    }

}
