
//ToDo: comments
//ToDo: start the application here
//ToDo: connector to kafka connect

import KafkaPLCProducer.producer.PLCData;

/**
 * This class is the main entry of the plc4x connector application
 * with the Programmable Logic Controller (PLC).
 * ToDo: Update description
 * @author Herberto Werner
 */

public class ApplicationStarter {
    //ToDo: final pipeline impl starter
    PLCData plcdata = PLCData.newBuilder()
            .setData(345345)
            .setSenderName("teste")
            .setDataType("Long")
            .setOrderNr(12001)
            .setOperation("Read")
            .build();


    //ToDo: Optional: Über Konsole: Einstellbar welche Connection zu PLC usw.

}
