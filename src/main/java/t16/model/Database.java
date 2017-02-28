package t16.model;

import com.sun.jndi.ldap.pool.PooledConnection;

import java.io.*;
import java.sql.*;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * Created by Charles Gandon on 25/02/2017.
 * Modified by James Curran 26/2/17
 */
public class Database
{
    private Connection connection;
    /*
    - We could use PooledConnection but it seems incompatible with the use of testament
     */
//    PooledConnection connection;

    public Database()
    {

    }

    public void createDB(String name, String login, String password)  {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            this.connection = DriverManager.getConnection("jdbc:h2:~"+name, login, password);
//            this.connection = (PooledConnection) DriverManager.getConnection("jdbc:h2:~"+name, login, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("DB created");

    }

    public void addTables(File impression, File click, File server){
        try {
            /*
                - From connections, create SQL statement to create the table from the files in parameters
             */
            Statement doimpression = this.connection.createStatement();

            doimpression.execute("CREATE TABLE Impression(Date datetime, ID float(53), Gender varchar(20), " +
                    "Age varchar(20), Income varchar(20), Context varchar(20), Impression_cost decimal(10,7)) " +
                    "AS SELECT * FROM CSVREAD('"+impression.getPath()+"')");


            Statement doclick = this.connection.createStatement();

            doclick.execute("CREATE TABLE Click(Date datetime, ID float(53), Click_cost decimal(10,7)) " +
                    "AS SELECT * FROM CSVREAD('"+click.getPath()+"')");


            Statement doserver = this.connection.createStatement();

            doserver.execute("CREATE TABLE Server(Date datetime, ID float(53), Exit_date datetime, Page_viewed int, " +
                    "Conversion varchar(20)) AS SELECT * FROM CSVREAD('"+server.getPath()+"')");

        }catch (SQLException e){

        }
    }

    public void go() throws SQLException, IOException, SecurityException
    {
//        //DROP THE CAMPAIGN TABLES BEFORE CREATING
//        this.connection.createStatement().execute("DROP TABLE Impression");
//        this.connection.createStatement().execute("DROP TABLE Click");
//        this.connection.createStatement().execute("DROP TABLE Server");
        this.createDB("Campaign", "login", "password");

        //Create extractedLogs folder
        File outputFolder = new File("resources/extractedLogs");
        if(!outputFolder.exists())
        {
            outputFolder.mkdir();
        }

        ZipFile zipFile = new ZipFile("resources/campaign_data.zip");
        ZipInputStream zis = new ZipInputStream(new FileInputStream("resources/campaign_data.zip"));
        FileOutputStream logOutput;
        byte[] readBuffer = new byte[1024];
        int readLength;

        //Extract click log
        zis.getNextEntry();
        File clickFile = new File(outputFolder + File.separator + "click_log.csv");
        logOutput = new FileOutputStream(clickFile);
        readLength = zis.read(readBuffer);
        while(readLength > 0)
        {
            logOutput.write(readBuffer, 0, readLength);
            readLength = zis.read(readBuffer);
        }
        logOutput.close();

        //Extract impression log
        zis.getNextEntry();
        File impressionFile = new File(outputFolder + File.separator + "impression_log.csv");
        logOutput = new FileOutputStream(impressionFile);
        readLength = zis.read(readBuffer);
        while(readLength > 0)
        {
            logOutput.write(readBuffer, 0, readLength);
            readLength = zis.read(readBuffer);
        }
        logOutput.close();

        //Extract server log
        zis.getNextEntry();
        File serverFile = new File(outputFolder + File.separator + "server_log.csv");
        logOutput = new FileOutputStream(serverFile);
        readLength = zis.read(readBuffer);
        while(readLength > 0)
        {
            logOutput.write(readBuffer, 0, readLength);
            readLength = zis.read(readBuffer);
        }
        logOutput.close();
        zipFile.close();

        this.addTables(impressionFile, clickFile, serverFile);
//        SQLException: "An error occurred in the database manager."
//        ZipException: "Input .zip file was incorrectly formatted.");
//        IOException: "An error occurred while reading the input .zip file.");
//        SecurityException: "A security manager is prohibiting the program from reading the input .zip file.");
    }

    /*
     * SQL access statements
     */

    /**
     * @return a set of dates and times, and the number of impressions on each date and time
     * @throws SQLException if an error occurs during SQL execution
     */
    public ResultSet getImpressions() throws SQLException
    {
        Statement s = this.connection.createStatement();
        s.execute("SELECT Date, COUNT(*) FROM Impression GROUP BY Date");
        return s.getResultSet();
    }

    /**
     * @return a set of dates and times, and the number of clicks on each date and time
     * @throws SQLException if an error occurs during SQL execution
     */
    public ResultSet getClicks() throws SQLException
    {
        Statement s = this.connection.createStatement();
        s.execute("SELECT Date, COUNT(*) FROM Click GROUP BY Date");
        return s.getResultSet();
    }

    /**
     * Unfinished.
     * @return a set of dates and times, and the number of unique users
     * @throws SQLException
     */
    public ResultSet getUniques() throws SQLException
    {
        Statement s = this.connection.createStatement();
        s.execute("");
        return s.getResultSet();
    }

    /**
     * Unfinished.
     */
    public ResultSet getBounces() throws SQLException
    {
        Statement s = this.connection.createStatement();
        s.execute("");
        return s.getResultSet();
    }

    /**
     * Unfinished.
     */
    public ResultSet getConversions() throws SQLException
    {
        Statement s = this.connection.createStatement();
        s.execute("");
        return s.getResultSet();
    }
}
