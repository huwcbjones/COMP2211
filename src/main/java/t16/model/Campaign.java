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
 * TODO Here is a possible format for the Campaign. What do you think? After calculating the axes for seconds, there could be some additional calculation to get the average y values for minutes, hours etc.
 */
public class Campaign {
    public HashMap<Interval, AxisPair> data;
    private String name;

    private long numberImpressions = -1;
    private long numberClicks = -1;
    private long numberUniques = -1;
    private long numberConversions = -1;
    private long numberBounces = -1;
    private BigDecimal totalCost = BigDecimal.ZERO;
    private BigDecimal costPerClick = BigDecimal.ZERO;
    private BigDecimal costPerAcquisition = BigDecimal.ZERO;
    private BigDecimal costPer1kImpressions = BigDecimal.ZERO;
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

    public long getNumberImpressions() {
        return numberImpressions;
    }

    public void setNumberImpressions(long numberImpressions) {
        if (this.numberImpressions == -1) this.numberImpressions = numberImpressions;
    }

    public long getNumberClicks() {
        return numberClicks;
    }

    public void setNumberClicks(long numberClicks) {
        if (this.numberClicks == -1) this.numberClicks = numberClicks;
    }

    public long getNumberUniques() {
        return numberUniques;
    }

    public void setNumberUniques(long numberUniques) {
        if (this.numberUniques == -1) this.numberUniques = numberUniques;
    }

    public long getNumberConversions() {
        return numberConversions;
    }

    public void setNumberConversions(long numberConversions) {
        if (this.numberConversions == -1) this.numberConversions = numberConversions;
    }

    public long getNumberBounces() {
        return numberBounces;
    }

    public void setNumberBounces(long numberBounces) {
        if (this.numberBounces == -1) this.numberBounces = numberBounces;
    }

    public BigDecimal getTotalCost() {
        if (totalCost == null) return BigDecimal.ZERO;
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        if (this.totalCost.equals(BigDecimal.ZERO)) this.totalCost = totalCost;
    }

    public BigDecimal getCostPerClick() {
        if (costPerClick == null) return BigDecimal.ZERO;
        return costPerClick;
    }

    public void setCostPerClick(BigDecimal costPerClick) {
        if (this.costPerClick.equals(BigDecimal.ZERO)) this.costPerClick = costPerClick;
    }

    public BigDecimal getCostPerAcquisition() {
        if (costPerAcquisition == null) return BigDecimal.ZERO;
        return costPerAcquisition;
    }

    public void setCostPerAcquisition(BigDecimal costPerAcquisition) {
        if (this.costPerAcquisition.equals(BigDecimal.ZERO)) this.costPerAcquisition = costPerAcquisition;
    }

    public BigDecimal getCostPer1kImpressions() {
        if (costPer1kImpressions == null) return BigDecimal.ZERO;
        return costPer1kImpressions;
    }

    public void setCostPer1kImpressions(BigDecimal costPer1kImpressions) {
        if (this.costPer1kImpressions.equals(BigDecimal.ZERO)) this.costPer1kImpressions = costPer1kImpressions;
    }

    public double getClickThroughRate() {
        return clickThroughRate;
    }

    public void setClickThroughRate(double clickThroughRate) {
        if (this.clickThroughRate == -1) this.clickThroughRate = clickThroughRate;
    }

    public double getBounceRate() {
        return bounceRate;
    }

    public void setBounceRate(double bounceRate) {
        if (this.bounceRate == -1) this.bounceRate = bounceRate;
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
