package MQTT_connector;

import KafkaPLCProducer.StringFormat.StringProducer;
import KafkaConsumer.StringFormat.StringConsumer;
import org.eclipse.paho.mqttv5.client.*;

import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;

/**
 * Bridge between an MQTT broker and a Kafka broker.
 * Forwards incoming MQTT messages to Kafka and can publish Kafka messages back to the PLC.
 * @author Herberto Werner
 */
public class MQTT_Kafka_Bridge implements MqttCallback {

    private static final Logger log = LoggerFactory.getLogger(MQTT_Kafka_Bridge.class);

    private static final String CLIENT_ID = "mqtt_pipeline";
    private static final int QOS_LEVEL_0_FIRE_AND_FORGET = 0;
    private static final int QOS_LEVEL_1_AT_LEAST_ONCE = 1;
    private static final int QOS_LEVEL_2_EXACTLY_ONCE = 2;
    private static final String KEY = "plc";

    private final StringProducer prod;
    private final StringConsumer cons;
    private MqttClient mqttClient;
    private MqttConnectionOptions connOpts;
    private int msgCount;

    /**
     * Creates a new MQTT-Kafka Bridge.
     * @param host MQTT broker host/IP
     * @param port MQTT broker port
     */
    public MQTT_Kafka_Bridge(String host, int port) {
        if (host == null) throw new IllegalArgumentException("Parameter 'host' can't be null");
        msgCount = 0;
        prod = new StringProducer();
        cons = new StringConsumer();
        try {
            this.mqttClient = new MqttClient("tcp://" + host + ":" + port, CLIENT_ID);
        } catch (MqttException e) {
            log.error("Failed to create MQTT client", e);
        }
    }

    /**
     * Connects to the MQTT broker.
     */
    public void connect() {
        try {
            connOpts = new MqttConnectionOptions();
            connOpts.setAutomaticReconnect(true);
            connOpts.setCleanStart(false);
            this.mqttClient.setCallback(this);
            this.mqttClient.connect(connOpts);
            log.info("Connected to MQTT broker");
        } catch (MqttException e) {
            log.error("Failed to connect to MQTT broker", e);
        }
    }

    /**
     * Publishes a message to the MQTT broker.
     * @param topic the topic to publish to
     * @param content the message payload
     */
    public void publishMsg(String topic, String content) {
        try {
            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(QOS_LEVEL_1_AT_LEAST_ONCE);
            this.mqttClient.publish(topic, message);
        } catch (MqttException e) {
            log.error("Failed to publish message to topic '{}'", topic, e);
            disconnect();
        }
    }

    /**
     * Subscribes to an MQTT topic.
     * @param topic the topic to subscribe to
     * @param qos QoS level (0, 1, or 2)
     */
    public void subscribeMsg(String topic, int qos) {
        if (topic == null) throw new IllegalArgumentException("Parameter 'topic' can't be null");
        if (qos < 0 || qos > 2) throw new IllegalArgumentException("QoS must be 0, 1, or 2");

        try {
            switch (qos) {
                case 0: this.mqttClient.subscribe(topic, QOS_LEVEL_0_FIRE_AND_FORGET); break;
                case 1: this.mqttClient.subscribe(topic, QOS_LEVEL_1_AT_LEAST_ONCE); break;
                case 2: this.mqttClient.subscribe(topic, QOS_LEVEL_2_EXACTLY_ONCE); break;
            }
            log.info("Subscribed to topic '{}' with QoS {}", topic, qos);
        } catch (MqttException e) {
            log.error("Failed to subscribe to topic '{}'", topic, e);
            disconnect();
        }
    }

    /**
     * Disconnects from the MQTT broker.
     */
    public void disconnect() {
        try {
            this.mqttClient.disconnect();
            this.mqttClient.close();
            log.info("Disconnected from MQTT broker");
        } catch (MqttException e) {
            log.error("Failed to disconnect", e);
        }
    }

    /**
     * Forwards a message to the Kafka broker.
     * @param topic Kafka topic
     * @param kafkaKey record key
     * @param payload record value
     * @param withTS whether to include a timestamp
     * @param timestamp the timestamp in milliseconds
     */
    public void sendToKafka(String topic, String kafkaKey, String payload, boolean withTS, long timestamp) {
        if (topic == null) throw new IllegalArgumentException("Parameter 'topic' can't be null");
        if (kafkaKey == null) throw new IllegalArgumentException("Parameter 'kafkaKey' can't be null");
        if (payload == null) throw new IllegalArgumentException("Parameter 'payload' can't be null");

        if (withTS) {
            prod.runProducerStringWithTS(topic, null, timestamp, kafkaKey, payload);
        } else {
            prod.runProducerString(topic, kafkaKey, payload);
        }
    }

    /**
     * Continuously reads from Kafka and forwards new values back to the PLC via MQTT.
     */
    public void sendToPLC() {
        String val = "";
        String oldVal = "";
        while (true) {
            cons.runConsumer();
            val = cons.getRecordValue();
            if (val != null && !val.equals(oldVal)) {
                publishMsg(System.getenv().getOrDefault("MQTT_TOPIC", "plc/data"), val);
                oldVal = val;
            }
        }
    }

    public int getMsgCount() { return msgCount; }
    public void resetMsgCount() { msgCount = 0; }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        msgCount++;
        String payload = new String(mqttMessage.getPayload(), StandardCharsets.UTF_8);
        log.info("Message #{} arrived on topic '{}', size: {} bytes", msgCount, topic, mqttMessage.getPayload().length);
        sendToKafka(topic, KEY, payload, true, new Timestamp(System.currentTimeMillis()).getTime());
    }

    @Override public void disconnected(MqttDisconnectResponse response) {}
    @Override public void mqttErrorOccurred(MqttException e) { log.error("MQTT error", e); }
    @Override public void deliveryComplete(IMqttToken token) {}
    @Override public void connectComplete(boolean reconnect, String serverURI) { log.info("Connection complete — reconnect: {}", reconnect); }
    @Override public void authPacketArrived(int reasonCode, MqttProperties properties) {}

}
