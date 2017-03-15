package t16.model;

import java.sql.Timestamp;

/**
 * {DESCRIPTION}
 *
 * @author Huw Jones
 * @since 15/03/2017
 */
public class Query {

    private TYPE type;
    private RANGE range;
    private Timestamp from = null;
    private Timestamp to = null;

    public Query(TYPE type, RANGE range) {
        this(type, range, null, null);
    }

    public Query(TYPE type, RANGE range, Timestamp from, Timestamp to) {
        this.type = type;
        this.range = range;
        this.from = from;
        this.to = to;
    }

    public String getQuery() {
        switch (type) {
            case IMPRESSIONS:
                return impressionsQuery();
            case CLICKS:
                return clicksQuery();
            case UNIQUES:
            case BOUNCES:
            case CONVERSIONS:
            case COST:
            case COST_PER_ACQUISITION:
            case COST_PER_CLICK:
            case COST_PER_1KIMPRESSION:
            case CLICK_THROUGH_RATE:
                return clickThroughQuery();
            case BOUNCE_RATE:
                throw new UnsupportedOperationException();
            default:
                throw new IllegalStateException("Should not happen");
        }
    }

    protected String impressionsQuery() {
        return
                "SELECT " + getDateString() + ", COUNT(*) AS impressions" +
                        " FROM `Impressions` " + getWhereClause() +
                        " GROUP BY " + getRangeString() +
                        " ORDER BY " + getRangeString() + " ASC";
    }

    protected String clicksQuery() {
        return
                "SELECT " + getDateString() + ", COUNT(*) AS clicks" +
                        " FROM `Clicks` " + getWhereClause() +
                        " GROUP BY " + getRangeString() +
                        " ORDER BY " + getRangeString() + " ASC";
    }

    protected String clickThroughQuery() {
        String q =
                "SELECT " + getDateString("i_r") + ", CAST(clicks AS FLOAT)/CAST(impressions AS FLOAT) AS clickThrough FROM" +
                        "  (SELECT " + getRangeString() + ", COUNT(*) AS `impressions` FROM `Impressions` GROUP BY " + getRangeString() + ") i_r" +
                        "  LEFT JOIN" +
                        "  (SELECT " + getRangeString() + ", COUNT(*) AS `clicks` FROM `Clicks` GROUP BY " + getRangeString() + ") c_r" +
                        " ON i_r.YEAR = c_r.YEAR" +
                        "    AND i_r.MONTH = c_r.MONTH";
        if (range == RANGE.MONTHLY) return q;
        q += "    AND i_r.DAY = c_r.DAY";

        if (range == RANGE.DAILY) return q;
        q += "    AND i_r.HOUR = c_r.HOUR";

        return q;
    }

    protected String getDateString(String table) {
        String t = (table.length() == 0) ? "" : "`" + table + "`.";
        String c = "";
        String f = "";
        switch (range) {
            case HOURLY:
                c = ", ' ', " + t + "`HOUR`" + c;
                f = " HH24" + f;
            case DAILY:
                c = ", '-', " + t + "`DAY`" + c;
                f = "-DD" + f;
            case MONTHLY:
                c = ", '-', " + t + "`MONTH`" + c;
                f = "-MM" + f;
                break;
            default:
                throw new IllegalArgumentException();
        }
        c = t + "`YEAR`" + c;
        f = "YYYY" + f;
        return "TO_TIMESTAMP(CONCAT(" + c + "), '" + f + "') as `date`";
    }

    protected String getDateString() {
        return getDateString("");
    }

    protected String getWhereClause() {
        if (from != null && to != null) {
            return "WHERE `date` BETWEEN '" + from.toString() + "' AND '" + to.toString() + "'";
        } else if (from != null) {
            return "`date` < " + from.toString();
        } else if (to != null) {
            return "`date` > " + to.toString();
        } else {
            return "";
        }
    }

    private String getRangeString() {
        String r = "";
        switch (range) {
            case HOURLY:
                r = ", `HOUR`" + r;
            case DAILY:
                r = ", `DAY`" + r;
            case MONTHLY:
                r = ", `MONTH`" + r;
                break;
            default:
                throw new IllegalArgumentException();
        }
        return "`YEAR`" + r;
    }

    public enum RANGE {
        DAILY,
        HOURLY,
        MONTHLY
    }

    public enum TYPE {
        IMPRESSIONS,
        CLICKS,
        UNIQUES,
        BOUNCES,
        CONVERSIONS,
        COST,
        COST_PER_ACQUISITION,
        COST_PER_CLICK,
        COST_PER_1KIMPRESSION,
        CLICK_THROUGH_RATE,
        BOUNCE_RATE
    }

    public boolean isInt(){
        if(type == TYPE.CLICK_THROUGH_RATE) return false;
        return true;
    }
}
