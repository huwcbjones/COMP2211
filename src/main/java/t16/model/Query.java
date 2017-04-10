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
    private AGE age = AGE.ALL;
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

    public Query(TYPE type, RANGE range, Timestamp from, Timestamp to, GENDER gender, AGE age, INCOME income, CONTEXT context) {
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
            case BOUNCES_PAGES:
                return bouncesQueryPages();
            case BOUNCES_TIME:
                return bouncesQueryTime();
            case CONVERSIONS:
                return conversionsQuery();
            case CLICK_THROUGH_RATE:
                return clickThroughQuery();
            case TOTAL_COST:
                return totalCostQuery();
            case COST_PER_ACQUISITION:
                return costPerAcquisitionQuery();
            case COST_PER_THOUSAND_IMPRESSIONS:
                return costPer1kImpressionsQuery();
            case COST_PER_CLICK:
            case BOUNCE_RATE_PAGES:
            case BOUNCE_RATE_TIME:
                throw new UnsupportedOperationException();
            case BOUNCES:
            case BOUNCE_RATE:
                throw new IllegalStateException("Chart type not permitted.");
            default:
                throw new IllegalStateException("Should not happen");
        }
    }

    protected String impressionsQuery() {
        String whereClause = getWhereClause();
        if (whereClause.length() != 0) whereClause = " WHERE " + whereClause;
        return
                "SELECT " + getDateString("Impressions") + ", COUNT(*) AS impressions" +
                        " FROM `Impressions` " +
                        whereClause +
                        " GROUP BY " + getRangeString() +
                        " ORDER BY " + getRangeString() + " ASC";
    }

    protected String clicksQuery() {
        String whereClause = getWhereClause("Impressions");
        if (whereClause.length() != 0) whereClause = " WHERE " + whereClause;
        return
                "SELECT " + getDateString("Clicks") + ", COUNT(*) AS clicks" +
                        " FROM `Clicks` " +
                        " LEFT JOIN `Impressions` ON `Impressions`.`ID`=`Clicks`.`ID`" +
                        whereClause +
                        " GROUP BY " + getRangeString("Clicks") +
                        " ORDER BY " + getRangeString("Clicks") + " ASC";
    }

    protected String clickThroughQuery() {
        String rangeString = getRangeString();
        String whereClause = getWhereClause();
        if (whereClause.length() != 0) whereClause = " WHERE " + whereClause;
        String q =
                "SELECT " + getDateString("i_r") + ", CAST(clicks AS FLOAT)/CAST(impressions AS FLOAT) AS clickThrough FROM" +
                        "  (SELECT " + rangeString + ", COUNT(*) AS `impressions` FROM `Impressions` " + whereClause + " GROUP BY " + rangeString + ") i_r" +
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
        String whereClause = getWhereClause("Impressions");
        if (whereClause.length() != 0) whereClause = " WHERE " + whereClause;
        return
                "SELECT " + getDateString("Server") + ", COUNT(*) AS numberOfUniques" +
                        " FROM `Server` " +
                        " LEFT JOIN `Impressions` ON `Impressions`.`ID`=`Server`.`ID`" +
                        whereClause +
                        " GROUP BY " + getRangeString("Server") +
                        " ORDER BY " + getRangeString("Server") + " ASC";
    }

    /**
     * Here a bounce is when one page is viewed
     */
    protected String bouncesQueryPages() {
        String whereClause = getWhereClause("Impressions");
        if (whereClause.length() != 0) whereClause = " AND " + whereClause;
        return
                "SELECT " + getDateString("Server") + ", COUNT(*) AS bounces" +
                        " FROM `Server` " +
                        " LEFT JOIN `Impressions` ON `Impressions`.`ID`=`Server`.`ID`" +
                        " WHERE `page_viewed`=1 " + whereClause +
                        " GROUP BY " + getRangeString("Server") +
                        " ORDER BY " + getRangeString("Server") + " ASC";
    }

    /**
     * Here a bounce is when less than 60 seconds are spent on the site
     */
    protected String bouncesQueryTime() {
        String whereClause = getWhereClause("Server");
        if (whereClause.length() != 0) whereClause = " AND " + whereClause;
        return
                "SELECT " + getDateString("Server") + ", COUNT(*) AS bounces" +
                        " FROM `Server` " +
                        " LEFT JOIN `Impressions` ON `Impressions`.`ID`=`Server`.`ID`" +
                        " WHERE TIMESTAMPDIFF(SECOND,`Server`.`date`,`exit_date`) < 60 " + whereClause +
                        " GROUP BY " + getRangeString("Server") +
                        " ORDER BY " + getRangeString("Server") + " ASC";
    }

    protected String conversionsQuery() {
        String whereClause = getWhereClause("Impressions");
        if (whereClause.length() != 0) whereClause = " AND " + whereClause;
        return
                "SELECT " + getDateString("Server") + ", COUNT(*) AS conversions" +
                        " FROM `Server` " +
                        " LEFT JOIN `Impressions` ON `Impressions`.`ID`=`Server`.`ID`" +
                        " WHERE `conversion`=1 " + whereClause +
                        " GROUP BY " + getRangeString("Server") +
                        " ORDER BY " + getRangeString("Server") + " ASC";
    }

    protected String totalCostQuery() {
        //TODO: This needs to be refactored so filters can be applied to total cost blehhhh
        String whereClause = getWhereClause("s");
        if (whereClause.length() != 0) whereClause = " WHERE " + whereClause;
        return "SELECT " + getDateString("TotalCost") + ", SUM(cost)/100 AS cost FROM TotalCost GROUP BY " + getRangeString();
    }

    protected String costPerAcquisitionQuery() {
        String whereClause = getWhereClause("s");
        if (whereClause.length() != 0) whereClause = " AND " + whereClause;
        String q = "SELECT " + getDateString("s") + ", (SUM(cost) / 100) / COUNT(*) AS costPerAcquisition\n" +
                " FROM `Server` s\n" +
                " JOIN `TotalCost` c\n" +
                " ON s.YEAR = c.YEAR AND s.MONTH = c.MONTH ";
        if (range != RANGE.MONTH) {
            q += "AND s.DAY = c.DAY\n";
            if (range != RANGE.DAY) {
                q += "AND s.HOUR = c.HOUR\n";
            }
        }
        q += "WHERE s.`Conversion` = 1 " + whereClause +
                " GROUP BY " + getRangeString("s");
        return q;
    }

    protected String costPerClick() {
        String q = "SELECT " + getDateString("s") + ", (SUM(cost) / 100) / COUNT(*) AS costPerClick\n" +
                " FROM `Click` s\n" +
                " JOIN `TotalCost` c\n" +
                " ON s.YEAR = c.YEAR AND s.MONTH = c.MONTH ";
        if (range != RANGE.MONTH) {
            q += "AND s.DAY = c.DAY\n";
            if (range != RANGE.DAY) {
                q += "AND s.HOUR = c.HOUR\n";
            }
        }
        q += " GROUP BY " + getRangeString("s");
        return q;
    }

    protected String costPer1kImpressionsQuery() {
        String rangeString = getRangeString();

        String impressionsWhereClause = getWhereClause();
        String clickWhereClause = getWhereClause("Clicks");

        if (impressionsWhereClause.length() != 0) impressionsWhereClause = " WHERE " + impressionsWhereClause;
        if (clickWhereClause.length() != 0) clickWhereClause = " WHERE " + clickWhereClause;

        String q =
                "SELECT\n" +
                        "  " + getDateString("i") + ", totalCost / NULLIF(CAST(COUNT(*) / 1000 as FLOAT), 0) AS costPer1kImpressions\n" +
                        "FROM\n" +
                        " `Impressions` i\n" +
                        " JOIN\n" +
                        "  (SELECT " + getRangeString("i_r") + ", (clicks + impressions) / 100 AS totalCost\n" +
                        "   FROM\n" +
                        "     (SELECT " + rangeString + ", SUM(cost) AS `impressions` FROM `Impressions` " + impressionsWhereClause + " GROUP BY " + rangeString + ") i_r\n" +
                        "     JOIN\n" +
                        "     (SELECT " + getRangeString("Clicks") + ", SUM(click_cost) AS `clicks` FROM `Clicks` LEFT JOIN `Impressions` ON `Impressions`.ID = `Clicks`.ID " + clickWhereClause + " GROUP BY " + getRangeString("Clicks") + ") c_r\n" +
                        "      ON i_r.YEAR = c_r.YEAR AND i_r.MONTH = c_r.MONTH ";
        if (range != RANGE.MONTH) {
            q += "AND i_r.DAY = c_r.DAY\n";
            if (range != RANGE.DAY) {
                q += "AND i_r.HOUR = c_r.HOUR\n";
            }
        }
        q +=
                "  ) c\n" +
                "  ON i.YEAR = i.YEAR AND i.MONTH = c.MONTH";
        if (range != RANGE.MONTH) {
            q += " AND i.DAY = c.DAY\n";
            if (range != RANGE.DAY) {
                q += " AND i.HOUR = c.HOUR\n";
            }
        }
        q += " GROUP BY" +
                " " + getRangeString("i");
        return q;
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
        if (age != null && age != AGE.ALL) {
            clauses.add("`age` = '" + getAgeString(age) + "'");
        }
        if (income != null && income != INCOME.ALL) {
            clauses.add("`income` = '" + income.toString() + "'");
        }
        if (context != null && context != CONTEXT.ALL) {
            clauses.add("`context` = '" + context.toString() + "'");
        }
        if (!clause.equals("")) {
            return clause + " AND " + String.join(" AND ", clauses);
        }
        return String.join(" AND ", clauses);
    }

    protected String getDateWhere(String t, String f) {
        if (from != null && to != null) {
            return t + f + " BETWEEN '" + from.toString() + "' AND '" + to.toString() + "'";
        } else if (from != null) {
            return t + f + " >= '" + from.toString() + "'";
        } else if (to != null) {
            return t + f + " <= '" + to.toString() + "'";
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

    /**
     * Converts AGE enum to the appropriate string for SQL queries
     *
     * @param age AGE enum
     * @return String to use in SQL queries
     */
    private String getAgeString(AGE age) {
        switch (age) {
            case ALL:
                return "";
            case LT_25:
                return "<25";
            case _25_TO_34:
                return "25-34";
            case _35_TO_44:
                return "35-44";
            case _45_TO_54:
                return "45-54";
            case GT_54:
                return ">54";
            default:
                throw new IllegalStateException();
        }
    }

    public void setType(TYPE type) {
        this.type = type;
    }

    public void setRange(RANGE range) {
        this.range = range;
    }

    public void setFrom(Timestamp from) {
        this.from = from;
    }

    public void setTo(Timestamp to) {
        this.to = to;
    }

    public void setGender(GENDER gender) {
        this.gender = gender;
    }

    public void setAge(AGE age) {
        this.age = age;
    }

    public void setIncome(INCOME income) {
        this.income = income;
    }

    public void setContext(CONTEXT context) {
        this.context = context;
    }

    /**
     * Returns whether the values of this query should be treated as INTs, otherwise, treat as floats
     *
     * @return True if the result's are INTs
     */
    public boolean isInt() {
        return type == TYPE.CLICKS
                || type == TYPE.IMPRESSIONS
                || type == TYPE.UNIQUES
                || type == TYPE.BOUNCES_PAGES
                || type == TYPE.BOUNCES_TIME
                || type == TYPE.CONVERSIONS;
    }

    /**
     * Returns true if a complicated filter is applied
     *
     * @return
     */
    public boolean isComplicated() {
        return gender != GENDER.ALL || income != INCOME.ALL || context != CONTEXT.ALL || age != AGE.ALL;
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
        BOUNCES_PAGES,
        BOUNCES_TIME,
        CONVERSIONS,
        CLICK_THROUGH_RATE,
        TOTAL_COST,
        COST_PER_ACQUISITION,
        COST_PER_CLICK,
        COST_PER_THOUSAND_IMPRESSIONS,
        BOUNCE_RATE,
        BOUNCE_RATE_PAGES,
        BOUNCE_RATE_TIME
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

    public enum AGE {
        ALL,
        LT_25,
        _25_TO_34,
        _35_TO_44,
        _45_TO_54,
        GT_54
    }
}
