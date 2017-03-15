package t16.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.h2.jdbcx.JdbcConnectionPool;
import t16.exceptions.DatabaseConnectionException;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Charles Gandon on 25/02/2017.
 * Modified by James Curran 26/2/17
 * Modified by Huw Jones 06/03/2017
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
    protected static final Logger log = LogManager.getLogger(Database.class);
    private static final String usr = "login";
    private static final String pwd = "password";

    public static Database database;
    private JdbcConnectionPool connectionPool;
    private boolean isConnected = false;
    private File databaseFile = null;

    public Database() {
        Database.database = this;
    }

    public static Database InitialiseDatabase() {
        if (database == null) {
            database = new Database();
        }
        return database;
    }

    public void connect(File databaseFile) throws DatabaseConnectionException {
        connect(databaseFile, usr, pwd, true);
    }

    public void connect(File databaseFile, String user, String password, boolean checkExist) throws DatabaseConnectionException {
        if (checkExist) {
            if (!databaseFile.exists())
                throw new DatabaseConnectionException("Database file does not exist.");
            if (!databaseFile.canWrite())
                throw new DatabaseConnectionException("Cannot open database to write");
        }

        String databasePath = databaseFile.getAbsolutePath();
        if (databasePath.contains(".h2.db")) databasePath = databasePath.replace(".h2.db", "");
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            throw new DatabaseConnectionException("Could not connect to database driver not present.", e);
        }

        this.connectionPool = JdbcConnectionPool.create("jdbc:h2:" + databasePath + ";MV_STORE=FALSE", user, password);
        this.isConnected = true;
        this.databaseFile = databaseFile;
    }

    public void createTables() throws SQLException {
        try (Connection c = getConnection()) {
            try (Statement createStmt = c.createStatement()) {
                log.debug("Creating tables...");
                // Create tables
                createStmt.execute("DROP ALL OBJECTS");
                createStmt.execute("CREATE TABLE Clicks(date TIMESTAMP, year INT, month TINYINT, day TINYINT, hour TINYINT, id BIGINT, click_cost DECIMAL(10, 7))");
                createStmt.execute("CREATE TABLE Impressions(date TIMESTAMP, year INT, month TINYINT, day TINYINT, hour TINYINT,  id BIGINT, gender CHAR(6), age CHAR(5), income CHAR(6), context CHAR(12), cost DECIMAL(10, 7))");
                createStmt.execute("CREATE TABLE Server(date TIMESTAMP, year INT, month TINYINT, day TINYINT, hour TINYINT,  id BIGINT, exit_date TIMESTAMP NULL, page_viewed INT(11), conversion BOOLEAN)");
            }
        }
        log.debug("Created tables!");
    }

    public void connect(File databaseFile, boolean checkExist) throws DatabaseConnectionException {
        connect(databaseFile, usr, pwd, checkExist);
    }

    public void connect(File databaseFile, String user, String password) throws DatabaseConnectionException {
        connect(databaseFile, user, password, true);
    }

    public void createIndices() throws SQLException {
        try (Connection c = getConnection()) {
            try (Statement indexStmt = c.createStatement()) {
                for (String t : new String[]{"Clicks", "Impressions", "Server"}) {
                    log.debug("Creating indexes for {}", t);
                    indexStmt.execute("CREATE INDEX ID_" + t + "_IND ON " + t + "(ID)");
                    indexStmt.execute("CREATE INDEX date_" + t + "_IND ON " + t + "(year, month, day, hour)");
                    /*indexStmt.execute("CREATE INDEX month_" + t + "_IND ON " + t + "(month)");
                    indexStmt.execute("CREATE INDEX day_" + t + "_IND ON " + t + "(day)");
                    indexStmt.execute("CREATE INDEX hour_" + t + "_IND ON " + t + "(hour)");*/
                }
            }
        }
        log.debug("Created all indexes!");
    }

    public Connection getConnection() throws SQLException {
        return connectionPool.getConnection();
    }

    /**
     * Gets the current database file
     *
     * @return
     */
    public File getDatabaseFile() {
        return databaseFile;
    }

    /*
     * SQL access statements
     */

    /**
     * @return the total number of impressions in the campaign
     * @throws SQLException if an error occurs during SQL execution
     */
    public long getTotalImpressions() throws SQLException {
        try (Connection c = this.connectionPool.getConnection()) {
            try (Statement s = c.createStatement()) {
                ResultSet set = s.executeQuery("SELECT COUNT(*) AS numberOfImpressions FROM `Impressions`");
                while (set.next()) {
                    return set.getLong("numberOfImpressions");
                }
                return 0;
            }
        }
    }

    /**
     * @return the total number of clicks in the campaign
     * @throws SQLException if an error occurs during SQL execution
     */
    public long getTotalClicks() throws SQLException {
        try (Connection c = this.connectionPool.getConnection()) {
            try (Statement s = c.createStatement()) {
                ResultSet set = s.executeQuery("SELECT COUNT(*) AS numberOfClicks FROM `Clicks`");
                while (set.next()) {
                    return set.getLong("numberOfClicks");
                }
                return 0;
            }
        }
    }

    /**
     * @return the total number of unique users that clicked an ad during the campaign
     * @throws SQLException if an error occurs during SQL execution
     */
    public long getTotalUniques() throws SQLException {
        try (Connection c = this.connectionPool.getConnection()) {
            try (Statement s = c.createStatement()) {
                ResultSet set = s.executeQuery("SELECT COUNT(DISTINCT ID) AS numberOfUniques FROM `Clicks`");
                while (set.next()) {
                    return set.getLong("numberOfUniques");
                }
                return 0;
            }
        }
    }

    /**
     * Defines a bounce as only 1 page being viewed.
     *
     * @return the total number of bounces that occurred during the campaign
     * @throws SQLException if an error occurs during SQL execution
     */
    public long getTotalBouncesPages() throws SQLException {
        try (Connection c = this.connectionPool.getConnection()) {
            try (Statement s = c.createStatement()) {
                ResultSet set = s.executeQuery("SELECT COUNT(*) AS numberOfBounces FROM `Server` WHERE `page_viewed`=1");
                while (set.next()) {
                    return set.getLong("numberOfBounces");
                }
                return 0;
            }
        }
    }

    /**
     * Defines a bounce as less than one minute being spent.
     *
     * @return the total number of bounces that occurred during the campaign
     * @throws SQLException if an error occurs during SQL execution
     */
    public long getTotalBouncesTime() throws SQLException {
        try (Connection c = this.connectionPool.getConnection()) {
            try (Statement s = c.createStatement()) {
                ResultSet set = s.executeQuery("SELECT COUNT(*) AS numberOfBounces FROM `Server` WHERE TIMESTAMPDIFF(2,`exit_date`-`date`) < 60");
                while (set.next()) {
                    return set.getLong("numberOfBounces");
                }
                return 0;
            }
        }
    }

    /**
     * @return the total number of conversions that occurred during the campaign
     * @throws SQLException if an error occurs during SQL execution
     */
    public long getTotalConversions() throws SQLException {
        try (Connection c = this.connectionPool.getConnection()) {
            try (Statement s = c.createStatement()) {
                ResultSet set = s.executeQuery("SELECT COUNT(*) AS numberOfConversions FROM `Server` WHERE `conversion`=1");
                while (set.next()) {
                    return set.getLong("numberOfConversions");
                }
                return 0;
            }
        }
    }

    public BigDecimal getTotalCost() throws SQLException {
        try (Connection c = this.connectionPool.getConnection()) {
            try (Statement s = c.createStatement()) {
                ResultSet set = s.executeQuery(
                        "SELECT (" +
                                "   (SELECT SUM(`clicks`.`click_cost`) FROM `clicks`)" +
                                "   + (SELECT SUM(`impressions`.`cost`) FROM `impressions`)" +
                                ") / 100 AS totalCost"
                );
                while (set.next()) {
                    return set.getBigDecimal("totalCost");
                }
                return BigDecimal.ZERO;
            }
        }
    }

    public BigDecimal getCostPerClick() throws SQLException {
        try (Connection c = this.connectionPool.getConnection()) {
            try (Statement s = c.createStatement()) {
                ResultSet set = s.executeQuery(
                        "SELECT" +
                                "  (SELECT " +
                                "     (" +
                                "       (SELECT SUM(`clicks`.`click_cost`) FROM `clicks`)" +
                                "      + (SELECT SUM(`impressions`.`cost`) FROM `impressions`)" +
                                "     ) / 100" +
                                "  ) / COUNT(*) AS costPerClick FROM `clicks`"
                );
                while (set.next()) {
                    return set.getBigDecimal("costPerClick");
                }
                return BigDecimal.ZERO;
            }
        }
    }

    public BigDecimal getCostPerAcquisition() throws SQLException {
        try (Connection c = this.connectionPool.getConnection()) {
            try (Statement s = c.createStatement()) {
                ResultSet set = s.executeQuery(
                        "SELECT" +
                                "  (SELECT" +
                                "     (" +
                                "       (SELECT SUM(`clicks`.`click_cost`) FROM `clicks`)" +
                                "      + (SELECT SUM(`impressions`.`cost`) FROM `impressions`)" +
                                "     ) / 100" +
                                "  ) / COUNT(*) AS costPerAcquisition FROM `Impressions`" +
                                "LEFT JOIN `Server`" +
                                "ON `Server`.`ID` = `Impressions`.`ID` AND" +
                                "    `Server`.`Date` = `Impressions`.`Date`" +
                                "WHERE `Server`.`Conversion` = 1"
                );
                while (set.next()) {
                    return set.getBigDecimal("costPerAcquisition");
                }
                return BigDecimal.ZERO;
            }
        }
    }

    public BigDecimal getCostPer1kImpressions() throws SQLException {
        try (Connection c = this.connectionPool.getConnection()) {
            try (Statement s = c.createStatement()) {
                ResultSet set = s.executeQuery(
                        "SELECT" +
                                "  (SELECT " +
                                "     (" +
                                "       (SELECT SUM(`clicks`.`click_cost`) FROM `clicks`)" +
                                "      + (SELECT SUM(`impressions`.`cost`) FROM `impressions`)" +
                                "     ) / 100" +
                                "  ) / COUNT(*) / 1000 AS costPer1kImpressions FROM `impressions`"
                );
                while (set.next()) {
                    return set.getBigDecimal("costPer1kImpressions");
                }
                return BigDecimal.ZERO;
            }
        }
    }


