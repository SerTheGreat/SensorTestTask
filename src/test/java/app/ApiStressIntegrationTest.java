package app;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * The main purpose of this test set is to verify the fact that API works with large data
 * and to evaluate the execution times
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:application.yaml")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ApiStressIntegrationTest implements ApiIntegrationTest {

    //The name of the file with large json measurements data
    private static final String DATAFILE = "data.json";

    @Autowired
    MeasurementService measurementService;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    @Order(0) //Other tests rely on the data saved by this one so it should always run first
    public void testMeasurementsSave() throws Exception {
        long time = System.currentTimeMillis();
        try (InputStream inputStream = new BufferedInputStream(new FileInputStream(DATAFILE))) {
            measurementService.parseAndSave(inputStream);
        }
        time -=System.currentTimeMillis();
        Assert.assertTrue("Too long execution", time / 60000 < 5);
    }

    @Test
    @Override
    public void testApiHistory() throws Exception {
        long time = System.currentTimeMillis();
        mockMvc.perform(get(ApiIntegrationTest.HISTORY_URL)
                .param("id", "1")
                .param("from","0")
                .param("to", "" + System.currentTimeMillis()))
                .andExpect(status().isOk());
        time -=System.currentTimeMillis();
        Assert.assertTrue("Too long execution", time / 1000 < 1);
    }

    @Test
    @Override
    public void testApiLatest() throws Exception {
        long time = System.currentTimeMillis();
        mockMvc.perform(get(ApiIntegrationTest.LATEST_URL).param("id", "2"))
                .andExpect(status().isOk())
                .andDo(result -> System.out.println(ApiIntegrationTest.LATEST_URL +
                        ": " +result.getResponse().getContentAsString()))
                .andExpect(result ->
                        Assert.assertTrue(result.getResponse().getContentAsString().length() > 0));
        time -=System.currentTimeMillis();
        Assert.assertTrue("Too long execution", time / 1000 < 1);
    }

    @Test
    @Override
    public void testApiAverages() throws Exception {
        long time = System.currentTimeMillis();
        mockMvc.perform(get(ApiIntegrationTest.AVERAGES_URL))
                .andExpect(status().isOk())
                .andDo(result -> System.out.println(ApiIntegrationTest.AVERAGES_URL +
                        ": " +result.getResponse().getContentAsString()))
                .andExpect(result ->
                        Assert.assertTrue(result.getResponse().getContentAsString().length() > 0));
        time -=System.currentTimeMillis();
        Assert.assertTrue("Too long execution", time / 1000 < 1);
    }

    @Override
    public void testApiSave() throws Exception {

    }

}
