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
     * @return a set of dates and times, and the number of impressions on each date and time
     * @throws SQLException if an error occurs during SQL execution
     */
    public ResultSet getImpressions() throws SQLException
    {
        return this.db.getImpressions();
    }

    /**
     * @return a set of dates and times, and the number of clicks on each date and time
     * @throws SQLException if an error occurs during SQL execution
     */
    public ResultSet getClicks() throws SQLException
    {
        return this.db.getClicks();
    }
}
