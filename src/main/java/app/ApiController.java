package app;

import com.fasterxml.jackson.annotation.JsonView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {

    private Logger log = LoggerFactory.getLogger(ApiController.class);

    @Autowired
    MeasurementService measurementService;

    @RequestMapping(path = "/save", method = RequestMethod.POST)
    public void save(HttpServletRequest request) throws IOException {
        measurementService.parseAndSave(request.getInputStream());
    }

    @RequestMapping(path = "/history", method = RequestMethod.GET)
    public void history(@RequestParam("id") int sensorId,
                        @RequestParam("from") long from,
                        @RequestParam("to") long to,
                        HttpServletResponse response) {
        try {
            measurementService.findBySensorIdAndPeriod(sensorId, from, to, response.getOutputStream());
        } catch (IOException ioe) {
            log.error("Cannot get output stream", ioe);
            throw new RuntimeException(ioe);
        }
    }

    @RequestMapping(path = "/latest", method = RequestMethod.GET)
    public Map<Integer, Double> latest(@RequestParam("id") int objectId) {
        return measurementService.getLatestValuesByObjectId(objectId);
    }

    @RequestMapping(path = "/avg", method = RequestMethod.GET)
    public Map<Integer, Double> averages() {
        return measurementService.currentAverages();
    }

}
