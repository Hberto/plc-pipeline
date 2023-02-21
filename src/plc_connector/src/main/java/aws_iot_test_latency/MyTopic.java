package aws_iot_test_latency;

import MQTT_connector.MeasurementTimestamp;
import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.amazonaws.services.iot.client.AWSIotTopic;

import java.sql.Timestamp;

public class MyTopic extends AWSIotTopic {

    private String csvFilePath = "src/main/java/aws_iot_test_latency/timestamp_measurement/sync_test2_10000/arrival.csv";

    public MyTopic(String topic, AWSIotQos qos) {
        super(topic, qos);
    }
    @Override
    public void onMessage(AWSIotMessage message) {
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        MeasurementTimestamp.measureMqttAndSaveToCSVSimple(csvFilePath,ts);
        System.out.println("Message arrived: " +
                 "\n\t"
                + "MESSAGE:  " + message.getStringPayload());
    }
}
