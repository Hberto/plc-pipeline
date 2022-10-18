package MQTT_connector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application_pipeline {

    private static final Logger log = LoggerFactory.getLogger(Application_pipeline.class);
    private static final String HOST = "89.58.43.63";
    private static final int PORT = 1883;



    public static void main(String[] args) {

        MQTT_client mqttClient = new MQTT_client(HOST,PORT);
        mqttClient.connect();
        System.out.println("Connected....");
        mqttClient.publishMsg("test","hello_world");
        System.out.println("SENT....");

        mqttClient.subscribeMsg("test");
    }
}
