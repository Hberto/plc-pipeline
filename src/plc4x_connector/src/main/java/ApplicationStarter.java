
//ToDo: comments
//ToDo: start the application here
//ToDo: connector to kafka connect

import KafkaPLCProducer.Avro.AvroProducer;
import KafkaPLCProducer.JSON.JSONProducer;
import KafkaPLCProducer.StringFormat.StringProducer;
import KafkaPLCProducer.producerData.PLCData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class is the main entry of the plc4x connector application
 * with the Programmable Logic Controller (PLC).
 * ToDo: Update description
 * @author Herberto Werner
 */

//ToDo: Optional: Über Konsole: Einstellbar welche Connection zu PLC usw.

public class ApplicationStarter {

    private static final Logger log = LoggerFactory.getLogger(ApplicationStarter.class);
    private static final int TRYOUTS = 10;
    //ToDo: Integrate PLC4X


    public static void main(String[] args) {

        PLCData plcdata = PLCData.newBuilder()
                .setData(345345L)
                .setSenderName("teste")
                .setDataType("Long")
                .setOrderNr(12001L)
                .setOperation("Read")
                .build();


        log.info("Starting Application Starter.......");
        AvroProducer avroProducer = new AvroProducer();
        JSONProducer jsonProducer = new JSONProducer();
        StringProducer stringProducer = new StringProducer();
        log.info("Sending Message.......");
        //avroProducer.runProducer(plcdata);
        //avroProducer.runProducerSimple("Hey it worked");
        jsonProducer.runProducer();
        //stringProducer.runProducerString();
        log.info("+++++++++++++++++DONE++++++++++++++++");

    }


}
