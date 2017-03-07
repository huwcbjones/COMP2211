package t16.controller;

import t16.model.Database;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Modified by James Curran 27/2/17
 */
public class Controller
{
    private Database db;

    public Controller(Database db)
    {
        this.db = db;
    }

    /*
     * SQL access statements
     */

    /**
     * @return the total number of impressions in the campaign
     * @throws SQLException if an error occurs during SQL execution
     */
    public int getTotalImpressions() throws SQLException
    {
        return this.db.getTotalImpressions();
    }

    /**
     * @return the total number of clicks in the campaign
     * @throws SQLException if an error occurs during SQL execution
     */
    public int getTotalClicks() throws SQLException
    {
        return this.db.getTotalClicks();
    }

    /**
     * @return the total number of unique users that clicked an ad during the campaign
     * @throws SQLException if an error occurs during SQL execution
     */
    public int getTotalUniques() throws SQLException
    {
        return this.db.getTotalUniques();
    }

    /**
     * Currently a bounce is decided by only 1 page being viewed.
     * @return the total number of bounces that occurred during the campaign
     * @throws SQLException if an error occurs during SQL execution
     */
    public int getTotalBounces() throws SQLException {
        return this.db.getTotalBounces();
    }

    /**
     * @return the total number of conversions that occurred during the campaign
     * @throws SQLException if an error occurs during SQL execution
     */
    public int getTotalConversions() throws SQLException
    {
        return this.db.getTotalConversions();
    }

    /**
     * @return a set of dates and times, and the number of impressions on each date and time
     * @throws SQLException if an error occurs during SQL execution
     */
    public ResultSet getImpressionsOverTime() throws SQLException {
        return this.db.getImpressionsOverTime();
    }

    /**
     * @return a set of dates and times, and the number of clicks on each date and time
     * @throws SQLException if an error occurs during SQL execution
     */
    public ResultSet getClicksOverTime() throws SQLException {
        return this.db.getClicksOverTime();
    }

    /**
     * @return a set of dates and times, and the number of unique users on each date and time
     * @throws SQLException if an error occurs during SQL execution
     */
    public ResultSet getUniquesOverTime() throws SQLException {
        return this.db.getUniquesOverTime();
    }

    /**
     * Currently a bounce is defined as only 1 page being viewed.
     *
     * @return a set of dates and times, and the number of bounces that occurred on each date and time
     * @throws SQLException if an error occurs during SQL execution
     */
    public ResultSet getBouncesOverTime() throws SQLException {
        return this.db.getBouncesOverTime();
    }

    /**
     * @return a set of dates and times, and the number of conversions that occurred on each date and time
     * @throws SQLException if an error occurs during SQL execution
     */
    public ResultSet getConversionsOverTime() throws SQLException {
        return this.db.getConversionsOverTime();
    }
}
