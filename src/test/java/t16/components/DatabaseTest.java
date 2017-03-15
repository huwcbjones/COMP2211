package t16.components;

import org.junit.Before;
import org.junit.Test;
import t16.controller.DataController;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Database Test
 *
 * @author Scott Williams
 * @since 03/01/2017
 */
public class DatabaseTest {

    private DataController dataController;

    @Before
    public void setUp() throws Exception {
        dataController = new DataController();
    }

    @Test
    public void initialiseDatabase() throws Exception {
        dataController.createCampaign(new File("resources/test/test_data.zip"),new File("resources/test/test_database.h2"));
    }

    @Test
    public void loadCampaign() throws Exception {

    }

    @Test
    public void createCampaign() throws Exception {

    }

    @Test
    public void createCampaign1() throws Exception {

    }

    @Test
    public void getTotalImpressions() throws Exception {

    }

    @Test
    public void getTotalClicks() throws Exception {

    }

    @Test
    public void getTotalUniques() throws Exception {

    }

    @Test
    public void getTotalBounces() throws Exception {

    }

    @Test
    public void getImpressions() throws Exception {

    }

    @Test
    public void getClicks() throws Exception {

    }

    @Test
    public void getUniques() throws Exception {

    }

    @Test
    public void getBounces() throws Exception {

    }

    @Test
    public void getConversions() throws Exception {

    }

    @Test
    public void getClickCost() throws Exception {

    }

    @Test
    public void getGender() throws Exception {

    }

    @Test
    public void getIncome() throws Exception {

    }

    @Test
    public void geContext() throws Exception {

    }

    @Test
    public void getImpressionCost() throws Exception {

    }

    @Test
    public void getServer() throws Exception {

    }

}