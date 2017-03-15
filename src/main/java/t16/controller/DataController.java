package t16.controller;

import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import t16.components.Importer;
import t16.exceptions.CampaignCreationException;
import t16.exceptions.CampaignLoadException;
import t16.exceptions.DatabaseConnectionException;
import t16.model.*;
import t16.utils.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Modified by James Curran 27/2/17
 * Modified by Huw Jones 06/03/2017
 */
public class DataController {
    protected static final Logger log = LogManager.getLogger(DataController.class);

    protected static final SimpleDateFormat mf = new SimpleDateFormat("MM");
    protected static final SimpleDateFormat df = new SimpleDateFormat("dd");
    protected static final SimpleDateFormat hf = new SimpleDateFormat("HH");

    private Database database;

    public enum RANGE {
        DAILY,
        HOURLY,
        WEEKLY,
        MONTHLY
    }

    public DataController() {
        this.database = Database.InitialiseDatabase();
    }

    /**
     * Shuts down the DataController
     */
    public void shutdown() {
        try {
            database.disconnect();
        } catch (SQLException e) {
            log.error("Error disconnecting database.");
            log.catching(e);
        }
    }
    //<editor-fold desc="Create/Open Campaign">
    /**
     * Creates a campaign and returns the created campaign
     *
     * @param zipFile      Zip file of log files
     * @param databaseFile Database file location
     * @return Created campaign
     * @throws CampaignCreationException Throw if an error occurred whilst creating the campaign
     */
    public Campaign createCampaign(File zipFile, File databaseFile) throws CampaignCreationException, CampaignLoadException {
        File outputDirectory = new File("temp");
        List<File> fileList;
        try {
            log.info("Extracting files...");
            fileList = extractFiles(outputDirectory, zipFile);
        } catch (IOException e) {
            log.catching(e);
            throw new CampaignCreationException("Failed to extract zip.", e);
        }
        if (fileList.size() != 3) {
            try {
                FileUtils.delete(outputDirectory);
            } catch (IOException e) {
                log.catching(e);
            }
            throw new CampaignCreationException("Incorrect zip format. Got " + fileList.size() + " files, expected 3.");
        }

        File file1, file2, file3;
        file1 = fileList.get(0);
        file2 = fileList.get(1);
        file3 = fileList.get(2);

        Campaign campaign;

        try {
            campaign = createCampaign(file1, file2, file3, databaseFile);
        } catch (CampaignCreationException e) {
            try {
                FileUtils.delete(outputDirectory);
            } catch (IOException e1) {
                log.catching(e1);
            }
            throw e;
        }

        try {
            FileUtils.delete(outputDirectory);
        } catch (IOException e1) {
            log.catching(e1);
        }
        return campaign;
    }

