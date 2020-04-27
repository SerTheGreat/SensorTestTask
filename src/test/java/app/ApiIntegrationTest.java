package app;

public interface ApiIntegrationTest {

    String SAVE_URL = "/api/save";
    String HISTORY_URL = "/api/history";
    String LATEST_URL = "/api/latest";
    String AVERAGES_URL = "/api/avg";

    void testApiSave() throws Exception;

    void testApiHistory() throws Exception;

    void testApiLatest() throws Exception;

    void testApiAverages() throws Exception;

}
