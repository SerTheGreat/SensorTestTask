package dao;

import model.Sensor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component
public interface SensorDAO {

    void save(Collection<Sensor> sensors);

    Map<Integer, Double> getLatestValuesByObjectId(int id);

    /**
     * Counts the average of the current sensor values for every location
     * @return a map where the key is a location id and the value is an average of current values of its sensors
     */
    Map<Integer, Double> currentAverages();

}
