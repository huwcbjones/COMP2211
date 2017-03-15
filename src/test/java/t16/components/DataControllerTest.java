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
public class DataControllerTest {

    private DataController dataController;

    @Before
    public void setUp() throws Exception {
        dataController = new DataController();
    }

    @Test
    public void initialiseDatabase() throws Exception {
        dataController.createCampaign(new File("resources/test/test_data.zip"),new File("resources/test/test_data.h2"));
    }

//    @Test
//    public void getTotalImpressions() throws Exception {
//        assertEquals(0,dataController.getTotalImpressions());
//    }

}