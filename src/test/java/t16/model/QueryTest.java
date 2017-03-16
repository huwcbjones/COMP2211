package t16.model;

import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import t16.AdDashboard;
import t16.controller.DataController;

import java.io.File;
import java.sql.Timestamp;
import java.util.List;

/**
 * {DESCRIPTION}
 *
 * @author Huw Jones
 * @since 15/03/2017
 */
public class QueryTest {

    protected static Timestamp from = Timestamp.valueOf("2015-01-05 00:00:00");
    protected static Timestamp to = Timestamp.valueOf("2015-01-15 00:00:00");

    protected static Logger log = LogManager.getLogger(QueryTest.class);
    static DataController d;

    @BeforeClass
    public static void setUp() throws Exception {
        AdDashboard.initialise();
        d = new DataController();
        d.openCampaign(new File(QueryTest.class.getClassLoader().getResource("test_database.h2.db").getFile()));
    }

    @AfterClass
    public static void tearDown() throws Exception {
        d.shutdown();
    }

    @Test
    public void impressionsQuery() throws Exception {
        log.info("** Testing Impressions **");
        Query q = new Query(Query.TYPE.IMPRESSIONS, Query.RANGE.HOUR);

        genderTestQuery(q);
        incomeTestQuery(q);
        contextTestQuery(q);
        log.info("** Test finished! **");
    }

    @Test
    public void clicksQuery() throws Exception {
        log.info("** Testing Clicks **");
        Query q = new Query(Query.TYPE.CLICKS, Query.RANGE.HOUR);

        genderTestQuery(q);
        incomeTestQuery(q);
        contextTestQuery(q);

        log.info("** Test finished! **");
    }

    @Test
    public void clickThroughQuery() throws Exception {
        log.info("** Testing CTR **");
        Query q = new Query(Query.TYPE.CLICK_THROUGH_RATE, Query.RANGE.HOUR);

        genderTestQuery(q);
        incomeTestQuery(q);
        contextTestQuery(q);

        log.info("** Test finished! **");
    }

    @Test
    public void uniquesQuery() throws Exception {
        log.info("** Testing Uniques **");
        Query q = new Query(Query.TYPE.UNIQUES, Query.RANGE.HOUR);

        genderTestQuery(q);
        incomeTestQuery(q);
        contextTestQuery(q);

        log.info("** Test finished! **");
    }

    @Test
    public void bouncesQuery() throws Exception {
        log.info("** Testing Bounces **");
        Query q = new Query(Query.TYPE.BOUNCES, Query.RANGE.HOUR);

        genderTestQuery(q);
        incomeTestQuery(q);
        contextTestQuery(q);

        log.info("** Test finished! **");
    }

    @Test
    public void conversionsQuery() throws Exception {
        log.info("** Testing Conversions **");
        Query q = new Query(Query.TYPE.CONVERSIONS, Query.RANGE.HOUR);

        genderTestQuery(q);
        incomeTestQuery(q);
        contextTestQuery(q);

        log.info("** Test finished! **");
    }

    @Test
    public void totalCostQuery() throws Exception {
        log.info("** Testing Total Cost **");
        Query q = new Query(Query.TYPE.TOTAL_COST, Query.RANGE.HOUR);

        genderTestQuery(q);
        incomeTestQuery(q);
        contextTestQuery(q);

        log.info("** Test finished! **");
    }

    public void timePeriodTestQuery(Query q) throws Exception {
        for(Query.RANGE r: Query.RANGE.values()){
            log.info("*  Testing time range {}", r.toString());
            q.setRange(r);

            testFromToQuery(q);
        }
    }

    public void testFromToQuery(Query q) throws Exception {
        log.info("*  no range");
        q.setFrom(null);
        q.setTo(null);
        testQuery(q);

        log.info("*  from ->");
        q.setFrom(from);
        q.setTo(null);
        testQuery(q);

        log.info("*  <- to");
        q.setFrom(null);
        q.setTo(to);
        testQuery(q);

        log.info("*  from <-> to");
        q.setFrom(from);
        q.setTo(to);
        testQuery(q);
    }

    public void testQuery(Query q) throws Exception {
        log.info("Query: {}", q.getQuery());

        List<Pair<String, Number>> l = d.getQuery(q);
        log.info("Returned {} rows", l.size());
    }

    public void genderTestQuery(Query q) throws Exception {
        for (Query.GENDER c : Query.GENDER.values()) {
            log.info("*  Testing gender {}", c.toString());
            q.setGender(c);
            timePeriodTestQuery(q);
        }
    }

    public void incomeTestQuery(Query q) throws Exception {
        for (Query.INCOME c : Query.INCOME.values()) {
            log.info("*  Testing income {}", c.toString());
            q.setIncome(c);
            timePeriodTestQuery(q);
        }
    }

    public void contextTestQuery(Query q) throws Exception {
        for (Query.CONTEXT c : Query.CONTEXT.values()) {
            log.info("*  Testing context {}", c.toString());
            q.setContext(c);
            timePeriodTestQuery(q);
        }
    }
}