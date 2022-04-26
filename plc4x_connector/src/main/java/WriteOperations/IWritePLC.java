package WriteOperations;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

/**
 *  Interface defines for writing data to the fields of
 *  a device with a specific protocol.
 *
 * @author  Herberto Werner
 */
public interface IWritePLC {

    /**
     * Writes a single data field to the plc. Specific Name of the item, datatype
     * and field should be spelled correctly.
     * The item is the name of the variable in the table. A variable could be
     * in the standard variable tabular, data block with as specific datatype etc.
     * The field is the address of the variable.
     * Example: itemName: "test" and fieldAdr: "%I0.0" and dataType Bool, BYTE, WORD, DWORD etc.
     * with the value of (byte) 0x12, (short) 0x434, (int) 3003
     *
     * @param itemName name of the item
     * @param fieldAdr address of the field
     * @param dataType datatype of the field
     * @param value value for the write operation
     */
    void writeSingleField(String itemName, String fieldAdr, String dataType, String value);

    /**
     * Writes iteratively data fields to the plc. Specific Name of the item
     * and field should be spelled correctly.
     * The item is the name of the variable in the table. A variable could be
     * in the standard variable tabular, data block etc.
     * The field is the address of the variable. The Value Array is filled with values with the same datatype
     * Example: itemName: "test" , fieldAdr: "%DB100.DBX0.0:BOOL[12]",
     * valArr: {true,false,true,true,false,true,true,false,true,true,true,false}
     * More information about the formats (S7): https://plc4x.apache.org/users/protocols/s7.html
     * @param itemNames List of Items
     * @param fieldAddresses List of Field Addresses
     * @param valArr An array of boolean values.
     */
    void writeMultipleFields(List<String> itemNames, List<String> fieldAddresses, Boolean[] valArr);

    /**
     * Writes iteratively data fields to the plc. Specific Name of the item
     * and field should be spelled correctly.
     * The item is the name of the variable in the table. A variable could be
     * in the standard variable tabular, data block etc.
     * The field is the address of the variable. The Value Array is filled with values with the same datatype
     * Example: itemName: "test" , fieldAdr: "%DB101.DB0:BYTE[6]",
     * valArr: {0x01, 0x02, 0x03, 0x04, 0x05, 0x06}
     * More information about the formats (S7): https://plc4x.apache.org/users/protocols/s7.html
     * @param itemNames List of Items
     * @param fieldAddresses List of Field Addresses
     * @param valArr An array of Byte values.
     */
    void writeMultipleFields(List<String> itemNames, List<String> fieldAddresses, Byte[] valArr);

    /**
     * Writes iteratively data fields to the plc. Specific Name of the item
     * and field should be spelled correctly.
     * The item is the name of the variable in the table. A variable could be
     * in the standard variable tabular, data block etc.
     * The field is the address of the variable. The Value Array is filled with values with the same datatype
     * Example: itemName: "test" , fieldAdr: "%DB102.DBW0:WORD[7]",
     * valArr: {0x1111, 0x2222, 0x3333, 0x4444, 0x5555, 0x6666, 0x7777}
     * More information about the formats (S7): https://plc4x.apache.org/users/protocols/s7.html
     * @param itemNames List of Items
     * @param fieldAddresses List of Field Addresses
     * @param valArr An array of Short values.
     */
    void writeMultipleFields(List<String> itemNames, List<String> fieldAddresses, Short[] valArr);

    /**
     * Writes iteratively data fields to the plc. Specific Name of the item
     * and field should be spelled correctly.
     * The item is the name of the variable in the table. A variable could be
     * in the standard variable tabular, data block etc.
     * The field is the address of the variable. The Value Array is filled with values with the same datatype
     * Example: itemName: "test" , fieldAdr: "%DB103.DBD12:DINT[7]",
     * valArr: {0x6666_7777, 0x2222_3333, 0x5555_6666, 0x1234_4444, 0x2222_5555, 0x1212_6666, 0xAAAA_7777}
     * More information about the formats (S7): https://plc4x.apache.org/users/protocols/s7.html
     * @param itemNames List of Items
     * @param fieldAddresses List of Field Addresses
     * @param valArr An array of Integer values.
     */
    void writeMultipleFields(List<String> itemNames, List<String> fieldAddresses, Integer[] valArr);

