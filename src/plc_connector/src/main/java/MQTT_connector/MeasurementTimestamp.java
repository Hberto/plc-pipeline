package MQTT_connector;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

/**
 *  This Class measures the arrival time of the MQTT Message.
 *  The arrival time will be stored in a csv file.
 * Source: https://www.geeksforgeeks.org/writing-a-csv-file-in-java-using-opencsv/
 * @author Herberto Werner
 */
public class MeasurementTimestamp {


    /**
     * Puts header and Timestamp data into a csv file.
     * @param measurementTS the passed timestamp
     */
    public static void measureMqttAndSaveToCSV(String csvFile, Timestamp measurementTS, int nr){
        long measureMilli = measurementTS.getTime();
        Date date = measurementTS;

        String[] header = { "MQTTMeasurementNr", "Date", "ArrivalTimeInMS" };
        String[] data = {String.valueOf(nr), date.toString(), String.valueOf(measureMilli)};

        //Writes Header and then the data to the csv file
        writeDataLineByLine(csvFile,header);
        writeDataLineByLine(csvFile,data);
    }

    /**
     * Private Methods
     */

    /**
     * Writes Data line by line.
     * @param filePath the file to use for.
     * @param data the data to be written in the file.
     */
    private static void writeDataLineByLine(String filePath, String[] data)
    {
        // first create file object for file placed at location
        // specified by filepath
        File file = new File(filePath);
        try {
            // create FileWriter object with file as parameter
            FileWriter outputfile = new FileWriter(file, true);

            // create CSVWriter with ',' as separator
            CSVWriter writer = new CSVWriter(outputfile, ',',
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);

            // add data to csv
            writer.writeNext(data);

            writer.close();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
