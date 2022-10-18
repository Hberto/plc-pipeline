package KafkaPLCProducer.StringFormat;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class StringProducer {

    //Configs for Kafka
    public final static String TOPIC = "strings";
    public final static String BOOTSTRAP_SERVERS = "89.58.43.63:29092";
    public final static String CLIENT_ID = "testAuftrag";
    private static final Logger log = LoggerFactory.getLogger(StringProducer.class);

    public StringProducer() {}

    public void runProducerString() {
        Producer<String, String> producer = createProducerSimple();

        try {

            log.info("Producer created......");
            ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC, "test123","test");
            producer.send(record);
            producer.flush();
            producer.close();
            log.info("Producer sent record!");
        } catch (Exception e) {
            log.error("--------->Exception at sending from Producer Operation<-----------");
            e.printStackTrace();
            producer.close();
        }
    }
        /**
         * Private Methods
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
