package t16.controller;

import t16.interfaces.IChartRenderer;
import t16.model.Database;

import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Modified by James Curran 27/2/17
 */
public class DataController
{
    private Database db;

    public DataController(Database db)
    {
        this.db = db;
    }

    /*
     * SQL access statements
     */

    /**
     *
     * @param range
     * @param from
     * @param to
     */
    public void getClicks(RANGE range, Timestamp from, Timestamp to, IChartRenderer renderer){


    }
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

    public enum RANGE {
        DAILY,
        HOURLY,
        WEEKLY,
        MONTHLY
    }
}