    /**
     * Writes iteratively data fields to the plc. Specific Name of the item
     * and field should be spelled correctly.
     * The item is the name of the variable in the table. A variable could be
     * in the standard variable tabular, data block etc.
     * The field is the address of the variable. The Value Array is filled with values with the same datatype
     * Example: itemName: "test" , fieldAdr: "%DB104:DBD4:REAL[7]",
     * valArr: {(float)3.1416, (float)1.4142, (float)2.7182, (float)0.5963, (float)2.5029, (float)0.3183, (float)1.2599}
     * More information about the formats (S7): https://plc4x.apache.org/users/protocols/s7.html
     * @param itemNames List of Items
     * @param fieldAddresses List of Field Addresses
     * @param valArr An array of Float values.
     */
    void writeMultipleFields(List<String> itemNames, List<String> fieldAddresses, Float[] valArr);

    /**
     * Writes iteratively data fields to the plc. Specific Name of the item
     * and field should be spelled correctly.
     * The item is the name of the variable in the table. A variable could be
     * in the standard variable tabular, data block etc.
     * The field is the address of the variable. The Value Array is filled with values with the same datatype
     * Example: itemName: "test" , fieldAdr: "%DB105.DBD20:TIME[3]",
     * valArr: {Duration.parse("P23DT19H30M"), Duration.parse("P22DT18H29M"),Duration.parse("P21DT17H28M")}
     * More information about the formats (S7): https://plc4x.apache.org/users/protocols/s7.html
     * @param itemNames List of Items
     * @param fieldAddresses List of Field Addresses
     * @param valArr An array of Duration values.
     */
    void writeMultipleFields(List<String> itemNames, List<String> fieldAddresses, Duration[] valArr);

    /**
     * Writes iteratively data fields to the plc. Specific Name of the item
     * and field should be spelled correctly.
     * The item is the name of the variable in the table. A variable could be
     * in the standard variable tabular, data block etc.
     * The field is the address of the variable. The Value Array is filled with values with the same datatype
     * Example: itemName: "test" , fieldAdr: "%DB107.DBD20:TIME_OF_DAY[3]",
     * valArr: LocalTime tod01 = LocalTime.of(12, 30);
     *         LocalTime tod02 = LocalTime.of(22,59,15);
     *         LocalTime[] todArray = new LocalTime[]{tod01, tod02, tod01};
     * More information about the formats (S7): https://plc4x.apache.org/users/protocols/s7.html
     * @param itemNames List of Items
     * @param fieldAddresses List of Field Addresses
     * @param valArr An array of LocalTime values.
     */
    void writeMultipleFields(List<String> itemNames, List<String> fieldAddresses, LocalDate[] valArr);

    /**
     * Writes iteratively data fields to the plc. Specific Name of the item
     * and field should be spelled correctly.
     * The item is the name of the variable in the table. A variable could be
     * in the standard variable tabular, data block etc.
     * The field is the address of the variable. The Value Array is filled with values with the same datatype
     * Example: itemName: "test" , fieldAdr: "%DB109.DBB0:CHAR[7]",
     * valArr: {'A','B','C','D','E','F','G'}
     * More information about the formats (S7): https://plc4x.apache.org/users/protocols/s7.html
     * @param itemNames List of Items
     * @param fieldAddresses List of Field Addresses
     * @param valArr An array of Character values.
     */
    void writeMultipleFields(List<String> itemNames, List<String> fieldAddresses, Character[] valArr);

    /**
     * Writes iteratively data fields to the plc. Specific Name of the item
     * and field should be spelled correctly.
     * The item is the name of the variable in the table. A variable could be
     * in the standard variable tabular, data block etc.
     * The field is the address of the variable. The Value Array is filled with values with the same datatype
     * Example: itemName: "test" , fieldAdr: "%DB110.DBB0:STRING[3]",
     * valArr: "BARCELONA","ANZOATEGUI","BARCELONA_ANZOATEGUI_VENEZUELA"
     * More information about the formats (S7): https://plc4x.apache.org/users/protocols/s7.html
     * @param itemNames List of Items
     * @param fieldAddresses List of Field Addresses
     * @param valArr An array of String values.
     */
    void writeMultipleFields(List<String> itemNames, List<String> fieldAddresses, String[] valArr);

}
