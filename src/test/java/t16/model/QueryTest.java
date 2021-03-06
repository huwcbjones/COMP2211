package t16.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import t16.AdDashboard;
import t16.controller.DataController;

import java.io.File;
import java.sql.Timestamp;
import java.util.concurrent.atomic.AtomicLong;

/**
 * {DESCRIPTION}
 *
 * @author Huw Jones
 * @since 15/03/2017
 */
@Ignore
public class QueryTest {

    protected static final AtomicLong count = new AtomicLong();
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
        log.info("Tested {} query permutations!", count.get());
        d.shutdown();
    }

    @Test
    public void impressionsQuery() throws Exception {
        log.info("** Testing Impressions **");
        Query q = new Query(Query.TYPE.IMPRESSIONS, Query.RANGE.HOUR);

        genderTestQuery(q);
        log.info("** Test finished! **");
    }

    @Test
    public void clicksQuery() throws Exception {
        log.info("** Testing Clicks **");
        Query q = new Query(Query.TYPE.CLICKS, Query.RANGE.HOUR);

        genderTestQuery(q);

        log.info("** Test finished! **");
    }

    @Test
    public void clickThroughQuery() throws Exception {
        log.info("** Testing CTR **");
        Query q = new Query(Query.TYPE.CLICK_THROUGH_RATE, Query.RANGE.HOUR);

        genderTestQuery(q);

        log.info("** Test finished! **");
    }

    @Test
    public void uniquesQuery() throws Exception {
        log.info("** Testing Uniques **");
        Query q = new Query(Query.TYPE.UNIQUES, Query.RANGE.HOUR);

        genderTestQuery(q);

        log.info("** Test finished! **");
    }

    @Test
    public void bouncesQueryPages() throws Exception {
        log.info("** Testing Bounces - Pages Viewed **");
        Query q = new Query(Query.TYPE.BOUNCES_PAGES, Query.RANGE.HOUR);

        genderTestQuery(q);

        log.info("** Test finished! **");
    }

    @Test
    public void bouncesQueryTime() throws Exception {
        log.info("** Testing Bounces - Time Spent **");
        Query q = new Query(Query.TYPE.BOUNCES_TIME, Query.RANGE.HOUR);

        genderTestQuery(q);

        log.info("** Test finished! **");
    }

    @Test
    public void conversionsQuery() throws Exception {
        log.info("** Testing Conversions **");
        Query q = new Query(Query.TYPE.CONVERSIONS, Query.RANGE.HOUR);

        genderTestQuery(q);

        log.info("** Test finished! **");
    }

    @Test
    public void totalCostQuery() throws Exception {
        log.info("** Testing Total Cost **");
        Query q = new Query(Query.TYPE.TOTAL_COST, Query.RANGE.HOUR);

        genderTestQuery(q);

        log.info("** Test finished! **");
    }

    @Test
    public void costPerAcquisitionQuery() throws Exception {
        log.info("** Testing Cost per Acquisition **");
        Query q = new Query(Query.TYPE.COST_PER_ACQUISITION, Query.RANGE.HOUR);

        genderTestQuery(q);

        log.info("** Test finished! **");
    }

    @Test
    public void costPer1kImpressionsQuery() throws Exception {
        log.info("** Testing Cost per 1k Impressions **");
        Query q = new Query(Query.TYPE.COST_PER_THOUSAND_IMPRESSIONS, Query.RANGE.HOUR);

        genderTestQuery(q);

        log.info("** Test finished! **");
    }

    @Test
    public void bounceRateQueryPages() throws Exception {
        log.info("** Testing Bounce Rate - Pages Viewed **");
        Query q = new Query(Query.TYPE.BOUNCE_RATE_PAGES, Query.RANGE.HOUR);

        genderTestQuery(q);

        log.info("** Test finished! **");
    }

    @Test
    public void bounceRateQueryTime() throws Exception {
        log.info("** Testing Bounce Rate - Time Spent **");
        Query q = new Query(Query.TYPE.BOUNCE_RATE_TIME, Query.RANGE.HOUR);

        genderTestQuery(q);

        log.info("** Test finished! **");
    }

    public void timePeriodTestQuery(Query q) throws Exception {
        for (Query.RANGE r : Query.RANGE.values()) {
            try {
                q.setRange(r);
                testFromToQuery(q);
            } catch (Exception e) {
                log.error("Failed Grouping: {}", r.toString());
                throw e;
            }
        }
    }

    public void testFromToQuery(Query q) throws Exception {
        try {
            q.setFrom(null);
            q.setTo(null);
            testQuery(q);
        } catch (Exception e) {
            log.error("Failed Range: NO RANGE");
            throw e;
        }

        try {
            q.setFrom(from);
            q.setTo(null);
            testQuery(q);
        } catch (Exception e) {
            log.error("Failed Range: FROM");
            throw e;
        }

        try {
            q.setFrom(null);
            q.setTo(to);
            testQuery(q);
        } catch (Exception e) {
            log.error("Failed Range: TO");
            throw e;
        }
        try {
            q.setFrom(from);
            q.setTo(to);
            testQuery(q);
        } catch (Exception e) {
            log.error("Failed Range: BETWEEN");
            throw e;
        }
    }

    public void testQuery(Query q) throws Exception {
        try {
            d.getQuery(q);
            count.incrementAndGet();
        } catch (Exception e) {
            log.info("Query: \n{}\n", q.getQuery());
            throw e;
        }
    }

    public void genderTestQuery(Query q) throws Exception {
        for (Query.GENDER c : Query.GENDER.values()) {
            try {
                q.setGender(c);
                ageTestQuery(q);
            } catch (Exception e) {
                log.error("Failed GENDER {}", c.toString());
                throw e;
            }
        }
    }

    public void ageTestQuery(Query q) throws Exception {
        for (Query.AGE c : Query.AGE.values()) {
            try {
                q.setAge(c);
                incomeTestQuery(q);
            } catch (Exception e) {
                log.error("Failed AGE {}", c.toString());
                throw e;
            }
        }
    }

    public void incomeTestQuery(Query q) throws Exception {
        for (Query.INCOME c : Query.INCOME.values()) {
            try {
                q.setIncome(c);
                contextTestQuery(q);
            } catch (Exception e) {
                log.error("Failed INCOME {}", c.toString());
                throw e;
            }
        }
    }

    public void contextTestQuery(Query q) throws Exception {
        for (Query.CONTEXT c : Query.CONTEXT.values()) {
            try {
                q.setContext(c);
                timePeriodTestQuery(q);
            } catch (Exception e) {
                log.error("Failed CONTEXT {}", c.toString());
                throw e;
            }
        }
    }
}
