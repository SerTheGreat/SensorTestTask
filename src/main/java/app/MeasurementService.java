package app;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import dao.MeasurementDAO;
import dao.SensorDAO;
import model.Measurement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * The main service to deal with measurement data
 */
@Service
public class MeasurementService {

    private Logger log = LoggerFactory.getLogger(MeasurementService.class);

    @Value("${maxMeasurementsBatchSize : 9192}")
    private int maxBatchSize;

    @Autowired
    private MeasurementDAO measurementDAO;

    @Autowired
    private SensorDAO sensorDAO;

    @Autowired
    private MeasurementBatchService batchService;

    /**
     * Parses and saves measurement data to the database from an input stream.
     * The method expects json array of objects in the stream.
     * @param inputStream the input stream of json measurement data
     */
    void parseAndSave(InputStream inputStream) {
        MeasurementBatchService.MeasurementBatch batch = batchService.createBatch();
        try {
            JsonFactory jfactory = new JsonFactory();
            JsonParser parser = jfactory.createParser(inputStream);
            while (parser.nextToken() != JsonToken.END_ARRAY) {
                //Parsing our measurement data:
                Measurement measurement = new Measurement();
                while (parser.nextToken() != JsonToken.END_OBJECT) {
                    if (!JsonToken.FIELD_NAME.equals(parser.currentToken())) { //looking for the next field name
                        continue;
                    }
                    String fieldName = parser.getCurrentName();
                    if (fieldName == null) {
                        continue;
                    }
                    switch (fieldName) {
                        case "objectId":
                            parser.nextToken();
                            measurement.setObjectId(parser.getIntValue());
                            break;
                        case "sensorId":
                            parser.nextToken();
                            measurement.setSensorId(parser.getIntValue());
                            break;
                        case "time":
                            parser.nextToken();
                            measurement.setTime(parser.getLongValue());
                            break;
                        case "value":
                            parser.nextToken();
                            measurement.setValue(parser.getDoubleValue());
                            break;
                    }
                }
                //Reached the end of an object. If its data isn't full, jump to the next one
                if (!validate(measurement)) {
                    log.info("Invalid measurement data:" + (new JsonMapper()).writeValueAsString(measurement));
                    continue;
                }
                //If the parsed data is Ok, add the measurement to the batch:
                batchService.saveThroughBatch(measurement, batch);
            }
        } catch (Exception e) {
            log.error("Error on parsing json", e);
            throw new RuntimeException(e);
        } finally {
            batchService.flush(batch);
        }
    }

    /**
     * Finds all measurements for specified time period by sensor id and writes them into output stream as json.
     * @param sensorId id of the sensor
     * @param from start of the period
     * @param to end of the period
     * @param outputStream the output stream to write json results
     */
    public void findBySensorIdAndPeriod(
            int sensorId,
            long from,
            long to,
            OutputStream outputStream
    ) {
        //Measurements will be queried and streamed in pages to avoid OutOfMemory if too much history is requested
        try {
            JsonFactory factory = new JsonFactory();
            JsonGenerator jsonGenerator = factory.createGenerator(outputStream);
            jsonGenerator.setCodec(new ObjectMapper());
            int offset = 0; //we will query measurements in pages adding a page size to offset every time
            List<Measurement> result;
            jsonGenerator.writeStartArray();
            do {
                result = measurementDAO.findBySensorIdAndPeriod(sensorId, from, to, maxBatchSize, offset);
                result.forEach(m -> {
                    try {
                        jsonGenerator.writeObject(m);
                    } catch (Exception e) {
                        log.error("Error writing output stream", e);
                        throw new RuntimeException(e);
                    }
                });
                offset += result.size();
            } while (result.size() > 0);
            jsonGenerator.writeEndArray();
            jsonGenerator.flush();
        } catch (Exception e) {
            log.error("Error on serializing json");
            throw new RuntimeException(e);
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException ioe) {
                log.error("Error on closing output stream", ioe);
                throw new RuntimeException(ioe);
            }
        }
    }

    /**
     * Gets the latest measurement values for specified location
     * @param objectId id of the location
     * @return a map where the key is a sensor id and the value is the latest measurement value of the sensor
     */
    public Map<Integer, Double> getLatestValuesByObjectId(int objectId) {
        return sensorDAO.getLatestValuesByObjectId(objectId);
    }

    /**
     * Gets the average of the current sensor values for every location
     * @return a map where the key is a location id and the value is an average of current values of its sensors
     */
    public Map<Integer, Double> currentAverages() {
        return sensorDAO.currentAverages();
    }

    /**
     * Checks the specified measurement data for validity
     * @param measurement the Measurement object to validate
     * @return true if the measurement data is valid, false otherwise
     */
    private boolean validate(Measurement measurement) {
        return
                measurement.getObjectId() != null &&
                measurement.getSensorId() != null &&
                measurement.getTime() != null &&
                measurement.getValue() != null;
    }

}
