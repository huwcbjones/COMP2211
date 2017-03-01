package t16.model;

import java.io.*;
import java.sql.*;
import java.util.zip.ZipInputStream;

/**
 * Created by Charles Gandon on 25/02/2017.
 * Modified by James Curran 26/2/17
 * This increment:
 * TODO Total conversions
 * TODO Uniques over time
 * TODO Bounces over time
 * TODO Conversions over time
 * TODO Click-through rate
 * Future increments:
 * TODO Total bounces when bounces are measured by time spent as opposed to pages viewed
 */
public class Database {
    public static Database database;
    private Connection connection;
    /*
    - We could use PooledConnection but it seems incompatible with the use of testament
     */
//    PooledConnection connection;

    public Database() {
        Database.database = this;
    }

    /**
     * Loads a Campaign from a Database file.
     */
    public Campaign loadCampaign(File databaseFile) {
        return new Campaign(databaseFile);
    }


    /**
     * Creates a campaign.
     *
     * @param zipFile An input .zip containing click_log.csv, impression_log.csv and server_log.csv.
     * @return the result of creating the campaign with the extracted .csv files
     */
    public Campaign createCampaign(File zipFile, File databaseFile) throws IOException {
        //Create temp folder
        File outputFolder = new File("temp");
        if (!outputFolder.exists()) {
            outputFolder.mkdir();
        }

        //Begin extracting .csvs to temp folder
        ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
        FileOutputStream logOutput;
        byte[] readBuffer = new byte[1024];
        int readLength;

        //Extract click log
        File clickFile = new File(outputFolder + File.separator + "click_log.csv");
        logOutput = new FileOutputStream(clickFile);
        readLength = zis.read(readBuffer);
        while (readLength > 0) {
            logOutput.write(readBuffer, 0, readLength);
            readLength = zis.read(readBuffer);
        }
        logOutput.close();
        zis.closeEntry();

        //Extract impression log
        File impressionFile = new File(outputFolder + File.separator + "impression_log.csv");
        logOutput = new FileOutputStream(impressionFile);
        readLength = zis.read(readBuffer);
        while (readLength > 0) {
            logOutput.write(readBuffer, 0, readLength);
            readLength = zis.read(readBuffer);
        }
        logOutput.close();
        zis.closeEntry();

        //Extract server log
        File serverFile = new File(outputFolder + File.separator + "server_log.csv");
        logOutput = new FileOutputStream(serverFile);
        readLength = zis.read(readBuffer);
        while (readLength > 0) {
            logOutput.write(readBuffer, 0, readLength);
            readLength = zis.read(readBuffer);
        }
        logOutput.close();
        zis.closeEntry();
        zis.close();

        Campaign result = createCampaign(clickFile, impressionFile, serverFile, databaseFile);
        deleteTemporaryFiles();
        return result;
    }

    public Campaign createCampaign(File clicks, File impressions, File server, File databaseFile) throws IOException {
        this.createDB(databaseFile.getAbsolutePath(), "login", "password");
        this.addTables(clicks, impressions, server);
        return new Campaign(databaseFile);
    }

    private void createDB(String name, String login, String password) throws IOException {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            // TODO: We probably shouldn't just hide this error, if we can't find the database, we should bubble an Exception back up. Just my 0,02€ - HJ
            e.printStackTrace();
        }
        try {
            this.connection = DriverManager.getConnection("jdbc:h2:~" + name, login, password);
//            this.connection = (PooledConnection) DriverManager.getConnection("jdbc:h2:~"+name, login, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addTables(File click, File impression, File server) {
        try {
            /*
                - From connections, create SQL statement to create the table from the files in parameters
             */
            Statement doclick = this.connection.createStatement();
            doclick.execute("CREATE TABLE Click(Date timestamp, ID float(53), Click_cost decimal(10,7)) " +
                    "AS SELECT * FROM CSVREAD('" + click.getPath() + "')");

            Statement doimpression = this.connection.createStatement();
            doimpression.execute("CREATE TABLE Impression(Date timestamp, ID float(53), Gender varchar(20), " +
                    "Age varchar(20), Income varchar(20), Context varchar(20), Impression_cost decimal(10,7)) " +
                    "AS SELECT * FROM CSVREAD('" + impression.getPath() + "')");

            Statement doserver = this.connection.createStatement();
            doserver.execute("CREATE TABLE Server(Date timestamp, ID float(53), Exit_date timestamp, Page_viewed int, " +
                    "Conversion varchar(20)) AS SELECT * FROM CSVREAD('" + server.getPath() + "')");

        } catch (SQLException e) {
            // TODO: Please don't silently kill exceptions, bubble them up and handle them
        }
    }

