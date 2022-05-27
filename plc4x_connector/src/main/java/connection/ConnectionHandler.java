package connection;

import org.apache.plc4x.java.*;
import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.exceptions.PlcConnectionException;
import org.apache.plc4x.java.utils.connectionpool.PooledPlcDriverManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class handles the connection to a driver.
 * It is an adaption to the driver requirements of the pipeline.
 * It handles a simple PlcDriverManager.
 *
 * @author Herberto Werner
 */
public class ConnectionHandler implements IConnector {

    //Constants
    private static final Logger log = LoggerFactory.getLogger(ConnectionHandler.class);
    //Fields
    private String adr;
    private int numConnections;
    private PlcDriverManager driverManager;
    private PlcDriverManager driverManagerPool;

    /**
     * Constructor of the ConnectionHandlerSimple.
     * @param adr address of the driver.
     * @param driverManager driverManager for the connection.
     */
    public ConnectionHandler(String adr, PlcDriverManager driverManager){
        this.adr = adr;
        this.driverManager = driverManager;
    }

    /**
     * Constructor of the ConnectionHandlerSimple with a PoolDriverManager.
     * @param adr address of the driver.
     */
    public ConnectionHandler(String adr, PooledPlcDriverManager driverManagerPool) {
        this.adr = adr;
        this.driverManagerPool = driverManagerPool;
    }


    @Override
    public PlcConnection connect(boolean simple) throws PlcConnectionException {
        PlcConnection con;

        if(simple) {
            con = driverManager.getConnection(adr);
        }
        else {
            con = driverManagerPool.getConnection(adr);
        }
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

    /**
     * Returns the PLCDriverManagerPool
     * @return PLCDriverManagerPool.
     */
    public PooledPlcDriverManager getDriverManagerPool() {
        return (PooledPlcDriverManager) driverManagerPool;
    }

    /**
     * Sets the PLCDriverManagerPool
     * @return PLCDriverManagerPool.
     */
    public void setDriverManagerPool(PooledPlcDriverManager driverManagerPool) {
        this.driverManagerPool = driverManagerPool;
    }
}
