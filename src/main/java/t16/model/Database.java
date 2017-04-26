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

        boolean shouldCreateTempTables = false;
        // Test the connection - should provide a better error message if the database is already in use
        try (Connection c = getConnection()) {
            try (Statement s = c.createStatement()) {
                try (ResultSet set = s.executeQuery("SELECT count(*) as count FROM INFORMATION_SCHEMA.tables WHERE TABLE_SCHEMA='PUBLIC';")) {
                    while (set.next()) {
                        shouldCreateTempTables = (set.getInt("count") == 3);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Failed to open database. Is the database already in use?", e);
        }


        try {
            if(shouldCreateTempTables) createTempTables();
        } catch (SQLException e) {
            log.catching(e);
            throw new DatabaseConnectionException("Failed to open database. Temporary structures could not be created.", e.getCause());
        }

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
                }
                indexStmt.execute("CREATE INDEX conversion_IND ON `Server` (`conversion`)");
            }
        }
        log.debug("Created all indexes!");
    }

    private void createTempTables() throws SQLException {
        log.debug("Creating temporary tables...");
        createTotalCostTable();
        log.debug("Created temporary tables!");
    }

    private void createTotalCostTable() throws SQLException {
        try (Connection c = getConnection()) {
            try (Statement createStmt = c.createStatement()) {
                // Create temporary
                createStmt.execute("CREATE TEMPORARY TABLE TotalCost(date TIMESTAMP, year INT, month TINYINT, day TINYINT, hour TINYINT, cost DECIMAL(10, 7), gender CHAR(6), age CHAR(5), income CHAR(6), context CHAR(12))");
                createStmt.execute("CREATE INDEX cost_TotalCost_IND ON TotalCost(year, month, day, hour)");
                createStmt.execute("CREATE INDEX date_TotalCost_IND ON TotalCost(year, month, day, hour)");
                createStmt.execute(
                        "INSERT INTO TotalCost (date, year, month, day, hour, gender, age, income, context, cost)\n" +
                                "  SELECT TO_TIMESTAMP(CONCAT(`i`.`YEAR`, '-', `i`.`MONTH`, '-', `i`.`DAY`, ' ', `i`.`HOUR`), 'YYYY-MM-DD HH24'), `i`.`YEAR`, `i`.`MONTH`, `i`.`DAY`, `i`.`HOUR`, `i`.`gender`, `i`.`age`, `i`.`income`, `i`.`context`, SUM(`i`.`cost`) + SUM(`c`.`click_cost`) AS totalCost\n" +
                                "  FROM\n" +
                                "    `Impressions` `i`\n" +
                                "    RIGHT JOIN `Clicks` `c` ON `c`.ID = `i`.ID  AND `i`.`date` BETWEEN DATEADD('MINUTE', -10, `c`.`date`) AND DATEADD('MINUTE', 10, `c`.`date`)\n" +
                                "  GROUP BY `i`.`YEAR`, `i`.`MONTH`, `i`.`DAY`, `i`.`HOUR`, `i`.`gender`, `i`.`age`, `i`.`income`, `i`.`context`"
                );
            }
        }

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
                try (ResultSet set = s.executeQuery("SELECT COUNT(*) AS numberOfImpressions FROM `Impressions`")) {
                    while (set.next()) {
                        return set.getLong("numberOfImpressions");
                    }
                    return 0;
                }
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
                try (ResultSet set = s.executeQuery("SELECT COUNT(*) AS numberOfClicks FROM `Clicks`")) {
                    while (set.next()) {
                        return set.getLong("numberOfClicks");
                    }
                    return 0;
                }
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
                try (ResultSet set = s.executeQuery("SELECT COUNT(DISTINCT ID) AS numberOfUniques FROM `Clicks`")) {
                    while (set.next()) {
                        return set.getLong("numberOfUniques");
                    }
                    return 0;
                }
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
                try (ResultSet set = s.executeQuery("SELECT COUNT(*) AS numberOfBounces FROM `Server` WHERE `page_viewed`=1")) {
                    while (set.next()) {
                        return set.getLong("numberOfBounces");
                    }
                    return 0;
                }
            }
        }
    }

    /**
     * Defines a bounce as less than thirty seconds being spent.
     *
     * @return the total number of bounces that occurred during the campaign
     * @throws SQLException if an error occurs during SQL execution
     */
    public long getTotalBouncesTime() throws SQLException {
        try (Connection c = this.connectionPool.getConnection()) {
            try (Statement s = c.createStatement()) {
                ResultSet set = s.executeQuery("SELECT COUNT(*) AS numberOfBounces FROM `Server` WHERE TIMESTAMPDIFF(SECOND,`date`,`exit_date`) < 30");
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
                try (ResultSet set = s.executeQuery("SELECT COUNT(*) AS numberOfConversions FROM `Server` WHERE `conversion`=1")) {
                    while (set.next()) {
                        return set.getLong("numberOfConversions");
                    }
                    return 0;
                }
            }
        }
    }

    public BigDecimal getTotalCost() throws SQLException {
        try (Connection c = this.connectionPool.getConnection()) {
            try (Statement s = c.createStatement()) {
                try (ResultSet set = s.executeQuery("SELECT SUM(cost) / 100 AS totalCost FROM TotalCost")) {
                    while (set.next()) {
                        return set.getBigDecimal("totalCost");
                    }
                    return BigDecimal.ZERO;
                }
            }
        }
    }

    public BigDecimal getCostPerClick() throws SQLException {
        try (Connection c = this.connectionPool.getConnection()) {
            try (Statement s = c.createStatement()) {
                try (ResultSet set = s.executeQuery("SELECT (SELECT SUM(cost) / 100 FROM TotalCost) / NULLIF((SELECT COUNT(*) FROM Clicks), 0) AS costPerClick")) {
                    while (set.next()) {
                        return set.getBigDecimal("costPerClick");
                    }
                    return BigDecimal.ZERO;
                }
            }
        }
    }

    public BigDecimal getCostPerAcquisition() throws SQLException {
        try (Connection c = this.connectionPool.getConnection()) {
            try (Statement s = c.createStatement()) {
                try (ResultSet set = s.executeQuery("SELECT (SELECT SUM(cost) / 100 FROM TotalCost) / NULLIF((SELECT COUNT(*) FROM Server WHERE Conversion = 1), 0) AS costPerAcquisition")) {
                    while (set.next()) {
                        return set.getBigDecimal("costPerAcquisition");
                    }
                    return BigDecimal.ZERO;
                }
            }
        }
    }

    public BigDecimal getCostPer1kImpressions() throws SQLException {
        try (Connection c = this.connectionPool.getConnection()) {
            try (Statement s = c.createStatement()) {
                try (ResultSet set = s.executeQuery("SELECT (SELECT SUM(cost) / 100 FROM TotalCost) / NULLIF((SELECT COUNT(*) / 1000 FROM Impressions), 0) AS costPer1kImpressions")) {
                    while (set.next()) {
                        return set.getBigDecimal("costPer1kImpressions");
                    }
                    return BigDecimal.ZERO;
                }
            }
        }
    }

    public double getClickThroughRate() throws SQLException {
        try (Connection c = this.connectionPool.getConnection()) {
            try (Statement s = c.createStatement()) {
                ResultSet set = s.executeQuery(
                        "SELECT CAST(clicks AS DOUBLE)/NULLIF(CAST(impressions AS DOUBLE), 0) AS clickThrough FROM" +
                                "  (SELECT COUNT(*) AS `impressions` FROM `Impressions`) i_r" +
                                "  LEFT JOIN" +
                                "  (SELECT  COUNT(*) AS `clicks` FROM `Clicks`) c_r"
                );
                while (set.next()) {
                    return set.getDouble("clickThrough");
                }
                return 0d;
            }
        }
    }

    public void disconnect() throws SQLException {
        if (!isConnected()) return;
        if (connectionPool.getActiveConnections() != 0) {
            log.warn("There are {} active connections", connectionPool.getActiveConnections());
        } else {
            log.info("All connections are closed!");
        }
        connectionPool.dispose();
        this.isConnected = false;
    }

    public boolean isConnected() {
        return isConnected;
    }
}