/*    *//**
     * @return a set of dates and times, and the number of impressions on each date and time
     * @throws SQLException if an error occurs during SQL execution
     *//*
    public ResultSet getImpressions() throws SQLException {
        Statement s = this.connection.createStatement();
        s.execute("SELECT CONCAT(TO_CHAR(Date, 'YYYY-MM-DD HH24'), ':00') AS dates, " +
                "COUNT(*) AS impressions " +
                "FROM Impression GROUP BY dates ORDER BY dates ASC;");
        return s.getResultSet();
    }*/

    /**
     * @return a set of dates and times, and the number of clicks on each date and time
     * @throws SQLException if an error occurs during SQL execution
     */
    public ResultSet getClicks() throws SQLException {
        Statement s = this.connectionPool.getConnection().createStatement();
        s.execute("SELECT CONCAT(TO_CHAR(date, 'YYYY-MM-DD HH24'), ':00') AS label, COUNT(*) AS click FROM `Clicks` GROUP BY label ORDER BY label ASC;");
        return s.getResultSet();
    }

    public ResultSet getClickThrough() throws SQLException {
        Statement s = this.connectionPool.getConnection().createStatement();
        s.execute("SELECT CONCAT(impression_rate.id, ':00') AS label, CAST(clicks AS FLOAT) / CAST(impressions AS FLOAT) AS clickThrough FROM" +
                "  (SELECT TO_CHAR(`Impressions`.`date`, 'YYYY-MM-DD HH24') AS id, COUNT(`Impressions`.`date`) AS impressions FROM `Impressions` GROUP BY id) impression_rate" +
                "  LEFT JOIN" +
                "  (SELECT TO_CHAR(`Clicks`.`date`, 'YYYY-MM-DD HH24') AS id, COUNT(`Clicks`.`date`) AS clicks FROM `Clicks` GROUP BY id) click_rate" +
                "  ON impression_rate.id = click_rate.id");
        return s.getResultSet();
    }

    /**
     * Currently a bounce is defined as only 1 page being viewed.
     *
     * @return a set of dates and times, and the number of bounces that occurred on each date and time
     * @throws SQLException if an error occurs during SQL execution
     */
  /*  public ResultSet getUniques() throws SQLException {
        Statement s = this.connection.createStatement();
        s.execute("SELECT CONCAT(TO_CHAR(Date, 'YYYY-MM-DD HH24'), ':00') AS dates, " +
                "COUNT(*) AS bounces " +
                "FROM Server WHERE Page_viewed = 1 " +
                "GROUP BY dates ORDER BY dates ASC;");
        return s.getResultSet();
    }

    */

    /**
     * Unfinished.
     *//*
    public ResultSet getBounces() throws SQLException {
        Statement s = this.connection.createStatement();
        s.execute("SELECT CONCAT(TO_CHAR(Date, 'YYYY-MM-DD HH24'), ':00') AS dates, " +
                "COUNT(*) AS conversions " +
                "FROM Server WHERE Conversion = 'Yes' " +
                "GROUP BY dates ORDER BY dates ASC;");
        return s.getResultSet();
    }

    public ResultSet getConversions() throws SQLException {
        Statement s = this.connection.createStatement();
        s.execute("SELECT CONCAT(impression_rate.id, ':00') AS label, CAST(clicks AS FLOAT) / CAST(impressions AS FLOAT) AS clickThrough FROM" +
                "  (SELECT TO_CHAR(`Impression`.`date`, 'YYYY-MM-DD HH24') AS id, COUNT(`Impression`.`date`) AS impressions FROM `Impression` GROUP BY id) impression_rate" +
                "  LEFT JOIN" +
                "  (SELECT TO_CHAR(`Click`.`date`, 'YYYY-MM-DD HH24') AS id, COUNT(`Click`.`date`) AS clicks FROM `Click` GROUP BY id) click_rate" +
                "  ON impression_rate.id = click_rate.id");
        return s.getResultSet();
    }*/

    /*
    PROPOSED SQL "HEAVY" ACCESS METHODS FOR VIEW
     */
  /*  public ResultSet getClickCost(String n, String m) throws SQLException {
        Statement s = this.connection.createStatement();
        s.execute("Select Date, ID FROM Click WHERE click_cost>=" + n + " AND click_cost<=" + m + " ;");
        return s.getResultSet();
    }

    public ResultSet getGender(String gender) throws SQLException {
        Statement s = this.connection.createStatement();
        s.execute("Select Date, ID FROM Impression WHERE Gender='" + gender + "' ;");
        return s.getResultSet();
    }

    public ResultSet getIncome(String income) throws SQLException {
        Statement s = this.connection.createStatement();
        s.execute("Select Date, ID FROM Impression WHERE Income='" + income + "' ;");
        return s.getResultSet();
    }

    public ResultSet geContext(String context) throws SQLException {
        Statement s = this.connection.createStatement();
        s.execute("Select Date, ID FROM Impression WHERE Context='" + context + "' ;");
        return s.getResultSet();
    }

    public ResultSet getImpressionCost(String n, String m) throws SQLException {
        Statement s = this.connection.createStatement();
        s.execute("Select Date, ID FROM Impression WHERE Impression_cost>=" + n + " AND Impression_cost<=" + m + " ;");
        return s.getResultSet();
    }

    public ResultSet getServer(String id) throws SQLException {
        Statement s = this.connection.createStatement();
        s.execute("Select * FROM Server WHERE ID='" + id + "' ;");
        return s.getResultSet();
    }*/
    public void disconnect() throws SQLException {
        if (!isConnected()) return;
        log.warn("There are {} active connections", connectionPool.getActiveConnections());
        connectionPool.dispose();
        this.isConnected = false;
    }

    public boolean isConnected() {
        return isConnected;
    }
}
