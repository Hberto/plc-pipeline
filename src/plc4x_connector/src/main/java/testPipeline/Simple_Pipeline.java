package testPipeline;

import org.apache.plc4x.java.PlcDriverManager;
import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.messages.PlcReadResponse;

import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class shows a simple pipeline with plc4x and kafka connect.
 * It's a simple setup to send some data into the pipeline.
 * This Class is going to be removed after deployment.
 *
 * @author Herberto Werner
 */

public class Simple_Pipeline {
    private static final Logger log = LoggerFactory.getLogger(Simple_Pipeline.class);

    /**
     * Main entrypoint of the simulation subsystem application.
     *
     * @param args command line parameters.
     */
    public static void main(String[] args) {

        PlcDriverManager driverManager = new PlcDriverManager();

        /*
         *   Testing the first version and connection to plc (S7-1200)
         */

        try (PlcConnection con = driverManager.getConnection("s7://192.168.0.1")){
            log.info("Test: Connection success");
            CompletableFuture<? extends PlcReadResponse> reqFuture = con.readRequestBuilder()
                    .addItem("test","%I0.0:BOOL")
                    .build()
                    .execute();
            log.info("Request sent......");

            PlcReadResponse response = reqFuture.get();
            log.info("Response arrived......");

            log.info("Status:::: " + response.getResponseCode("test"));
            log.info("Value:::: " +  response.getBoolean("test"));
        }
        catch (Exception e) {
            e.printStackTrace();
            log.error("Test: No Connection");
        }

        //ToDo: Integrate kafka producer and connect


    }

}