    private void deleteTemporaryFiles() {
        new File("temp/click_log.csv").delete();
        new File("temp/impression_log.csv").delete();
        new File("temp/server_log.csv").delete();
        new File("temp").delete();
    }

    /*
     * SQL access statements
     */

    /**
     * @return the total number of impressions in the campaign
     * @throws SQLException if an error occurs during SQL execution
     */
    public int getTotalImpressions() throws SQLException {
        Statement s = this.connection.createStatement();
        s.execute("SELECT COUNT(*) FROM Impression");
        return s.getResultSet().getInt(1);
    }

    /**
     * @return the total number of clicks in the campaign
     * @throws SQLException if an error occurs during SQL execution
     */
    public int getTotalClicks() throws SQLException {
        Statement s = this.connection.createStatement();
        s.execute("SELECT COUNT(*) FROM Click");
        return s.getResultSet().getInt(1);
    }

    /**
     * @return the total number of unique users that clicked an ad during the campaign
     * @throws SQLException if an error occurs during SQL execution
     */
    public int getTotalUniques() throws SQLException {
        Statement s = this.connection.createStatement();
        s.execute("SELECT COUNT(DISTINCT ID) FROM Click");
        return s.getResultSet().getInt(1);
    }

    /**
     * Currently a bounce is decided by only 1 page being viewed.
     *
     * @return the total number of bounces that occurred during the campaign
     * @throws SQLException if an error occurs during SQL execution
     */
    public int getTotalBounces() throws SQLException {
        Statement s = this.connection.createStatement();
        s.execute("SELECT COUNT(*) FROM Server WHERE Page_viewed = 1");
        return s.getResultSet().getInt(1);
    }


    /**
     * @return a set of dates and times, and the number of impressions on each date and time
     * @throws SQLException if an error occurs during SQL execution
     */
    public ResultSet getImpressions() throws SQLException {
        Statement s = this.connection.createStatement();
        s.execute("SELECT Date, COUNT(*) FROM Impression GROUP BY Date");
        return s.getResultSet();
    }

    /**
     * @return a set of dates and times, and the number of clicks on each date and time
     * @throws SQLException if an error occurs during SQL execution
     */
    public ResultSet getClicks() throws SQLException {
        Statement s = this.connection.createStatement();
        s.execute("SELECT Date, COUNT(*) FROM Click GROUP BY Date");
        return s.getResultSet();
    }

    /**
     * Unfinished.
     *
     * @return a set of dates and times, and the number of unique users
     * @throws SQLException
     */
    public ResultSet getUniques() throws SQLException {
        Statement s = this.connection.createStatement();
        //Need to add FROM and ;
        s.execute("SELECT Date, COUNT(DISTINCT )");
        return s.getResultSet();
    }

    /**
     * Unfinished.
     */
    public ResultSet getBounces() throws SQLException {
        Statement s = this.connection.createStatement();
        s.execute("");
        return s.getResultSet();
    }

    /**
     * Unfinished.
     */
    public ResultSet getConversions() throws SQLException {
        Statement s = this.connection.createStatement();
        s.execute("");
        return s.getResultSet();
    }

    /*
    PROPOSED SQL "HEAVY" ACCESS METHODS FOR VIEW
     */
    public ResultSet getClickCost(String n, String m) throws SQLException
    {
        Statement s = this.connection.createStatement();
        s.execute("Select Date, ID FROM Click WHERE click_cost>="+n+" AND click_cost<="+m+" ;");
        return s.getResultSet();
    }

    public ResultSet getGender(String gender) throws SQLException
    {
        Statement s = this.connection.createStatement();
        s.execute("Select Date, ID FROM Impression WHERE Gender='"+gender+"' ;");
        return s.getResultSet();
    }

    public ResultSet getIncome(String income) throws SQLException
    {
        Statement s = this.connection.createStatement();
        s.execute("Select Date, ID FROM Impression WHERE Income='"+income+"' ;");
        return s.getResultSet();
    }

    public ResultSet geContext(String context) throws SQLException
    {
        Statement s = this.connection.createStatement();
        s.execute("Select Date, ID FROM Impression WHERE Context='"+context+"' ;");
        return s.getResultSet();
    }

    public ResultSet getImpressionCost(String n, String m) throws SQLException
    {
        Statement s = this.connection.createStatement();
        s.execute("Select Date, ID FROM Impression WHERE Impression_cost>="+n+" AND Impression_cost<="+m+" ;");
        return s.getResultSet();
    }

    public ResultSet getServer(String id) throws SQLException
    {
        Statement s = this.connection.createStatement();
        s.execute("Select * FROM Server WHERE ID='"+id+"' ;");
        return s.getResultSet();
    }


}
