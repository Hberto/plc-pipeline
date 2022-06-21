package ReadOperations;

import java.util.List;

/**
 *  Interface defines for reading fields from
 *  a device with a specific protocol.
 *
 * @author  Herberto Werner
 */
public interface IReadPLC {

    /**
     * Reads a single data field from the plc. Specific Name of the item
     * and field should be spelled correctly.
     * The item is the name of the variable in the table. A variable could be
     * in the standard variable tabular, data block etc.
     * The field is the address of the variable.
     * Example: itemName: "test" and fieldAdr: "%I0.0"
     *
     * @param itemName name of the item
     * @param fieldAdr address of the field
     */
    void readSingleField(String itemName, String fieldAdr);

    /**
     * Reads iteratively data fields from the plc. Specific Name of the item
     * and field should be spelled correctly.
     * The item is the name of the variable in the table. A variable could be
     * in the standard variable tabular, data block etc.
     * The field is the address of the variable.
     * Example: itemName: "test" and fieldAdr: "%I0.0"
     * @param itemNames List of Items
     * @param fieldAddresses List of Field Addresses
     */
    void readMultipleFields(List<String> itemNames, List<String> fieldAddresses);

}
