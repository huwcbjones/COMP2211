package t16.model;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A Campaign
 *
 * @author Huw Jones
 * @since 25/02/2017
 * Modified by James Curran 28/2/17
 * TODO Here is a possible format for the Campaign. What do you think? After calculating the axes for seconds, there could be some additional calculation to get the average y values for minutes, hours etc.
 */
public class Campaign {
    private String name;

    public enum Interval
    {
        SECONDS,
        MINUTES,
        HOURS,
        DAYS,
        WEEKS,
        MONTHS,
    }

    public Campaign(File dbFile)
    {
        this.name = dbFile.getName();
    }

    public HashMap<Interval, AxisPair> data;

    public void setData(String name, ResultSet results) throws SQLException
    {
		this.data = new HashMap<>();
		ArrayList<Timestamp> x = new ArrayList<>();
		ArrayList<Integer> y = new ArrayList<>();
        results.next();
		while(!results.isLast())
		{
			x.add(results.getTimestamp(1));
			y.add(results.getInt(2));
			results.next();
		}
		this.data.put(Interval.SECONDS, new AxisPair(x, y));
    }

    public String getName()
    {
        return this.name;
    }

    private class AxisPair
    {
        private ArrayList<Timestamp> xAxis;
		private ArrayList<Integer> yAxis;
		public AxisPair(ArrayList<Timestamp> xAxis, ArrayList<Integer> yAxis)
		{
			this.xAxis = xAxis;
			this.yAxis = yAxis;
		}
		
		public ArrayList<Timestamp> getXAxis()
		{
			return this.xAxis;
		}
			
		public ArrayList<Integer> getYAxis()
		{
			return this.yAxis;
		}
    }
}
