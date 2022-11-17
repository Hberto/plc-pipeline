package MQTT_connector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Application Starter for the MQTT Kafka Bridge.
 * @author Herberto Werner
 */
public class Application_pipeline {

    private static final Logger log = LoggerFactory.getLogger(Application_pipeline.class);
    private static final String HOST = "89.58.55.209";
    private static final int PORT = 1883;



    public static void main(String[] args) {
        log.info("Starting MQTT_KAFKA_BRDIGE");
        MQTT_Kafka_Bridge mqttClient = new MQTT_Kafka_Bridge(HOST,PORT);
        mqttClient.connect();
        mqttClient.subscribeMsg("12003800_test",0);
        mqttClient.sendToPLC();
    }

}
