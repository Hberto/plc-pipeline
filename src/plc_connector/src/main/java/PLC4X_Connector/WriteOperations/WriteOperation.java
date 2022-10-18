package PLC4X_Connector.WriteOperations;

import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.messages.PlcWriteRequest;
import org.apache.plc4x.java.api.messages.PlcWriteResponse;
import org.apache.plc4x.java.api.types.PlcResponseCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;


/**
 * This class represents a wrapper for writing data into fields of a device or protocol using
 * the plc4x api data wrapper. It's possible to write data into a single field or multiple values of the
 * same datatype.
 *
 *  Attention: First Version of Write Operations
 *
 * @author Herberto Werner
 */
public class WriteOperation implements IWritePLC {

    //Constants
    private static final Logger log = LoggerFactory.getLogger(WriteOperation.class);
    // Private Fields
    private PlcConnection con;

    /**
     * Constructor of the class.
     * @param con the PlcConnection which will be passed
     */
    public WriteOperation(PlcConnection con){
        this.con = con;
    }


    @Override
    public void writeSingleField(String itemName, String fieldAdr, String dataType, String value) {

        if(itemName.length() < 0 || itemName.isEmpty()) {
            log.error("Parameter itemName null");
            throw new IllegalArgumentException("Parameter itemName null or empty");
        }

        if(fieldAdr.length() < 0 || fieldAdr.isEmpty()) {
            log.error("Parameter field address null");
            throw new IllegalArgumentException("Parameter field address null or empty");
        }

        if(dataType.length() < 0 || dataType.isEmpty()) {
            log.error("Parameter datatype null");
            throw new IllegalArgumentException("Parameter datatype null or empty");
        }

        if(value.length() < 0 || value.isEmpty()) {
            log.error("Parameter value null");
            throw new IllegalArgumentException("Parameter value null or empty");
        }
        checkMetaData();
        try {
            if(con.isConnected()){
                PlcWriteRequest.Builder builder = con.writeRequestBuilder();
                String dataTypeLower = dataType.toLowerCase();
                //Parsing the data types of the s7-protocol
                switch (dataTypeLower) {
                    case "bool":
                        builder.addItem(itemName,fieldAdr,Boolean.parseBoolean(value));
                        break;
                    case "byte":
                        builder.addItem(itemName,fieldAdr,Byte.parseByte(value));
                        break;
                    case "int":
                    case "word":
                        builder.addItem(itemName,fieldAdr,Short.parseShort(value));
                        break;
                    case "dword":
                    case "dint":
                        builder.addItem(itemName,fieldAdr,Integer.parseInt(value));
                        break;
                    case "real":
                        builder.addItem(itemName,fieldAdr,Float.parseFloat(value));
                        break;
                    case "String":
                        builder.addItem(itemName,fieldAdr,value);
                        break;

                    default:
                        log.error("DataType unknown!");
                        throw new Exception("Unknown Datatype in writing Data Operation");
                }
                PlcWriteRequest writeRequest = builder.build();
                processResponse(writeRequest);
            }

        }
        catch (Exception e) {
            log.error("Writing in single Data Operation failed");
            e.printStackTrace();
        }
    }


    @Override
    public void writeMultipleFields(List<String> itemNames, List<String> fieldAddresses, Boolean[] valArr) {
        writeMultipleFieldsGeneric(itemNames,fieldAddresses,valArr);
    }

    @Override
    public void writeMultipleFields(List<String> itemNames, List<String> fieldAddresses, Byte[] valArr) {
        writeMultipleFieldsGeneric(itemNames,fieldAddresses,valArr);
    }

    @Override
    public void writeMultipleFields(List<String> itemNames, List<String> fieldAddresses, Short[] valArr) {
        writeMultipleFieldsGeneric(itemNames,fieldAddresses,valArr);
    }

    @Override
    public void writeMultipleFields(List<String> itemNames, List<String> fieldAddresses, Integer[] valArr) {
        writeMultipleFieldsGeneric(itemNames,fieldAddresses,valArr);
    }

