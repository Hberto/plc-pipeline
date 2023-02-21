package aws_iot_test_latency;


public class AppLatencyMeasure {


    public static void main(String[] args) {

        final int EXPERIMENT_10 = 10;
        final int EXPERIMENT_100 = 100;
        final int EXPERIMENT_1000 = 1000;
        final int EXPERIMENT_10000 = 10000;

        String strom = "{\"strom\": ";
        String stromAuftrag = "{\"auftrag\": 12003800,\"strom\": ";
        String sendingData = "";

        String topic = "firstTopic";
        int qos = 1;
        String payload = "43";

        AwsIoTClient client = new AwsIoTClient();
        client.connectToAWS();
        System.out.println("+++STARTING TEST+++");
        //client.publishToAWS(topic,qos,payload);
        client.subscribeToAWSTopic(topic,qos);
        //client.disconnectFromAWS();

        /**
         * For Tests purposes only
         */
        for(int i = 0; i < EXPERIMENT_10000; i++ ){
            sendingData = strom + i + "}";
           client.publishToAWS(topic,qos,sendingData);
        }
    }


}
