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

import MQTT_connector.MeasurementTimestamp;

import java.util.Properties;
import java.util.Collections;
import java.time.Duration;

import java.sql.Timestamp;
import java.util.Date;

/**
 * A simple Kafka Consumer Class with static parameters.
 * Disclaimer: Only use for latency measurement.
 * @author Herberto Werner
 */
public class StringConsumer {

    //Configs for Kafka
    private final static String BOOTSTRAP_SERVERS = "89.58.55.209:29092";
    private final static String GROUP_ID = "plcpipeline";
    private final static String TOPIC = "12003800_test2";
    private static final Logger log = LoggerFactory.getLogger(StringConsumer.class);
    private Consumer <String, String> consumer = null;

    private static final String csvFilePath = "/home/herb/BA/plc-pipeline/metrics/mqttEvaluation/KafkaMsgArrival.csv";

    private String val;

    /**
     * Constructor of the Consumer class.
     */
    public StringConsumer() {
        consumer = createConsumer();
    }

    /**
     * Runs a Consumer with static configs. Prints out basic record metadata.
     */
    public void runConsumer() {
        ConsumerRecords<String,String> records=consumer.poll(Duration.ofMillis(100));
        for(ConsumerRecord<String, String> record : records) {
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        MeasurementTimestamp.measureKafkaArrivalAndSaveToCSV(csvFilePath,ts);
        long measureMilli = ts.getTime();
        Date date = ts;
        System.out.println("++++++ARRIVAL FROM SPARK TS :"+ date.toString()
            + "\n\t"  
            + "inMs: " + measureMilli);
            //System.out.println("CONSUMER: "+ "Topic: " + record.topic() + "Timestamp in ms: " + record.timestamp() + "type:  "+record.timestampType());
            setRecordValue(record.value());
        } 
    }

    /**
     * Creates a Consumer with static Properties and returns it.
     * @return a Consumer with configs
     */
     private static Consumer<String, String> createConsumer() {
      final Properties props = new Properties();
      props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
      props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
      props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
      props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

      // Create the consumer using props
      Consumer<String, String> consumer = new KafkaConsumer<>(props);

      // Subscribe to the topic
      consumer.subscribe(Collections.singletonList(TOPIC));
      return consumer;
     }

     /**
      * Sets the Record value.
      * @param val the value to be setted.
      */
     public void setRecordValue(String val) {
        this.val = val;
     }

     /**
      * Returns the Record Value.
      * @return the record value
      */
     public String getRecordValue() {
        return val;
     }

    
}
