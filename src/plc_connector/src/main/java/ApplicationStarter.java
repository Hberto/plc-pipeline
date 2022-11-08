

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Timestamp;
import java.util.Date; 

import KafkaPLCProducer.StringFormat.StringProducer;
import KafkaConsumer.StringFormat.StringConsumer;


/**
 * This class is the main entry of the plc4x connector application
 * with the Programmable Logic Controller (PLC).
 * @author Herberto Werner
 */


public class ApplicationStarter {

    private static final Logger log = LoggerFactory.getLogger(ApplicationStarter.class);
    public static void main(String[] args) {
        StringProducer prod = new StringProducer();
        try {
            ApplicationStarter.fire();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    synchronized public static void fire() throws InterruptedException {
        StringProducer prod = new StringProducer();
        StringConsumer cons = new StringConsumer();
        int min = 0;
        int max = 1000;
        while(true) {
            int randomNu = (int)(Math.random()*(max-min+1)+min);  
            prod.runProducerString("12003800_test", "test", String.valueOf(randomNu));
            //cons.runConsumer();
            Thread.sleep(10000);
            //prod.getMetrics();
        }
    }


}
