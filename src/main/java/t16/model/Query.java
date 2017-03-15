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
            case BOUNCE_RATE:
            throw new UnsupportedOperationException();
            default:
                throw new IllegalStateException("Should not happen");
        }
    }

    private String impressionsQuery() {
        return
                "SELECT " + getDateString() + ", COUNT(*) AS impressions" +
                " FROM `Impressions` " + getWhereClause() +
                " GROUP BY " + getRangeString() +
                " ORDER BY " + getRangeString() + " ASC";
    }

    private String clicksQuery() {
        return
                "SELECT " + getDateString() + ", COUNT(*) AS clicks" +
                " FROM `Clicks` " + getWhereClause() +
                " GROUP BY " + getRangeString() +
                " ORDER BY " + getRangeString() + " ASC";
    }

    private String getDateString() {
        String c = "";
        String f = "";
        switch (range) {
            case HOURLY:
                c = ", ' ', `HOUR`" + c;
                f = " HH24" + f;
            case DAILY:
                c = ", '-', `DAY`" + c;
                f = "-DD" + f;
            case MONTHLY:
                c = ", '-', `MONTH`" + c;
                f = "-MM" + f;
                break;
            default:
                throw new IllegalArgumentException();
        }
        c = "`YEAR`" + c;
        f = "YYYY" + f;
        return "TO_TIMESTAMP(CONCAT(" + c + "), '" + f + "') as `date`";
    }

    private String getWhereClause() {
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


}
