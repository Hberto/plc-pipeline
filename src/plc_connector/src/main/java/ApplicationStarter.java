
import PLC4X_Connector.ReadOperations.ReadOperation;
import PLC4X_Connector.connection.ConnectionHandler;
import org.apache.plc4x.java.PlcDriverManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Timestamp;
import java.util.Date; 

import KafkaPLCProducer.StringFormat.StringProducer;


/**
 * This class is the main entry of the plc4x connector application
 * with the Programmable Logic Controller (PLC).
 * @author Herberto Werner
 */


public class ApplicationStarter {

    private static final Logger log = LoggerFactory.getLogger(ApplicationStarter.class);
    private static final String CON_ADDR = "s7://192.168.0.2";

    private static PlcDriverManager driverManager = null;
    private static ConnectionHandler conHandler = null;
    private static ReadOperation reader = null;


    public static void main(String[] args) {

        //driverManager = new PlcDriverManager();
        //conHandler = new ConnectionHandler(CON_ADDR, driverManager);
//
        ////Returns a PLCConnection with a driverManager or driverManagerPool
        //reader = new ReadOperation(conHandler.connect(true));
        //log.info("Test: Connection success");
        //reader.readSingleField("test2","%I0.1:BOOL");
        //log.info("Read success");
        StringProducer prod = new StringProducer();
        for(int i=0; i < 1; i++){
            prod.runProducerString("test", "test", String.valueOf(i));
        }
    }


}
