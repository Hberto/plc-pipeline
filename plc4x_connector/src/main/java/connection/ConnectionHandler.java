package connection;

import org.apache.plc4x.java.*;
import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.exceptions.PlcConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class handles the connection to a driver.
 * It is an adaption to the driver requirements of the pipeline.
 * It handles a simple PlcDriverManager.
 *
 * @author Herberto Werner
 */
public class ConnectionHandlerSimple implements IConnector {

    //Constants
    private static final Logger log = LoggerFactory.getLogger(ConnectionHandlerSimple.class);
    //Fields
    private String adr;
    private int numConnections;
    private PlcDriverManager driverManager;

    /**
     * Constructor of the ConnectionHandlerSimple.
     * @param adr address of the driver.
     * @param driverManager driverManager for the connection.
     */
    public ConnectionHandlerSimple(String adr, PlcDriverManager driverManager){
        this.adr = adr;
        this.driverManager = driverManager;
    }

    /**
     * Constructor of the ConnectionHandlerSimple without DriverManager.
     * @param adr address of the driver.
     */
    public ConnectionHandlerSimple(String adr) {
        this.adr = adr;
        driverManager = new PlcDriverManager();
    }

    /**
     * Constructor of the ConnectionHandlerSimple without DriverManager.
     */
    public ConnectionHandlerSimple() {
        driverManager = new PlcDriverManager();
    }

    @Override
    public PlcConnection connect(int numberOfConnections) throws PlcConnectionException {

        if (numberOfConnections < 0 ) {
            log.error("Negative not allowed");
            throw new IllegalArgumentException("Cannot be negative");
        }

        PlcConnection con = driverManager.getConnection(adr);
        numConnections++;

        return con;
    }

    /**
     * Returns the address of driver.
     * @return driver address.
     */
    public String getAdr() {
        return adr;
    }

    /**
     * Sets the address of the driver.
     * @param adr driver address.
     */
    public void setAdr(String adr) {
        this.adr = adr;
    }

    /**
     * Returns the established number of Connections.
     * @return number of connections.
     */
    public int getNumConnections() {
        return numConnections;
    }

    /**
     * Sets the NumberOfConnections.
     * @param numConnections number of connections.
     */
    public void setNumConnections(int numConnections) {
        this.numConnections = numConnections;
    }

    /**
     * Returns the PLCDriverManager
     * @return PLCDriverManager.
     */
    public PlcDriverManager getDriverManager() {
        return driverManager;
    }

    /**
     * Sets the PLCDriverManager.
     * @param driverManager PLCDriverManager.
     */
    public void setDriverManager(PlcDriverManager driverManager) {
        this.driverManager = driverManager;
    }
}
