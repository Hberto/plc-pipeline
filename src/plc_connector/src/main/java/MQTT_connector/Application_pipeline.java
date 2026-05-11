package MQTT_connector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Application entry point for the MQTT-Kafka Bridge.
 * Configure HOST and TOPIC via environment variables MQTT_HOST and MQTT_TOPIC.
 * @author Herberto Werner
 */
public class Application_pipeline {

    private static final Logger log = LoggerFactory.getLogger(Application_pipeline.class);
    private static final String HOST = System.getenv().getOrDefault("MQTT_HOST", "localhost");
    private static final int PORT = Integer.parseInt(System.getenv().getOrDefault("MQTT_PORT", "1883"));
    private static final String TOPIC = System.getenv().getOrDefault("MQTT_TOPIC", "plc/data");

    public static void main(String[] args) {
        log.info("Starting MQTT-Kafka Bridge");
        MQTT_Kafka_Bridge mqttClient = new MQTT_Kafka_Bridge(HOST, PORT);
        mqttClient.connect();
        mqttClient.subscribeMsg(TOPIC, 1);
    }

}
