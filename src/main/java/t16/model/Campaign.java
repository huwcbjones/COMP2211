package t16.model;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
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
    public HashMap<Interval, AxisPair> data;
    private String name;

    public Campaign(File dbFile) {
        this.name = dbFile.getName();
    }

    public void setData(String name, ResultSet results, boolean isFloat) throws SQLException {
        this.data = new HashMap<>();
        ArrayList<String> x = new ArrayList<>();
        ArrayList<Number> y = new ArrayList<>();
        results.next();
        while (!results.isLast()) {
            x.add(results.getString(1));
            if (isFloat) {
                y.add(results.getFloat(2));
            } else {
                y.add(results.getInt(2));
            }
            results.next();
        }
        this.data.put(Interval.SECONDS, new AxisPair(x, y));
    }

    public String getName() {
        return this.name;
    }

    public enum Interval {
        SECONDS,
        MINUTES,
        HOURS,
        DAYS,
        WEEKS,
        MONTHS,
    }

    public class AxisPair {
        private ArrayList<String> xAxis;
        private ArrayList<Number> yAxis;

        private AxisPair(ArrayList<String> xAxis, ArrayList<Number> yAxis) {
            this.xAxis = xAxis;
            this.yAxis = yAxis;
        }

        public ArrayList<String> getXAxis() {
            return this.xAxis;
        }

        public ArrayList<Number> getYAxis() {
            return this.yAxis;
        }
    }
}
