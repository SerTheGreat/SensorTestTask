package dao;

import model.Sensor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component
public interface SensorDAO {

    /**
     * Saves sensors to database. For every sensor that already exist the latestTime and latestValue is updated
     * @param sensors a collection of sensor data to save
     */
    void save(Collection<Sensor> sensors);

    /**
     * Gets the latest measurement values for specified location
     * @param id id of the location
     * @return a map where the key is a sensor id and the value is the latest measurement value of the sensor
     */
    Map<Integer, Double> getLatestValuesByObjectId(int id);

    /**
     * Counts the average of the current sensor values for every location
     * @return a map where the key is a location id and the value is an average of current values of its sensors
     */
    Map<Integer, Double> currentAverages();

}
