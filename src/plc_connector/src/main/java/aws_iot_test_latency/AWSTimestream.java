package aws_iot_test_latency;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.timestreamquery.TimestreamQueryClient;
import software.amazon.awssdk.services.timestreamquery.model.QueryRequest;
import software.amazon.awssdk.services.timestreamquery.model.QueryResponse;
import software.amazon.awssdk.services.timestreamquery.paginators.QueryIterable;

public class AWSTimestream {


    static AWSCredentials credentials = new BasicAWSCredentials("AKIATDVFRUAHVANYKBTQ","TNl9R44tMq+Ky+kPybdttb0R1LhB1dGZ35aAXhMR");

    public static void main(String[] args) {
        TimestreamQueryClient client;



    }

    private static TimestreamQueryClient buildQueryClient() {
        return TimestreamQueryClient.builder().region(Region.EU_CENTRAL_1).build();
    }

    private static void runQuery(String query) {
        try {
            QueryRequest queryRequest = QueryRequest.builder().queryString(query).build();
            //final QueryIterable queryResponseIterator = timestreamQueryClient.queryPaginator(queryRequest);
            //for (QueryResponse queryResponse : queryResponseIterator) {
            //        parseQueryResult(queryResponse);
            //}
        } catch (Exception e) {
            // Some queries might fail with 500 if the result of a sequence function has more than 10000 entries
            e.printStackTrace();
        }
    }

}
