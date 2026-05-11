package KafkaPLCProducer.Avro;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import KafkaPLCProducer.producerData.PLCData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * Kafka Producer for PLC data using Avro serialization.
 * Configure via environment variables: KAFKA_BOOTSTRAP_SERVERS, KAFKA_CLIENT_ID.
 * @author Herberto Werner
 */
public class AvroProducer {

    private static final String TOPIC = System.getenv().getOrDefault("KAFKA_TOPIC_AVRO", "plcDataAvro");
    private static final String TOPIC_SIMPLE = System.getenv().getOrDefault("KAFKA_TOPIC_SIMPLE", "plcDataString");
    private static final String BOOTSTRAP_SERVERS = System.getenv().getOrDefault("KAFKA_BOOTSTRAP_SERVERS", "localhost:29092");
    private static final String CLIENT_ID = System.getenv().getOrDefault("KAFKA_CLIENT_ID", "plcpipeline");

    private static final Logger log = LoggerFactory.getLogger(AvroProducer.class);

    /**
     * Sends a PLCData record to the configured Avro topic.
     * @param data the PLC data to publish
     */
    public void runProducer(PLCData data) {
        Producer<Long, PLCData> producer = createProducer();
        try {
            ProducerRecord<Long, PLCData> record = new ProducerRecord<>(TOPIC, data);
            producer.send(record);
            producer.flush();
            log.info("Avro record sent to topic '{}'", TOPIC);
        } catch (Exception e) {
            log.error("Failed to send Avro record", e);
        } finally {
            producer.close();
        }
    }

    /**
     * Sends a plain String record to the configured simple topic.
     * @param data the string value to publish
     */
    public void runProducerSimple(String data) {
        Producer<Long, String> producer = createProducerSimple();
        try {
            ProducerRecord<Long, String> record = new ProducerRecord<>(TOPIC_SIMPLE, 0L, data);
            producer.send(record);
            producer.flush();
            log.info("Record sent to topic '{}'", TOPIC_SIMPLE);
        } catch (Exception e) {
            log.error("Failed to send record", e);
        } finally {
            producer.close();
        }
    }

    private static Producer<Long, PLCData> createProducer() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        properties.put(ProducerConfig.CLIENT_ID_CONFIG, CLIENT_ID);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, AvroSerializerGeneric.class.getName());
        return new KafkaProducer<>(properties);
    }

    private static Producer<Long, String> createProducerSimple() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        properties.put(ProducerConfig.CLIENT_ID_CONFIG, CLIENT_ID);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return new KafkaProducer<>(properties);
    }

}
