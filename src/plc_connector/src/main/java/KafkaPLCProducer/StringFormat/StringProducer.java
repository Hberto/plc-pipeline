package KafkaPLCProducer.StringFormat;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * A simple String Kafka Producer.
 * Configure via environment variables: KAFKA_BOOTSTRAP_SERVERS, KAFKA_CLIENT_ID.
 * @author Herberto Werner
 */
public class StringProducer {

    private static final String BOOTSTRAP_SERVERS = System.getenv().getOrDefault("KAFKA_BOOTSTRAP_SERVERS", "localhost:29092");
    private static final String CLIENT_ID = System.getenv().getOrDefault("KAFKA_CLIENT_ID", "plcpipeline");
    private static final Logger log = LoggerFactory.getLogger(StringProducer.class);

    private Producer<String, String> producer;

    /**
     * Constructor — creates a new Kafka Producer.
     */
    public StringProducer() {
        producer = createProducerSimple();
    }

    /**
     * Creates and sends a record to the Kafka broker.
     * @param topic the topic to publish to
     * @param key key of the record
     * @param value value of the record
     */
    public void runProducerString(String topic, String key, String value) {
        if (topic == null) throw new IllegalArgumentException("Parameter 'topic' can't be null");
        if (key == null) throw new IllegalArgumentException("Parameter 'key' can't be null");
        if (value == null) throw new IllegalArgumentException("Parameter 'value' can't be null");

        try {
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, value);
            producer.send(record);
            producer.flush();
        } catch (Exception e) {
            log.error("Failed to send record", e);
            producer.close();
        }
    }

    /**
     * Creates and sends a record with an explicit timestamp.
     * @param topic the topic to publish to
     * @param partition target partition (null for default partitioner)
     * @param timestamp record timestamp in milliseconds
     * @param key key of the record
     * @param value value of the record
     */
    public void runProducerStringWithTS(String topic, Integer partition, Long timestamp, String key, String value) {
        if (topic == null) throw new IllegalArgumentException("Parameter 'topic' can't be null");
        if (key == null) throw new IllegalArgumentException("Parameter 'key' can't be null");
        if (value == null) throw new IllegalArgumentException("Parameter 'value' can't be null");

        try {
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, partition, timestamp, key, value);
            producer.send(record);
            producer.flush();
            log.info("Record sent to topic '{}'", topic);
        } catch (Exception e) {
            log.error("Failed to send record with timestamp", e);
            producer.close();
        }
    }

    /**
     * Closes the producer.
     */
    public void closeProducerString() {
        try {
            producer.close();
            log.info("Producer closed");
        } catch (Exception e) {
            log.error("Failed to close producer", e);
        }
    }

    private static Producer<String, String> createProducerSimple() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        properties.put(ProducerConfig.CLIENT_ID_CONFIG, CLIENT_ID);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return new KafkaProducer<>(properties);
    }

}
