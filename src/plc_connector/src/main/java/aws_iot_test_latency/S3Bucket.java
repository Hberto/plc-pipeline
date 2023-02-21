package aws_iot_test_latency;

import MQTT_connector.MeasurementTimestamp;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import java.util.List;

public class S3Bucket {

    public static void main(String[] args) {

        String bucketName = "raw-sps";
        String csvFilePath = "src/main/java/aws_iot_test_latency/timestamp_measurement/sync_test2_10000/arrivalS3.csv";

        AWSCredentials credentials = new BasicAWSCredentials("AKIATDVFRUAHVANYKBTQ", "TNl9R44tMq+Ky+kPybdttb0R1LhB1dGZ35aAXhMR");

        AmazonS3 s3client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.EU_CENTRAL_1)
                .build();

        ListObjectsV2Result result = s3client.listObjectsV2(bucketName);
        List<S3ObjectSummary> objects = result.getObjectSummaries();
        for (S3ObjectSummary os : objects) {
            System.out.println("* " + "Key: " + os.getKey()
                    + "\n\t"
                    + "* " + "Last modified: " + os.getLastModified().getTime()
            );
            if (os.getKey().equals("2022/12/03/19/PUT-S3-SWyEj-4-2022-12-03-19-47-23-de0ab1ca-4eed-4d7e-9ea5-618335a62d7e")) {
                System.out.println("HELLO");
                System.out.println("* " + "Key: " + os.getKey());
                for (int i = 7564; i < 10000; i++) {
                    MeasurementTimestamp.measureMqttAndSaveToCSVSimpleLong(csvFilePath, os.getLastModified().getTime());
                }
                //for(int i = 0; i < 1000; i++) {
                //    MeasurementTimestamp.measureMqttAndSaveToCSVSimpleLong(csvFilePath,os.getLastModified().getTime());
                //}
            }

        }

    }
}
