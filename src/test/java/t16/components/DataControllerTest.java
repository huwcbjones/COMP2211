package t16.components;

import org.junit.BeforeClass;
import org.junit.Test;
import t16.AdDashboard;
import t16.controller.DataController;

import java.io.File;

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

}