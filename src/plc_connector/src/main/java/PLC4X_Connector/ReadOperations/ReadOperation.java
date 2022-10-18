package PLC4X_Connector.ReadOperations;

import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.messages.PlcReadRequest;
import org.apache.plc4x.java.api.messages.PlcReadResponse;
import org.apache.plc4x.java.api.types.PlcResponseCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * This class represents a wrapper for read operations of a device or protocol using the plc4x api data wrapper.
 * It's possible to read data from a single field or multiple fields of the device or protocol.
 * Attention: First Version of Read Operations
 *
 * @author Herberto Werner
 */
public class ReadOperation implements IReadPLC {

    //Constants
    private static final Logger log = LoggerFactory.getLogger(ReadOperation.class);
    // Private Fields
    private PlcConnection con;

    /**
     * Constructor of the class.
     * @param con the PlcConnection which will be passed
     */
    public ReadOperation(PlcConnection con){
        this.con = con;
    }


    @Override
    public void readSingleField(String itemName, String fieldAdr) {

        if(itemName.length() < 0 || itemName.isEmpty()) {
            log.error("Parameter itemName null");
            throw new IllegalArgumentException("Parameter itemName null or empty");
        }

        if(fieldAdr.length() < 0 || fieldAdr.isEmpty()) {
            log.error("Parameter field address null");
            throw new IllegalArgumentException("Parameter field address null or empty");
        }
        checkMetaData();

        if(con.isConnected()) {
            PlcReadRequest.Builder builder = con.readRequestBuilder();
            builder.addItem(itemName,fieldAdr);

            // Build and Process the Response
            PlcReadRequest readRequest =  builder.build();
            getValue(readRequest);
        }

    }

    @Override
    public void readMultipleFields(List<String> itemNames, List<String> fieldAddresses) {

        if(itemNames.isEmpty()) {
            log.error("Parameter: List of Items is empty");
            throw new IllegalArgumentException("itemNames List can't be empty");
        }

        if(fieldAddresses.isEmpty()) {
            log.error("Parameter: List of FieldAddresses is empty");
            throw new IllegalArgumentException("FieldAddresses List can't be empty");
        }
        checkMetaData();

        if(con.isConnected()) {
            PlcReadRequest multipleReq = getMultipleRequestList(itemNames,fieldAddresses);
            getValues(multipleReq);
        }
    }

    /*******************
     * Private Methods
     ******************/

    /**
     * Iterates through Fields with their addresses to add them to a readRequest
     * with multiple fields and field addresses.
     * @param itemNames list of fields
     * @param fieldAddresses list of field addresses
     * @return PlcReadRequest with multiple fields.
     */
    private PlcReadRequest getMultipleRequestList(List<String> itemNames, List<String> fieldAddresses) {

        PlcReadRequest.Builder builder = con.readRequestBuilder();

        Iterator<String> itemsIter = itemNames.iterator();
        Iterator<String> fieldAdrIter = fieldAddresses.iterator();

        while(itemsIter.hasNext() && fieldAdrIter.hasNext()) {
            builder.addItem(itemsIter.next(),fieldAdrIter.next());
        }

        PlcReadRequest readRequest = builder.build();

        return readRequest;
    }

    /**
     * Checks if the MetaData of the PLC Connection is okay.
     */
    private void checkMetaData() {
        if(!con.getMetadata().canRead()) {
            log.error("This PLC4X_Connector.PLC4X_Connector.connection doesn't support reading.");
        }
    }

    /**
     * Reads the Response of a single request.
     *
     * Attention: First Version: it ll be printed or would be integrated into logger
     * It should be passed on to kafka connect. It could be a return value.
     * @param readRequest the request to be read
     */
    private void getValue(PlcReadRequest readRequest) {

        CompletableFuture<? extends PlcReadResponse> asyncResponse = readRequest.execute();
        asyncResponse.whenComplete((response, throwable) -> {
            try {
                //ToDo: switch case for parsing between the types of values
                //ToDo: Save fieldnames into a list or set - value of the field must be known if boolean,word etc.
                //ToDo: Duplicated Code with for-loop
                //ToDo: send to kafka connect - field + value

                //First Version - logger or print out
                for(String fieldName : response.getFieldNames()) {
                    if(response.getResponseCode(fieldName) == PlcResponseCode.OK) {
                        int numValueOfFieldNames = response.getNumberOfValues(fieldName);
                        if(numValueOfFieldNames == 1) {
                            //Print out
                            log.info("FieldName: "+ fieldName + " with the value of: " +
                                    response.getObject(fieldName));
                        }

                    }
                    else {
                        log.error("ReadRequestFailed at " + fieldName + " with the ResponseCode: "+
                                response.getResponseCode(fieldName).name());
                    }

                }
            } catch (Exception e) {
                log.error("Response for a single field failed");
                e.printStackTrace();
            }
        });
    }

    /**
     *  Reads the Response of a request with multiple Fields.
     *
     *  Attention: First Version: it ll be printed or would be integrated into logger
     *  It should be passed on to kafka connect. It could be a return value.
     * @param readRequest the request to be read
     */
    private void getValues(PlcReadRequest readRequest) {

        CompletableFuture<? extends PlcReadResponse> asyncResponse = readRequest.execute();
        asyncResponse.whenComplete((response, throwable) -> {
            try {
                //ToDo: switch case for parsing between the types of values
                //ToDo: Save fieldnames into a list or set - value of the field must be known if boolean,word etc.
                //ToDo: Duplicated Code with for-loop
                //ToDo: send to kafka connect - field + value

                //First Version - logger or print out
                for(String fieldName : response.getFieldNames()) {
                    if(response.getResponseCode(fieldName) == PlcResponseCode.OK) {
                        int numValueOfFieldNames = response.getNumberOfValues(fieldName);
                        log.info("FieldName: ");
                        for(int i = 0; i < numValueOfFieldNames;i++) {
                            log.info("  " + response.getObject(fieldName,i));
                        }
                    }
                    else {
                        log.error("ReadRequestFailed at " + fieldName + " with the ResponseCode: "+
                                response.getResponseCode(fieldName).name());
                    }
                }
            }
            catch (Exception e) {
                log.error("Response for multiple fields failed");
                e.printStackTrace();
            }
        });

    }

}
