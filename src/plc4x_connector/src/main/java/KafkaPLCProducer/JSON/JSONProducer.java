package KafkaPLCProducer.JSON;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.nio.file.Paths;
import java.util.Properties;

public class JSONProducer {
    //Configs for Kafka
    public final static String TOPIC_SIMPLE = "plcDataString";
    public final static String BOOTSTRAP_SERVERS = "localhost:29092";
    public final static String CLIENT_ID = "testAuftrag";

    private static final Logger log = LoggerFactory.getLogger(JSONProducer.class);

    private ObjectMapper mapper;

    public JSONProducer(){
        this.mapper = new ObjectMapper();
    }

    public void runProducer() {
        Producer<String, PLCDataSchema> producer = createProducerSimple();

        try {
           PLCDataSchema plcDataObject = mapper.readValue(Paths.get("src/resources/json/plcdata.json").toFile(),
                   PLCDataSchema.class);
           log.info("Producer created......");
            ProducerRecord<String,PLCDataSchema> record = new ProducerRecord<String, PLCDataSchema>(TOPIC_SIMPLE,plcDataObject.getOrderNr(),plcDataObject);
            producer.send(record);
            producer.flush();
            producer.close();
            log.info("Producer sent record!");
        }
        catch(Exception e){
            log.error("--------->Exception at sending from Producer Operation<-----------");
            e.printStackTrace();
            producer.close();
        }



    }

    /**
     * Private Methods
     */

    private static Producer<String, PLCDataSchema> createProducerSimple() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        properties.put(ProducerConfig.CLIENT_ID_CONFIG, CLIENT_ID);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new KafkaProducer<String,PLCDataSchema>(properties);
    }

}
