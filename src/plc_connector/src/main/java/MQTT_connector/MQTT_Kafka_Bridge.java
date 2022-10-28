package MQTT_connector;

import KafkaPLCProducer.StringFormat.StringProducer;
import org.eclipse.paho.mqttv5.client.*;

import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;

/**
 * This class is a wrapper of the mqtt paho client and a bridge between the mqtt broker and kafka broker.
 * The wrapper connects, disconnects, subscribes and publishes topic with the mqtt paho payload
 * @author Herberto Werner
 */
public class MQTT_Kafka_Bridge implements MqttCallback {

    StringProducer prod;
    //MQTT Configs
    private static final String CLIENT_ID = "mqtt_pipeline";
    private static final int QOS_LEVEL_0_FIRE_AND_FORGET = 0;
    private static final int QOS_LEVEL_1_AT_LEAST_ONCE = 1;
    private static final int QOS_LEVEL_2_EXACTLY_ONCE = 2;
    private static final String KEY = "test";

    private static final String csvFilePath = "src/main/java/MQTT_connector/timestamp_measurement/MQTTArrival.csv";

    private MqttClient mqttClient;
    private MqttConnectionOptions connOpts;

    private int msgCount;


    private static final Logger log = LoggerFactory.getLogger(MQTT_Kafka_Bridge.class);

    /**
     * Simple Constructor of the Mqtt Client which creates a new Mqtt Client.
     * @param host the host/broker as IP-Address
     * @param port the port of the host/broker
     */
    public MQTT_Kafka_Bridge(String host, int port) {
        msgCount = 0;
        if (host == null) {
            throw new IllegalArgumentException(" Parameter 'host' can't be null");
        }
        try {
            prod = new StringProducer();
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
     * Publishes a Message to the mqtt host/broker with a topic and content.
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
            disconnect();
        }
    }

    /**
     * Subscribes to a topic with a qos level.
     * @param topic The topic which the mqtt client will subscribe to.
     * @param qos qos level 0,1,2
     */
    public void subscribeMsg(String topic, int qos) {

        if (topic == null) {
            throw new IllegalArgumentException(" Parameter 'topic' can't be null");
        }

        if (qos < 0 || qos > 2 ) {
            throw new IllegalArgumentException(" Parameter 'qos' can't be below 0 or above 3");
        }

        try {
            switch(qos){
                case 0:
                    this.mqttClient.subscribe(topic,QOS_LEVEL_0_FIRE_AND_FORGET);
                    break;
                case 1:
                    this.mqttClient.subscribe(topic,QOS_LEVEL_1_AT_LEAST_ONCE);
                    break;
                case 2:
                    this.mqttClient.subscribe(topic,QOS_LEVEL_2_EXACTLY_ONCE);
                    break;
            }
        }
        catch (MqttException e) {
            log.error("Receiving and subscribing a Message failed");
            log.error(e.getMessage());
            e.printStackTrace();
            disconnect();
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

    /**
     * Sends the message to a Kafka Broker.
     * @param topic The topic the payload will be published to.
     * @param kafkaKey Key of Kafka Record.
     * @param payload Payload of Kafka Record.
     * @param withTS Integration of TS query
     * @param timestamp the timestamp to be passed
     */
    public void sendToKafka(String topic, String kafkaKey, String payload, boolean withTS, long timestamp) {

        if (topic == null) {
            throw new IllegalArgumentException(" Parameter 'topic' can't be null");
        }

        if (kafkaKey == null) {
            throw new IllegalArgumentException(" Parameter 'kafkaKey' can't be null");
        }

        if (payload == null) {
            throw new IllegalArgumentException(" Parameter 'payload' can't be null");
        }

        if(withTS){
            prod.runProducerStringWithTS(topic, null, timestamp, kafkaKey, payload);
        }
        else {
            prod.runProducerString(topic, kafkaKey, payload);
        }

    }

    /**
     * Returns the MessageCount.
     * @return the message count.
     */
    public int getMsgCount() {
        return msgCount;
    }

    /**
     * Sets the MessageCount.
     * @param msgCount the message count to be setted.
     */
    public void setMsgCount(int msgCount) {
        msgCount = msgCount;
    }

    public void resetMsgCount(){
        msgCount = 0;
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
        // Measure arrival of mqtt message
        msgCount++;
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        MeasurementTimestamp.measureMqttAndSaveToCSV(csvFilePath,ts,msgCount);

        String payload = new String (mqttMessage.getPayload(), StandardCharsets.UTF_8);
        log.info("THE TOPIC:  " + topic
                + "\n\t"
                + "MESSAGE:  " + payload
                + "\n\t"
                + "TIMESTAMP:  " + new Timestamp(System.currentTimeMillis()));

        sendToKafka(topic, KEY,payload,true, new Timestamp(System.currentTimeMillis()).getTime());

        
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
