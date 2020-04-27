package dao;

import model.Measurement;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component
public interface MeasurementDAO {
    /**
     * Saves measurements data to the database
     * @param measurements a list of measurements to save
     */
    void save(Collection<Measurement> measurements);

    /**
     * Finds all measurements for specified time period by sensor id
     * @param sensorId id of the sensor
     * @param from start of the period in seconds
     * @param to end of the period in seconds
     * @return list of found measurements, or an empty list if nothing was found
     */
    List<Measurement> findBySensorIdAndPeriod(
            int sensorId,
            long from,
            long to,
            int limit,
            int offset
    );

}
