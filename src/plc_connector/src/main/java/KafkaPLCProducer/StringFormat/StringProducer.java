package KafkaPLCProducer.StringFormat;

//Imports
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.Properties;

/**
 * This class creates a simple String Kafka Producer.
 * @author Herberto Werner
 */
public class StringProducer {

    //Configs for Kafka
    private final static String BOOTSTRAP_SERVERS = "localhost:29092";
    private final static String CLIENT_ID = "plcpipeline";
    private static final Logger log = LoggerFactory.getLogger(StringProducer.class);

    private Producer<String, String> producer;

    /**
     * Constructor of the StringProducer.
     * Creates a new Producer.
     */
    public StringProducer() {
        producer = createProducerSimple();
    }

    /**
     * Creates and sends a record to the Kafka Broker.
     * @param topic the topic the producer will send to.
     * @param key key of the record.
     * @param value value of the record.
     */
    public void runProducerString(String topic, String key, String value) {

        if (topic == null) {
            throw new IllegalArgumentException(" Parameter 'topic' can't be null");
        }

        if (key == null) {
            throw new IllegalArgumentException(" Parameter 'key' can't be null");
        }

        if (value == null) {
            throw new IllegalArgumentException(" Parameter 'value' can't be null");
        }

        try {
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, key,value);
            producer.send(record);
            producer.flush();
            log.info("Producer sent record!");
        } catch (Exception e) {
            log.error("Exception at sending from Producer Operation");
            e.printStackTrace();
            producer.close();
        }
    }

    public void runProducerStringWithTS(String topic, Integer partition,Long timestamp, String key, String value) {

        if (topic == null) {
            throw new IllegalArgumentException(" Parameter 'topic' can't be null");
        }

        if (key == null) {
            throw new IllegalArgumentException(" Parameter 'key' can't be null");
        }

        if (value == null) {
            throw new IllegalArgumentException(" Parameter 'value' can't be null");
        }

        try {
            ProducerRecord<String, String> record = new ProducerRecord<>(topic,null,timestamp, key,value);
            producer.send(record);
            producer.flush();
            log.info("Producer sent record!");
        } catch (Exception e) {
            log.error("Exception at sending from Producer Operation");
            e.printStackTrace();
            producer.close();
        }
    }

    /**
     * Closes a Producer.
     */
    public void closeProducerString() {
        try {
            producer.close();
            log.info("Producer closed");
        }
        catch (Exception e) {
            log.error("Exception at closing from Producer Operation");
            e.printStackTrace();
            producer.close();
        }
    }
        /**
         * Private Methods
         */

    /**
     * Creates a KafkaProducer with Configs.
     * @return KafkaProducer with properties configs.
     */
    private static Producer<String, String> createProducerSimple() {
            Properties properties = new Properties();
            properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
            properties.put(ProducerConfig.CLIENT_ID_CONFIG, CLIENT_ID);
            properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            return new KafkaProducer<>(properties);
        }

}
