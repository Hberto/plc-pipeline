
//ToDo: comments
//ToDo: start the application here
//ToDo: connector to kafka connect

import KafkaPLCProducer.AvroProducer;
import KafkaPLCProducer.producer.PLCData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import testPipeline.Simple_Pipeline;

/**
 * This class is the main entry of the plc4x connector application
 * with the Programmable Logic Controller (PLC).
 * ToDo: Update description
 * @author Herberto Werner
 */

//ToDo: Optional: Über Konsole: Einstellbar welche Connection zu PLC usw.

public class ApplicationStarter {

    private static final Logger log = LoggerFactory.getLogger(Simple_Pipeline.class);
    private static final int TRYOUTS = 10;
    //ToDo: Integrate PLC4X


    public static void main(String[] args) {

        PLCData plcdata = PLCData.newBuilder()
                .setData(345345)
                .setSenderName("teste")
                .setDataType("Long")
                .setOrderNr(12001)
                .setOperation("Read")
                .build();

        log.info("Starting Application Starter.......");
        AvroProducer avroProducer = new AvroProducer();
        log.info("Sending Message.......");
        for(int i = 0; i < TRYOUTS; i++) {
            avroProducer.runProducer(plcdata);
            log.info("Message SENT.......");
        }

    }


}