    @Override
    public void writeMultipleFields(List<String> itemNames, List<String> fieldAddresses, Float[] valArr) {
        writeMultipleFieldsGeneric(itemNames,fieldAddresses,valArr);
    }

    @Override
    public void writeMultipleFields(List<String> itemNames, List<String> fieldAddresses, Duration[] valArr) {
        writeMultipleFieldsGeneric(itemNames,fieldAddresses,valArr);
    }

    @Override
    public void writeMultipleFields(List<String> itemNames, List<String> fieldAddresses, LocalDate[] valArr) {
        writeMultipleFieldsGeneric(itemNames,fieldAddresses,valArr);
    }

    @Override
    public void writeMultipleFields(List<String> itemNames, List<String> fieldAddresses, Character[] valArr) {
        writeMultipleFieldsGeneric(itemNames,fieldAddresses,valArr);
    }

    @Override
    public void writeMultipleFields(List<String> itemNames, List<String> fieldAddresses, String[] valArr) {
        writeMultipleFieldsGeneric(itemNames,fieldAddresses,valArr);
    }

    /*******************
     * Private Methods
     ******************/

    /**
     * Generic call of writing data to fields.
     * @param itemNames names of the field addresses
     * @param fieldAddresses field addresses
     * @param valArr values of specific type of data
     * @param <T> generic type
     */
    private <T> void writeMultipleFieldsGeneric(List<String> itemNames, List<String> fieldAddresses, T[] valArr) {

        if(itemNames.isEmpty()) {
            log.error("Parameter: List of Items is empty");
            throw new IllegalArgumentException("itemNames List can't be empty");
        }

        if(fieldAddresses.isEmpty()) {
            log.error("Parameter: List of FieldAddresses is empty");
            throw new IllegalArgumentException("FieldAddresses List can't be empty");
        }

        if(valArr.length == 0) {
            log.error("Parameter: Value Array is empty");
            throw new IllegalArgumentException("Value Array can't be empty");
        }
        checkMetaData();
        PlcWriteRequest multipleWriteReq = getMultipleRequestList(itemNames, fieldAddresses, valArr);
        processResponse(multipleWriteReq);

    }

    /**
     * Processes the write request.
     * @param writeRequest the request to be sent
     */
    private void processResponse(PlcWriteRequest writeRequest) {

        //First Version - write to console that field was updated
        CompletableFuture<? extends PlcWriteResponse> asyncResponse = writeRequest.execute();
        asyncResponse.whenComplete((response, throwable) -> {

            for (String fieldName : response.getFieldNames()) {
                if(response.getResponseCode(fieldName) == PlcResponseCode.OK) {
                    //Write to console - this could be passed as callback
                    log.info("Field: " + fieldName + " was updated!");
                }
                else {
                    log.error("Error with field: "+ fieldName + " with responseCode Error: "+
                            response.getResponseCode(fieldName).name());
                }
            }
        });
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
     * Iterates through Fields with their addresses and values to add them to a readRequest
     * with multiple fields and field addresses and values.
     * @param itemNames list of fields
     * @param fieldAddresses list of field addresses
     * @param valArr Array of values: possible values are from the class number, string and character and local time!
     * @return PlcWriteRequest with multiple fields.
     */
    private <T> PlcWriteRequest getMultipleRequestList(List<String> itemNames, List<String> fieldAddresses, T[] valArr) {

        PlcWriteRequest.Builder builder = con.writeRequestBuilder();
        Iterator<String> itemsIter = itemNames.iterator();
        Iterator<String> fieldAdrIter = fieldAddresses.iterator();


        while(itemsIter.hasNext() && fieldAdrIter.hasNext()) {
            builder.addItem(itemsIter.next(),fieldAdrIter.next(), valArr);
        }

        PlcWriteRequest writeRequest = builder.build();

        return writeRequest;
    }
}
