package t16.components;

import org.junit.BeforeClass;
import org.junit.Test;
import t16.AdDashboard;
import t16.controller.DataController;

import java.io.File;
import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

/**
 * Database Test
 *
 * @author Scott Williams
 * @since 03/01/2017
 */
public class DataControllerTest {

    private static DataController dataController;

    @BeforeClass
    public static void setUp() throws Exception {
        AdDashboard.initialise();
        dataController = new DataController();
        dataController.openCampaign(new File(DataControllerTest.class.getClassLoader().getResource("test_database.h2.db").getFile()));
    }

    @Test
    public void getTotalImpressions() throws Exception {
        assertEquals(133,dataController.getTotalImpressions());
    }

    @Test
    public void getTotalClicks() throws Exception {
        assertEquals(100,dataController.getTotalClicks());
    }

    @Test
    public void getTotalUniques() throws Exception {
        assertEquals(100,dataController.getTotalUniques());
    }

    @Test
    public void getTotalBounces() throws Exception {
        assertEquals(35, dataController.getTotalBouncesPages());
    }

    @Test
    public void getTotalConversions() throws Exception {
        assertEquals(6,dataController.getTotalConversions());
    }

    @Test
    public void getTotalCost() throws Exception {
        assertEquals(BigDecimal.valueOf(4.64972979),dataController.getTotalCost());
    }

    @Test
    public void getCostPerClick() throws Exception {
        assertEquals(BigDecimal.valueOf(0.0464972979),dataController.getCostPerClick());
    }

    @Test
    public void getCostPerAcquisition() throws Exception {
        assertEquals(BigDecimal.valueOf(0.774954965),dataController.getCostPerAcquisition());
    }

    @Test
    public void getCostPer1kImpressions() throws Exception {
        assertEquals(null,dataController.getCostPer1kImpressions());
    }
}