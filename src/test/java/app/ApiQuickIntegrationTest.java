package app;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ApiQuickIntegrationTest implements ApiIntegrationTest {

    @Autowired
    private WebApplicationContext context;

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
    @Order(0) //Other tests rely on the data saved by this one so it should always run first
    @Override
    public void testApiSave() throws Exception {
        mockMvc.perform(post(ApiIntegrationTest.SAVE_URL)
                .contentType("application/json")
                .content(TEST_JSON_CONTENT))
                .andExpect(status().isOk());
    }

    @Test
    @Override
    public void testApiHistory() throws Exception {
        final String expectedResult = "{\"sensorId\":1,\"time\":1001,\"value\":1.3}";
        mockMvc.perform(get(ApiIntegrationTest.HISTORY_URL)
                .param("id", "1")
                .param("from","1001")
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
