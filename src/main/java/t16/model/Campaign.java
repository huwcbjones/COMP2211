package t16.model;

import java.math.BigDecimal;
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
 */
public class Campaign {
    public HashMap<Interval, AxisPair> data;
    private String name;

    private long numberImpressions = -1;
    private long numberClicks = -1;
    private long numberUniques = -1;
    private long numberConversions = -1;
    private long numberBouncesPages = -1;
    private long numberBouncesTime = -1;
    private BigDecimal totalCost;
    private BigDecimal costPerClick;
    private BigDecimal costPerAcquisition;
    private BigDecimal costPer1kImpressions;
    private double clickThroughRate = -1d;
    private double bounceRate = -1d;

    public Campaign(String name) {
        this.name = name;
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

    public void setNumberImpressions(long numberImpressions) {
        if(this.numberImpressions == -1) this.numberImpressions = numberImpressions;
    }

    public void setNumberClicks(long numberClicks) {
        if(this.numberClicks == -1) this.numberClicks = numberClicks;
    }

    public void setNumberUniques(long numberUniques) {
        if(this.numberUniques == -1) this.numberUniques = numberUniques;
    }

    public void setNumberConversions(long numberConversions) {
        if(this.numberConversions == -1) this.numberConversions = numberConversions;
    }

    public void setNumberBouncesPages(long numberBounces) {
        if(this.numberBouncesPages == -1) this.numberBouncesPages = numberBounces;
    }

    public void setNumberBouncesTime(long numberBounces) {
        this.numberBouncesTime = numberBounces;
    }

    public void setTotalCost(BigDecimal totalCost) {
        if(this.totalCost == null) this.totalCost = totalCost;
    }

    public void setCostPerClick(BigDecimal costPerClick) {
        if(this.costPerClick == null) this.costPerClick = costPerClick;
    }

    public void setCostPerAcquisition(BigDecimal costPerAcquisition) {
        if(this.costPerAcquisition == null)  this.costPerAcquisition = costPerAcquisition;
    }

    public void setCostPer1kImpressions(BigDecimal costPer1kImpressions) {
        if(this.costPer1kImpressions == null)  this.costPer1kImpressions = costPer1kImpressions;
    }

    public void setClickThroughRate(double clickThroughRate) {
        if(this.clickThroughRate == -1) this.clickThroughRate = clickThroughRate;
    }

    public void setBounceRate(double bounceRate) {
        if(this.bounceRate == -1) this.bounceRate = bounceRate;
    }

    public long getNumberImpressions() {
        return numberImpressions;
    }

    public long getNumberClicks() {
        return numberClicks;
    }

    public long getNumberUniques() {
        return numberUniques;
    }

    public long getNumberConversions() {
        return numberConversions;
    }

    public long getNumberBouncesPages() {
        return numberBouncesPages;
    }

    public long getNumberBouncesTime() {
        return numberBouncesTime;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public BigDecimal getCostPerClick() {
        return costPerClick;
    }

    public BigDecimal getCostPerAcquisition() {
        return costPerAcquisition;
    }

    public BigDecimal getCostPer1kImpressions() {
        return costPer1kImpressions;
    }

    public double getClickThroughRate() {
        return clickThroughRate;
    }

    public double getBounceRate() {
        return bounceRate;
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
