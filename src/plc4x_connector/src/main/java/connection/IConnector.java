package connection;

import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.exceptions.PlcConnectionException;

/**
 *  Interface defines for connecting with
 *  a device with a specific protocol.
 *
 * @author  Herberto Werner
 */
public interface IConnector {

    /**
     * Establishes a connection to a driver.
     * Attention: ConnectionString varies of which protocol is used.
     * S7: IP-Address of device (TIA)
     * OPC: opcua:{transport}://{ip-address}:{port}?{options}
     * See more: https://plc4x.apache.org/users/protocols/index.html
     * @param simple if true it's driverManager otherwise it's a DriverManagerPool.
     * @throws PlcConnectionException if Connection couldn't be established.
     */
    PlcConnection connect(boolean simple) throws PlcConnectionException;
}
