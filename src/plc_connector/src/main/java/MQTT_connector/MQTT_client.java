package MQTT_connector;

import org.eclipse.paho.mqttv5.client.*;

import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

/**
 * This class is a wrapper of the mqtt paho client.
 * The wrapper connects, disconnects, subscribes and publishes topic with the mqtt paho payload
 * @author Herberto Werner
 */
public class MQTT_client implements MqttCallback {

    private static final String CLIENT_ID = "mqtt_pipeline";
    private static final int QOS_LEVEL_0_FIRE_AND_FORGET = 0;
    private static final int QOS_LEVEL_1_AT_LEAST_ONCE = 1;

    private MqttClient mqttClient;
    private MqttConnectionOptions connOpts;
    private static final Logger log = LoggerFactory.getLogger(MQTT_client.class);

    /**
     * Simple Constructor of the Mqtt Client which creates a new Mqtt Client.
     * @param host the host/broker as IP-Address
     * @param port the port of the host/broker
     */
    public MQTT_client(String host, int port) {

        if (host == null) {
            throw new IllegalArgumentException(" Parameter 'host' can't be null");
        }
        try {
            this.mqttClient = new MqttClient("tcp://"
                    + host + ":"
                    + port,
                    CLIENT_ID);
        }
        catch (MqttException e) {
            log.error("Creating MQTT Client failed");
            log.error(e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * Builds a connection to the broker/host with Connection Options.
     */
    public void connect(){

        try {
            connOpts = new MqttConnectionOptions();
            connOpts.setAutomaticReconnect(true);
            connOpts.setCleanStart(false);
            this.mqttClient.setCallback(this);
            this.mqttClient.connect(connOpts);
        }
        catch(MqttException e) {
            log.error("Building a Connection failed");
            log.error(e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * Publishes a Message to the host/broker with a topic and content
     * @param topic The topic which the mqtt client will publish to
     * @param content the Payload of the Message. Format: String
     */
    public void publishMsg(String topic, String content) {

        try {
            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(QOS_LEVEL_1_AT_LEAST_ONCE);
            this.mqttClient.publish(topic, message);
        }
        catch (MqttException e) {
            log.error("Publishing a Message failed");
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Subscribes to a topic.
     * @param topic The topic which the mqtt client will subscribe to
     */
    public void subscribeMsg(String topic) {

        try {
            this.mqttClient.subscribe(topic,QOS_LEVEL_1_AT_LEAST_ONCE);
        }
        catch (MqttException e) {
            log.error("Receiving and subscribing a Message failed");
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Disconnects the Mqtt Client.
     */
    public void disconnect(){
        try {
            this.mqttClient.disconnect();
            this.mqttClient.close();
        }
        catch(MqttException e) {
            log.error("Closing Connection failed");
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void disconnected(MqttDisconnectResponse mqttDisconnectResponse) {
        //Nothing to Do
    }

    @Override
    public void mqttErrorOccurred(MqttException e) {
        //Nothing to Do
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        log.info("THE TOPIC:  " + topic + "\n\t"
                + "MESSAGE:  " + new String (mqttMessage.getPayload(), StandardCharsets.UTF_8));

        System.out.println("THE TOPIC:  " + topic + "\n\t"
                + "MESSAGE:  " + new String (mqttMessage.getPayload(), StandardCharsets.UTF_8));
    }

    @Override
    public void deliveryComplete(IMqttToken iMqttToken) {
        //Nothing to Do
    }

    @Override
    public void connectComplete(boolean b, String s) {
        //Nothing to Do
    }

    @Override
    public void authPacketArrived(int i, MqttProperties mqttProperties) {
        //Nothing to Do
    }
}
