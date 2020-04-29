package app;

import dao.SensorDAO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestExecutionListener;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@TestExecutionListeners(value = {
        QuickIntegrationTestDBInitializingListener.class,
        DependencyInjectionTestExecutionListener.class
})
public class ApiQuickIntegrationTest implements ApiIntegrationTest, TestExecutionListener {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private SensorDAO sensorDAO;

    private MockMvc mockMvc;

    private static final String TEST_JSON_CONTENT = "["
            + " {\"objectId\": 1, \"sensorId\": 1, \"time\": 1000, \"value\": 1.2}"
            + ",{\"objectId\": 1, \"sensorId\": 1, \"time\": 1001, \"value\": 1.3}"
            + ",{\"objectId\": 1, \"sensorId\": 2, \"time\": 1000, \"value\": 1.4}"
            + ",{\"objectId\": 1, \"sensorId\": 2, \"time\": 1001, \"value\": 1.5}"
            + ",{\"objectId\": 2, \"sensorId\": 3, \"time\": 1002, \"value\": 1.6}"
            + ",{\"objectId\": 2, \"sensorId\": 4, \"time\": 1003, \"value\": 1.7}"
            + "]";

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    @Override
    public void testApiSave() throws Exception {
        //Here we'll test not only the fact that save API works but also that it changes the latest values properly
        Map<Integer, Double> latestBefore = sensorDAO.getLatestValuesByObjectId(2);
        mockMvc.perform(post(ApiIntegrationTest.SAVE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content("[{\"objectId\": 2, \"sensorId\": 3, \"time\": 1001, \"value\": 2.0}"
                        + ",{\"objectId\": 2, \"sensorId\": 4, \"time\": 1005, \"value\": 1.1}]"))
                .andExpect(status().isOk());
        Map<Integer, Double> latestAfter = sensorDAO.getLatestValuesByObjectId(2);
        //The latest value of the sensor 3 shouldn't  change:
        Assert.assertEquals(latestBefore.get(3), latestAfter.get(3), 0.0001);
        //The latest value of the sensor 4 should have been updated:
        Assert.assertEquals(1.1, latestAfter.get(4), 0.0001);
    }

    @Test
    @Override
    public void testApiHistory() throws Exception {
        final String expectedResult =
                "[{\"sensorId\":1,\"time\":1000,\"value\":1.2},{\"sensorId\":1,\"time\":1001,\"value\":1.3}]";
        mockMvc.perform(get(ApiIntegrationTest.HISTORY_URL)
                .param("id", "1")
                .param("from","1000")
                .param("to", "1002"))
                .andExpect(status().isOk())
                .andDo(result -> System.out.println(ApiIntegrationTest.HISTORY_URL +
                        ": " + result.getResponse().getContentAsString()))
                .andExpect(content().json(expectedResult));
    }

    @Test
    @Override
    public void testApiLatest() throws Exception {
        final String expectedResult = "{\"1\":1.3,\"2\":1.5}";
        mockMvc.perform(get(ApiIntegrationTest.LATEST_URL).param("id", "1"))
                .andExpect(status().isOk())
                .andDo(result -> System.out.println(ApiIntegrationTest.LATEST_URL +
                        ": " +result.getResponse().getContentAsString()))
                .andExpect(content().json(expectedResult));
    }

    @Test
    @Override
    public void testApiAverages() throws Exception {
        final String expectedResult = "{\"1\":1.4,\"2\":1.65}";
        mockMvc.perform(get(ApiIntegrationTest.AVERAGES_URL))
                .andExpect(status().isOk())
                .andDo(result -> System.out.println(ApiIntegrationTest.AVERAGES_URL +
                        ": " +result.getResponse().getContentAsString()))
                .andExpect(content().json(expectedResult));

    }

}
