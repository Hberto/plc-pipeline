package KafkaPLCProducer;

// Kafka Imports
import ReadOperations.ReadOperation;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.LongSerializer;


import KafkaPLCProducer.producer.PLCData;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

//ttetsts
//ToDo: Class Comments
public class AvroProducer {

    //Configs for Kafka
    public final static String TOPIC = "plcDataTest2";
    public final static String BOOTSTRAP_SERVERS = "localhost:29092";
    public final static String CLIENT_ID = "testAuftrag";

    private static final Logger log = LoggerFactory.getLogger(AvroProducer.class);


    public void runProducer( PLCData data) {
        Producer<Long, PLCData> producer = createProducer();
        //Producer<Long, String> producer = createProducer();

        try{
            ProducerRecord<Long, PLCData> record = new ProducerRecord<>(TOPIC,data);
            //ProducerRecord<Long, String> record = new ProducerRecord<>(TOPIC, 0L, "++Hey it worked++");

            producer.send(record);
            producer.flush();
            producer.close();
            /**
            (recordMetadata, e) -> {
                System.out.println("BROKER RECEIVED Details: \n"
                        + "Topic: " + recordMetadata.topic() + "\n"
                        + "Partition: " + recordMetadata.partition() + "\n"
                        + "Timestamp: " + recordMetadata.timestamp() + "\n");
            });**/
            log.info("Producer sent record!");
        }
        catch (Exception e) {
            log.error("--------->Exception at sending from Producer Operation<-----------");
            e.printStackTrace();
            producer.close();
        }
    }

    /**
     * Private Methods
     */


    private static Producer<Long, PLCData> createProducer() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        properties.put(ProducerConfig.CLIENT_ID_CONFIG, CLIENT_ID);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, AvroSerializerGeneric.class.getName());
        //properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return new KafkaProducer<>(properties);
    }

}
