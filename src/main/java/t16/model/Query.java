package t16.model;

import java.sql.Timestamp;

/**
 * {DESCRIPTION}
 *
 * @author Huw Jones
 * @since 15/03/2017
 */
public class Query {

    private TYPE type = null;
    private RANGE range = null;
    private Timestamp from = null;
    private Timestamp to = null;
    private GENDER gender = null;
    private String age = null;
    private INCOME income = null;
    private CONTEXT context = null;

    public Query(TYPE type, RANGE range) {
        this.type = type;
        this.range = range;
    }

    public Query(TYPE type, RANGE range, Timestamp from, Timestamp to) {
        this.type = type;
        this.range = range;
        this.from = from;
        this.to = to;
    }

    public Query(TYPE type, RANGE range, Timestamp from, Timestamp to, GENDER gender, String age, INCOME income, CONTEXT context) {
        this.type = type;
        this.range = range;
        this.from = from;
        this.to = to;
        this.gender = gender;
        this.age = age;
        this.income = income;
        this.context = context;
    }

    public String getQuery() {
        switch (type) {
            case IMPRESSIONS:
                return impressionsQuery();
            case CLICKS:
                return clicksQuery();
            case CLICK_THROUGH_RATE:
                return clickThroughQuery();
            case UNIQUES:
                return uniquesQuery();
            case BOUNCES:
                return bouncesQuery();
            case CONVERSIONS:
                return conversionsQuery();
            case COST:
            case COST_PER_ACQUISITION:
            case COST_PER_CLICK:
            case COST_PER_1KIMPRESSION:
            case BOUNCE_RATE:
                throw new UnsupportedOperationException();
            default:
                throw new IllegalStateException("Should not happen");
        }
    }

    protected String impressionsQuery() {
        return
                "SELECT " + getDateString() + ", COUNT(*) AS impressions" +
                        " FROM `Impressions` " +
                        " WHERE" + getWhereClause() +
                        " GROUP BY " + getRangeString() +
                        " ORDER BY " + getRangeString() + " ASC";
    }

    protected String clicksQuery() {
        return
                "SELECT " + getDateString() + ", COUNT(*) AS clicks" +
                        " FROM `Clicks` " +
                        " WHERE" + getWhereClause() +
                        " GROUP BY " + getRangeString() +
                        " ORDER BY " + getRangeString() + " ASC";
    }

    protected String clickThroughQuery() {
        String dateStringWhere = getDateString("i_r");
        String rangeString = getRangeString();
        String q =
                "SELECT " + dateStringWhere + ", CAST(clicks AS FLOAT)/CAST(impressions AS FLOAT) AS clickThrough FROM" +
                        "  (SELECT " + rangeString + ", COUNT(*) AS `impressions` FROM `Impressions` GROUP BY " + rangeString + ") i_r" +
                        "  LEFT JOIN" +
                        "  (SELECT " + rangeString + ", COUNT(*) AS `clicks` FROM `Clicks` GROUP BY " + rangeString + ") c_r" +
                        " ON i_r.YEAR = c_r.YEAR" +
                        "    AND i_r.MONTH = c_r.MONTH";
        if (range != RANGE.MONTH) {
            q += "    AND i_r.DAY = c_r.DAY";

            if (range != RANGE.DAY) {
                q += "    AND i_r.HOUR = c_r.HOUR";
            }
        }
        q += " WHERE " + getWhereClause("", dateStringWhere);
        return q;
    }

    protected String uniquesQuery() {
        return
                "SELECT " + getDateString() + ", COUNT(*) AS numberOfBounces" +
                        " FROM `Server` " +
                        " WHERE " + getWhereClause() +
                        " GROUP BY " + getRangeString() +
                        " ORDER BY " + getRangeString() + " ASC";
    }

    protected String bouncesQuery() {
        return
                "SELECT " + getDateString() + ", COUNT(*) AS bounces" +
                        " FROM `Server` " +
                        " WHERE `page_viewed`=1 AND " + getWhereClause() +
                        " GROUP BY " + getRangeString() +
                        " ORDER BY " + getRangeString() + " ASC";
    }

    protected String conversionsQuery() {
        return
                "SELECT " + getDateString() + ", COUNT(*) AS conversions" +
                        " FROM `Server` " +
                        " WHERE `conversion`=1 AND " + getWhereClause() +
                        " GROUP BY " + getRangeString() +
                        " ORDER BY " + getRangeString() + " ASC";
    }

    protected String getDateString(String table) {
        String t = (table.length() == 0) ? "" : "`" + table + "`.";
        String c = "";
        String f = "";
        switch (range) {
            case HOUR:
                c = ", ' ', " + t + "`HOUR`" + c;
                f = " HH24" + f;
            case DAY:
                c = ", '-', " + t + "`DAY`" + c;
                f = "-DD" + f;
            case MONTH:
                c = ", '-', " + t + "`MONTH`" + c;
                f = "-MM" + f;
                break;
            default:
                throw new IllegalArgumentException();
        }
        c = t + "`YEAR`" + c;
        f = "YYYY" + f;
        return "TO_TIMESTAMP(CONCAT(" + c + "), '" + f + "')";
    }

    protected String getDateString() {
        return getDateString("");
    }

    protected String getWhereClause() {
        return getWhereClause("", "");
    }

    protected String getWhereClause(String table, String field) {
        String t = (table.length() == 0) ? "" : "`" + table + "`";
        String f = (field.length() == 0) ? "`date`" : field;
        if (from != null && to != null) {
            return t + f + " BETWEEN '" + from.toString() + "' AND '" + to.toString() + "'";
        } else if (from != null) {
            return t + f + " <= '" + from.toString() + "'";
        } else if (to != null) {
            return t + f + " >= '" + to.toString() + "'";
        } else {
            return "1";
        }
    }

    private String getRangeString() {
        String r = "";
        switch (range) {
            case HOUR:
                r = ", `HOUR`" + r;
            case DAY:
                r = ", `DAY`" + r;
            case MONTH:
                r = ", `MONTH`" + r;
                break;
            default:
                throw new IllegalArgumentException();
        }
        return "`YEAR`" + r;
    }

    public void setType(TYPE type) {
        if (this.type == null) this.type = type;
    }

    public void setRange(RANGE range) {
        if (this.range == null) this.range = range;
    }

    public void setFrom(Timestamp from) {
        if (this.from == null) this.from = from;
    }

    public void setTo(Timestamp to) {
        if (this.to == null) this.to = to;
    }

    public void setGender(GENDER gender) {
        if (this.gender == null) this.gender = gender;
    }

    public void setAge(String age) {
        if (this.age == null) this.age = age;
    }

    public void setIncome(INCOME income) {
        if (this.income == null) this.income = income;
    }

    public void setContext(CONTEXT context) {
        if (this.context == null) this.context = context;
    }

    public boolean isInt() {
        if (type == TYPE.CLICK_THROUGH_RATE) return false;
        return true;
    }

    public enum RANGE {
        DAY,
        HOUR,
        MONTH
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

    public enum GENDER {
        ALL,
        MALE,
        FEMALE
    }

    public enum INCOME {
        ALL,
        LOW,
        MEDIUM,
        HIGH
    }

    public enum CONTEXT {
        ALL,
        NEWS,
        SHOPPING,
        SOCIAL_MEDIA,
        BLOG,
        HOBBIES,
        TRAVEL
    }
}
