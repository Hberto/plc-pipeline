package aws_iot_test_latency;

import MQTT_connector.MeasurementTimestamp;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.amazonaws.services.iot.client.sample.sampleUtil.SampleUtil;

import java.sql.Timestamp;

public class AwsIoTClient {
    private String clientEndpoint = "a2080z026ado1p-ats.iot.eu-central-1.amazonaws.com";
    private String clientId = "plc1";

    private String certificateFile = "C:\\Users\\Herberto\\Desktop\\UNI\\Bachelor-Arbeit\\AWS_Dienste\\zert\\gerät\\c0ac34c52d3c7e2a8c65fe00cba31af40a01b207b1a7ed3258b43d10468af1e1-certificate.pem.crt";
    private String privateKeyFile = "C:\\Users\\Herberto\\Desktop\\UNI\\Bachelor-Arbeit\\AWS_Dienste\\zert\\privat\\c0ac34c52d3c7e2a8c65fe00cba31af40a01b207b1a7ed3258b43d10468af1e1-private.pem.key";

    private String csvFilePath = "src/main/java/aws_iot_test_latency/timestamp_measurement/sync_test2_10000/start.csv";
    private AWSIotMqttClient client;

    public AwsIoTClient(){
        SampleUtil.KeyStorePasswordPair pair = SampleUtil.getKeyStorePasswordPair(certificateFile,privateKeyFile);
        client = new AWSIotMqttClient(clientEndpoint, clientId, pair.keyStore, pair.keyPassword);
    }

    public void connectToAWS() {
        try {
            client.setCleanSession(false);
            client.connect();
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }

    public void subscribeToAWSTopic(String subTopic, int qos) {
        MyTopic topic;
        if (subTopic == null) {
            throw new IllegalArgumentException(" Parameter 'subTopic' can't be null");
        }

        if (qos < 0 || qos > 1 ) {
            throw new IllegalArgumentException(" Parameter 'qos' can't be below 0 or above 1. QOS 2 not supported");
        }
        try {
            if(qos == 0) {
                topic = new MyTopic(subTopic, AWSIotQos.QOS0);
                this.client.subscribe(topic);
            }
            else {
                topic = new MyTopic(subTopic, AWSIotQos.QOS1);
                this.client.subscribe(topic);
            }
        }
        catch (Exception e){
            e.printStackTrace();
            disconnectFromAWS();
        }
    }

    public void publishToAWS(String topic, int qos, String payload) {

        if (topic == null) {
            throw new IllegalArgumentException(" Parameter 'topic' can't be null");
        }

        if (qos < 0 || qos > 1 ) {
            throw new IllegalArgumentException(" Parameter 'qos' can't be below 0 or above 1. QOS 2 not supported");
        }

        if (payload == null) {
            throw new IllegalArgumentException(" Parameter 'payload' can't be null");
        }

        try {
            Timestamp ts = new Timestamp(System.currentTimeMillis());
            if(qos == 0) {
                MeasurementTimestamp.measureMqttAndSaveToCSVSimple(csvFilePath,ts);
                this.client.publish(topic, AWSIotQos.QOS0, payload);
            }
            else {
                MeasurementTimestamp.measureMqttAndSaveToCSVSimple(csvFilePath,ts);
                this.client.publish(topic, AWSIotQos.QOS1, payload);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            disconnectFromAWS();
        }
    }

    public void disconnectFromAWS() {
        try {
            this.client.disconnect();
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }




}
