package KafkaConsumer.StringFormat;

import org.apache.kafka.clients.consumer.ConsumerConfig;  
import org.apache.kafka.clients.consumer.ConsumerRecord;  
import org.apache.kafka.clients.consumer.ConsumerRecords;  
import org.apache.kafka.clients.consumer.KafkaConsumer;  
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.serialization.StringDeserializer;  
import org.slf4j.Logger;  
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.Collections;
import java.time.Duration;  

public class StringConsumer {

    //Configs for Kafka
    private final static String BOOTSTRAP_SERVERS = "localhost:29092";
    private final static String GROUP_ID = "plcpipeline";
    private final static String TOPIC = "12003800_test2";
    private static final Logger log = LoggerFactory.getLogger(StringConsumer.class);

    private Consumer <String, String> consumer = null;

    public StringConsumer() {
        consumer = createConsumer();
    }

    public void runConsumer() {
        ConsumerRecords<String,String> records=consumer.poll(Duration.ofMillis(100));
        for(ConsumerRecord<String, String> record : records) {
            System.out.println("+++CONSUMER: "+ "Topic: " + record.topic() + "Timestamp in ms: " + record.timestamp());
        } 
    }


     private static Consumer<String, String> createConsumer() {
      final Properties props = new Properties();
      props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
      props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
      props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
      props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

      // Create the consumer using props
      Consumer<String, String> consumer = new KafkaConsumer<>(props);

      // Subscribe to the topic.
      consumer.subscribe(Collections.singletonList(TOPIC));
      return consumer;
     }


    
}
