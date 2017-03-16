package t16.model;

import java.sql.Timestamp;
import java.util.ArrayList;

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
    private GENDER gender = GENDER.ALL;
    private String age = null;
    private INCOME income = INCOME.ALL;
    private CONTEXT context = CONTEXT.ALL;

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
        this.gender = (gender == null) ? GENDER.ALL : gender;
        this.age = age;
        this.income = (income == null) ? INCOME.ALL : income;
        this.context = (context == null) ? CONTEXT.ALL : context;
    }

    public String getQuery() {
        switch (type) {
            case CLICKS:
                return clicksQuery();
            case IMPRESSIONS:
                return impressionsQuery();
            case UNIQUES:
                return uniquesQuery();
            case BOUNCES:
                return bouncesQuery();
            case CONVERSIONS:
                return conversionsQuery();
            case CLICK_THROUGH_RATE:
                return clickThroughQuery();
            case TOTAL_COST:
            case COST_PER_ACQUISITION:
            case COST_PER_THOUSAND_IMPRESSIONS:
            case COST_PER_CLICK:
            case BOUNCE_RATE:
                throw new UnsupportedOperationException();
            default:
                throw new IllegalStateException("Should not happen");
        }
    }

    protected String impressionsQuery() {
        if (!isComplicated()) {
            String whereClause = getWhereClause();
            if (whereClause.length() != 0) whereClause = " WHERE " + whereClause;

            return
                    "SELECT " + getDateString("Impressions") + ", COUNT(*) AS impressions" +
                            " FROM `Impressions` " +
                            whereClause +
                            " GROUP BY " + getRangeString() +
                            " ORDER BY " + getRangeString() + " ASC";
        }
        return
                "SELECT " + getDateString("Impressions") + ", COUNT(*) AS impressions" +
                        " FROM `Impressions` " +
                        " WHERE " + getWhereClause() +
                        " GROUP BY " + getRangeString() +
                        " ORDER BY " + getRangeString() + " ASC";
    }

    protected String clicksQuery() {
        if (!isComplicated()) {
            String whereClause = getWhereClause();
            if (whereClause.length() != 0) whereClause = " WHERE " + whereClause;
            return
                    "SELECT " + getDateString("Clicks") + ", COUNT(*) AS clicks" +
                            " FROM `Clicks` " +
                            whereClause +
                            " GROUP BY " + getRangeString() +
                            " ORDER BY " + getRangeString() + " ASC";
        }
        return
                "SELECT " + getDateString("Clicks") + ", COUNT(*) AS clicks" +
                        " FROM `Clicks` " +
                        " LEFT JOIN `Impressions` ON `Impressions`.`ID`=`Clicks`.`ID`" +
                        " WHERE " + getWhereClause("Clicks") +
                        " GROUP BY " + getRangeString("Clicks") +
                        " ORDER BY " + getRangeString("Clicks") + " ASC";
    }

    protected String clickThroughQuery() {
        if (!isComplicated()) {
            String rangeString = getRangeString();
            String whereClause = getWhereClause();
            if (whereClause.length() != 0) whereClause = " WHERE " + whereClause;
            String q =
                    "SELECT " + getDateString("i_r") + ", CAST(clicks AS FLOAT)/CAST(impressions AS FLOAT) AS clickThrough FROM" +
                            "  (SELECT " + rangeString + ", COUNT(*) AS `impressions` FROM `Impressions`" + whereClause + " GROUP BY " + rangeString + ") i_r" +
                            "  LEFT JOIN" +
                            "  (SELECT " + rangeString + ", COUNT(*) AS `clicks` FROM `Clicks` GROUP BY " + rangeString + ") c_r" +
                            " ON i_r.YEAR = c_r.YEAR" +
                            "    AND i_r.MONTH = c_r.MONTH";
            if (range != RANGE.MONTH) {
                q += " AND i_r.DAY = c_r.DAY";
                if (range != RANGE.DAY) {
                    q += " AND i_r.HOUR = c_r.HOUR";
                }
            }
            return q;
        }

        String rangeString = getRangeString();
        String whereClause = getWhereClause();
        String q =
                "SELECT " + getDateString("i_r") + ", CAST(clicks AS FLOAT)/CAST(impressions AS FLOAT) AS clickThrough FROM" +
                        "  (SELECT " + rangeString + ", COUNT(*) AS `impressions` FROM `Impressions` WHERE " + whereClause + " GROUP BY " + rangeString + ") i_r" +
                        "  LEFT JOIN" +
                        "  (SELECT " + rangeString + ", COUNT(*) AS `clicks` FROM `Clicks` GROUP BY " + rangeString + ") c_r" +
                        " ON i_r.YEAR = c_r.YEAR" +
                        " AND i_r.MONTH = c_r.MONTH";
        if (range != RANGE.MONTH) {
            q += " AND i_r.DAY = c_r.DAY";
            if (range != RANGE.DAY) {
                q += " AND i_r.HOUR = c_r.HOUR";
            }
        }

        return q;
    }

    protected String uniquesQuery() {
        if (!isComplicated()) {
            String whereClause = getWhereClause();
            if (whereClause.length() != 0) whereClause = " WHERE " + whereClause;
            return
                    "SELECT " + getDateString("Server") + ", COUNT(*) AS numberOfUniques" +
                            " FROM `Server` " +
                            whereClause +
                            " GROUP BY " + getRangeString() +
                            " ORDER BY " + getRangeString() + " ASC";
        }
        return
                "SELECT " + getDateString("Server") + ", COUNT(*) AS numberOfUniques" +
                        " FROM `Server` " +
                        " LEFT JOIN `Impressions` ON `Impressions`.`ID`=`Server`.`ID`" +
                        " WHERE " + getWhereClause("Server") +
                        " GROUP BY " + getRangeString("Server") +
                        " ORDER BY " + getRangeString("Server") + " ASC";
    }

    protected String bouncesQuery() {
        if (!isComplicated()) {
            String whereClause = getWhereClause();
            if (whereClause.length() != 0) whereClause = " AND " + whereClause;
            return
                    "SELECT " + getDateString() + ", COUNT(*) AS bounces" +
                            " FROM `Server` " +
                            " WHERE `page_viewed`=1 " + whereClause +
                            " GROUP BY " + getRangeString() +
                            " ORDER BY " + getRangeString() + " ASC";
        }
        return
                "SELECT " + getDateString("Server") + ", COUNT(*) AS bounces" +
                        " FROM `Server` " +
                        " LEFT JOIN `Impressions` ON `Impressions`.`ID`=`Server`.`ID`" +
                        " WHERE `page_viewed`=1 AND " + getWhereClause("Server") +
                        " GROUP BY " + getRangeString("Server") +
                        " ORDER BY " + getRangeString("Server") + " ASC";
    }

    protected String conversionsQuery() {
        if (!isComplicated()) {
            String whereClause = getWhereClause();
            if (whereClause.length() != 0) whereClause = " AND " + whereClause;
            return
                    "SELECT " + getDateString() + ", COUNT(*) AS conversions" +
                            " FROM `Server` " +
                            " WHERE `conversion`=1 " + whereClause +
                            " GROUP BY " + getRangeString() +
                            " ORDER BY " + getRangeString() + " ASC";
        }
        return
                "SELECT " + getDateString("Server") + ", COUNT(*) AS conversions" +
                        " FROM `Server` " +
                        " LEFT JOIN `Impressions` ON `Impressions`.`ID`=`Server`.`ID`" +
                        " WHERE `conversion`=1 AND " + getWhereClause("Server") +
                        " GROUP BY " + getRangeString("Server") +
                        " ORDER BY " + getRangeString("Server") + " ASC";
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

    protected String getWhereClause(String table) {
        return getWhereClause(table, "");
    }

    protected String getWhereClause(String table, String field) {
        // Set default and escape
        String t = (table.length() == 0) ? "" : table;
        if (table.length() != 0) {
            t = (t.contains("`")) ? t : "`" + table + "`.";
        }

        String f = (field.length() == 0) ? "date" : field;
        f = (!f.contains("`")) ? f : "`" + f + "`";

        if (!isComplicated()) {
            return getDateWhere(t, f);
        }

        String clause = getDateWhere(t, f);
        ArrayList<String> clauses = new ArrayList<>();
        if (gender != null && gender != GENDER.ALL) {
            clauses.add("`gender` = '" + gender.toString() + "'");
        }
        if (income != null && income != INCOME.ALL) {
            clauses.add("`income` = '" + income.toString() + "'");
        }
        if (context != null && context != CONTEXT.ALL) {
            clauses.add("`context` = '" + context.toString() + "'");
        }
        if(!clause.equals("")){
            return clause + " AND " + String.join(" AND ", clauses);
        }
        return String.join(" AND ", clauses);
    }

    protected String getDateWhere(String t, String f) {
        if (from != null && to != null) {
            return t + f + " BETWEEN '" + from.toString() + "' AND '" + to.toString() + "'";
        } else if (from != null) {
            return t + f + " <= '" + from.toString() + "'";
        } else if (to != null) {
            return t + f + " >= '" + to.toString() + "'";
        } else {
            return "";
        }
    }

    private String getRangeString() {
        return getRangeString("");
    }

    private String getRangeString(String table) {
        String t = (table.length() == 0) ? "" : table;
        if (table.length() != 0) {
            t = (t.contains("`")) ? t : "`" + table + "`.";
        }

        String r = "";
        switch (range) {
            case HOUR:
                r = ", " + t + "`HOUR`" + r;
            case DAY:
                r = ", " + t + "`DAY`" + r;
            case MONTH:
                r = ", " + t + "`MONTH`" + r;
                break;
            default:
                throw new IllegalArgumentException();
        }
        return t + "`YEAR`" + r;
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
        this.context = context;
    }

    public boolean isInt() {
        if (type == TYPE.CLICK_THROUGH_RATE) return false;
        return true;
    }

    public boolean isComplicated() {
        return gender != GENDER.ALL || income != INCOME.ALL || context != CONTEXT.ALL;
    }

    public enum RANGE {
        DAY,
        HOUR,
        MONTH
    }

    public enum TYPE {
        CLICKS,
        IMPRESSIONS,
        UNIQUES,
        BOUNCES,
        CONVERSIONS,
        CLICK_THROUGH_RATE,
        TOTAL_COST,
        COST_PER_ACQUISITION,
        COST_PER_CLICK,
        COST_PER_THOUSAND_IMPRESSIONS,
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