    private List<File> extractFiles(File outputDir, File zipFile) throws IOException {
        List<File> fileList = new ArrayList<>();

        //Create temp folder
        if (!outputDir.exists()) {
            outputDir.mkdir();
        }

        //Begin extracting .csvs to temp folder
        ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
        ZipEntry ze = zis.getNextEntry();

        byte[] buffer = new byte[1024];

        String fileName;
        File exportFile;
        FileOutputStream fos;
        while (ze != null) {
            fileName = new File(ze.getName()).getName();
            log.info("Unzipping {}...", fileName);
            exportFile = new File(outputDir + File.separator + fileName);

            fos = new FileOutputStream(exportFile);
            int length;
            while ((length = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }

            fos.close();
            ze = zis.getNextEntry();
            log.info("Unzipped to {}!", exportFile.getAbsolutePath());
            fileList.add(exportFile);
        }
        zis.closeEntry();
        zis.close();

        return fileList;
    }

    /**
     * Creates a campaign and returns the created campaign
     *
     * @param clickFile      Click log file location
     * @param impressionFile Impression log file location
     * @param serverFile     Server log file location
     * @param databaseFile   Database file location
     * @return Created campaign
     * @throws CampaignCreationException Thrown if an error occurred whilst creating the campaign
     */
    public Campaign createCampaign(File clickFile, File impressionFile, File serverFile, File databaseFile) throws CampaignCreationException, CampaignLoadException {
        log.info("Connecting to database {}", databaseFile.getAbsolutePath());
        try {
            database.connect(databaseFile, false);
        } catch (DatabaseConnectionException e) {
            try {
                database.disconnect();
            } catch (SQLException e1) {
                log.catching(e1);
            }
            databaseFile.delete();
            log.catching(e);
            throw new CampaignCreationException(e.getMessage(), e);
        }
        if (!database.isConnected()) {
            databaseFile.delete();
            throw new CampaignCreationException("Database failed to connect");
        }

        log.debug("Creating tables...");
        try {
            database.createTables();
        } catch (SQLException e) {
            try {
                database.disconnect();
            } catch (SQLException e1) {
                log.catching(e1);
            }
            databaseFile.delete();
            log.catching(e);
            throw new CampaignCreationException(e.getMessage(), e);
        }

        log.debug("Creating indexes...");
        try {
            database.createIndices();
        } catch (SQLException e) {
            try {
                database.disconnect();
            } catch (SQLException e1) {
                log.catching(e1);
            }
            databaseFile.delete();
            log.catching(e);
            throw new CampaignCreationException(e.getMessage(), e);
        }

        try {
            Importer importer = new Importer(clickFile, impressionFile, serverFile);
            importer.parseAndImport();
        } catch (Exception e) {
            try {
                database.disconnect();
            } catch (SQLException e1) {
                log.catching(e1);
            }
            databaseFile.delete();
            throw new CampaignCreationException(e.getMessage(), e);
        }

        // TODO: Fix this mess!
        Campaign c = new Campaign(databaseFile.getName());
        try {
            return setStats(c);
        } catch (SQLException e) {
            log.catching(e);
            throw new CampaignLoadException("Could not load stats.", e);
        }
    }

    /**
     * Opens a Campaign
     *
     * @param databaseFile Campaign file to open
     * @return The opened Campaign
     */
    public Campaign openCampaign(File databaseFile) throws CampaignLoadException {
        try {
            database.connect(databaseFile);
        } catch (DatabaseConnectionException e) {
            log.catching(e);
        }
        Campaign c = new Campaign(databaseFile.getName());
        try {
            return setStats(c);
        } catch (SQLException e) {
            log.catching(e);
            throw new CampaignLoadException("Could not load stats.", e);
        }
    }
    //</editor-fold>

    //<editor-fold desc="Insert Methods">
    /**
     * Inserts Click Logs into the database
     *
     * @param logList List of Click Logs
     * @throws SQLException Thrown if insert failed
     */
    public void insertClicks(List<ClickLog> logList) throws SQLException {
        try (Connection con = database.getConnection()) {
            long startTime = System.currentTimeMillis();
            con.setAutoCommit(false);
            try (PreparedStatement insert = con.prepareStatement("INSERT INTO `Clicks` VALUES (?, YEAR(?), MONTH(?), DAY_OF_MONTH(?), HOUR(?), ?, ?)")) {
                for (ClickLog c : logList) {
                    if (Thread.currentThread().isInterrupted()) {
                        insert.close();
                        con.close();
                    }
                    try {
                        insert.setTimestamp(1, c.getDate());
                        insert.setTimestamp(2, c.getDate());
                        insert.setTimestamp(3, c.getDate());
                        insert.setTimestamp(4, c.getDate());
                        insert.setTimestamp(5, c.getDate());
                        insert.setLong(6, c.getId());
                        insert.setBigDecimal(7, c.getCost());
                        insert.addBatch();
                    } catch (SQLException e) {
                        log.catching(e);
                        insert.close();
                        con.close();
                        throw e;
                    }
                }
                try {
                    insert.executeBatch();
                    con.setAutoCommit(true);
                    double time = (System.currentTimeMillis() - startTime) / 1000d;

                    log.info("Inserted {} rows in {} ({} per row).", logList.size(), DecimalFormat.getInstance().format(time), time / logList.size());
                } catch (SQLException e) {
                    log.catching(e);
                    insert.close();
                    con.close();
                    throw e;
                }
            }
        }
    }

    /**
     * Inserts Impression Logs into the database
     *
     * @param logList List of Impression Logs
     * @throws SQLException Throw if insert failed
     */
    public void insertImpressions(List<ImpressionLog> logList) throws SQLException {
        try (Connection con = database.getConnection()) {
            long startTime = System.currentTimeMillis();
            con.setAutoCommit(false);
            try (PreparedStatement insert = con.prepareStatement("INSERT INTO `Impressions` VALUES (?, YEAR(?), MONTH(?), DAY_OF_MONTH(?), HOUR(?), ?, ?, ?, ?, ?, ?)")) {
                for (ImpressionLog c : logList) {
                    if (Thread.currentThread().isInterrupted()) {
                        insert.close();
                        con.close();
                    }
                    try {
                        insert.setTimestamp(1, c.getDate());
                        insert.setTimestamp(2, c.getDate());
                        insert.setTimestamp(3, c.getDate());
                        insert.setTimestamp(4, c.getDate());
                        insert.setTimestamp(5, c.getDate());
                        insert.setLong(6, c.getId());
                        insert.setString(7, c.getGender().toString());
                        insert.setString(8, c.getAge());
                        insert.setString(9, c.getIncome().toString());
                        insert.setString(10, c.getContext().toString());
                        insert.setBigDecimal(11, c.getCost());
                        insert.addBatch();
                    } catch (SQLException e) {
                        log.catching(e);
                        insert.close();
                        con.close();
                        throw e;
                    }
                }
                try {
                    insert.executeBatch();
                    con.setAutoCommit(true);
                    double time = (System.currentTimeMillis() - startTime) / 1000d;

                    log.info("Inserted {} rows in {} ({} per row).", logList.size(), DecimalFormat.getInstance().format(time), time / logList.size());
                } catch (SQLException e) {
                    log.catching(e);
                    insert.close();
                    con.close();
                    throw e;
                }
            }
        }
    }

    /**
     * Inserts Server Logs into the database
     *
     * @param logList List of Server Logs
     * @throws SQLException Thrown if insert failed
     */
    public void insertServer(List<ServerLog> logList) throws SQLException {
        try (Connection con = database.getConnection()) {
            long startTime = System.currentTimeMillis();
            con.setAutoCommit(false);
            try (PreparedStatement insert = con.prepareStatement("INSERT INTO `Server` VALUES (?, YEAR(?), MONTH(?), DAY_OF_MONTH(?), HOUR(?), ?, ?, ?, ?)")) {
                for (ServerLog c : logList) {
                    if (Thread.currentThread().isInterrupted()) {
                        insert.close();
                        con.close();
                    }
                    try {
                        insert.setTimestamp(1, c.getEntry());
                        insert.setTimestamp(2, c.getEntry());
                        insert.setTimestamp(3, c.getEntry());
                        insert.setTimestamp(4, c.getEntry());
                        insert.setTimestamp(5, c.getEntry());
                        insert.setLong(6, c.getId());
                        insert.setTimestamp(7, c.getExit());
                        insert.setLong(8, c.getPageViews());
                        insert.setBoolean(9, c.isConversion());
                        insert.addBatch();
                    } catch (SQLException e) {
                        log.catching(e);
                        insert.close();
                        con.close();
                        throw e;
                    }
                }
                try {
                    insert.executeBatch();
                    con.setAutoCommit(true);
                    double time = (System.currentTimeMillis() - startTime) / 1000d;

                    log.info("Inserted {} rows in {} ({} per row).", logList.size(), DecimalFormat.getInstance().format(time), time / logList.size());
                } catch (SQLException e) {
                    log.catching(e);
                    insert.close();
                    con.close();
                    throw e;
                }
            }
        }
    }
    //</editor-fold>

    private Campaign setStats(Campaign c) throws SQLException {
        c.setNumberImpressions(getTotalImpressions());
        c.setNumberClicks(getTotalClicks());
        c.setNumberUniques(getTotalUniques());
        c.setNumberConversions(getTotalConversions());
        c.setNumberBounces(getTotalBounces());

        c.setTotalCost(getTotalCost());
        c.setCostPerClick(getCostPerClick());
        c.setCostPerAcquisition(getCostPerAcquisition());
        c.setCostPer1kImpressions(getCostPer1kImpressions());
        return c;
    }

    /**
     * Queries the database and returns the relevant result based on the Query object
     * @param query Query Object
     * @return List of points for chart
     * @throws SQLException
     */
    public List<Pair<String, Number>> getQuery(Query query) throws SQLException {
        try (Connection c = database.getConnection()) {
            try (PreparedStatement s = c.prepareStatement(query.getQuery())) {
                try (ResultSet res = s.executeQuery()) {
                    List<Pair<String, Number>> list = new ArrayList<>();
                    while (res.next()) {
                        if(query.isInt()) {
                            list.add(new Pair<>(res.getString(1), res.getInt(2)));
                        } else {
                            list.add(new Pair<>(res.getString(1), res.getFloat(2)));
                        }
                    }
                    return list;
                }
            }
        }
    }

    /**
     * @return the total number of impressions in the campaign
     * @throws SQLException if an error occurs during SQL execution
     */
    public long getTotalImpressions() throws SQLException {
        return this.database.getTotalImpressions();
    }

    /**
     * @return the total number of clicks in the campaign
     * @throws SQLException if an error occurs during SQL execution
     */
    public long getTotalClicks() throws SQLException {
        return this.database.getTotalClicks();
    }

    /**
     * @return the total number of unique users that clicked an ad during the campaign
     * @throws SQLException if an error occurs during SQL execution
     */
    public long getTotalUniques() throws SQLException {
        return this.database.getTotalUniques();
    }

    /**
     * Currently a bounce is decided by only 1 page being viewed.
     *
     * @return the total number of bounces that occurred during the campaign
     * @throws SQLException if an error occurs during SQL execution
     */
    public long getTotalBounces() throws SQLException {
        return this.database.getTotalBounces();
    }

    /**
     * @return the total number of conversions that occurred during the campaign
     * @throws SQLException if an error occurs during SQL execution
     */
    public long getTotalConversions() throws SQLException {
        return this.database.getTotalConversions();
    }

    public BigDecimal getTotalCost() throws SQLException {
        return this.database.getTotalCost();
    }

    public BigDecimal getCostPerClick() throws SQLException {
        return this.database.getCostPerClick();
    }

    public BigDecimal getCostPerAcquisition() throws SQLException {
        return this.database.getCostPerAcquisition();
    }

    public BigDecimal getCostPer1kImpressions() throws SQLException {
        return this.database.getCostPer1kImpressions();
    }
}
