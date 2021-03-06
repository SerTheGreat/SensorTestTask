package app;

import dao.LocationDAO;
import dao.MeasurementDAO;
import dao.SensorDAO;
import model.Location;
import model.Measurement;
import model.Sensor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * The measurement data are parsed and saved in batches to improve DB performance,
 * so this is the main service to deal with those batches
 */

@Service
public class MeasurementBatchService {

    @Value("${maxMeasurementsBatchSize : 9192}")
    private int maxBatchSize;

    @Autowired
    private LocationDAO locationDAO;

    @Autowired
    private SensorDAO sensorDAO;

    @Autowired
    private MeasurementDAO measurementDAO;


    /**
     * Creates a new empty batch to accumulate measurements before being flushed to database
     * @return a new MeasurementBatch instance
     */
    public MeasurementBatch createBatch() {
        return new MeasurementBatch(maxBatchSize);
    }

    /**
     * Puts a Measurement into the batch which is then flushed to database if its capacity is reached
     * @param measurement The measurement data to be saved
     * @param batch A @MeasurementBatch instance to hold the measurement
     * @return the number of actually saved measurements (non-zero only when batch flushes)
     */
    @Transactional
    public int saveThroughBatch(Measurement measurement, MeasurementBatch batch) {
        int saved = 0;
        batch.add(measurement);
        if (batch.measurements.size() >= maxBatchSize) { //actually the size should never get over the MAX
            saved = flush(batch);
        }
        return saved;
    }

    /**
     * Saves the contents if a batch to database and clears it.
     * This method also saves new locations and sensors referenced by the measurements data.
     * @param batch a MeasurementBatch instance to flush
     * @return the number of actually saved measurements
     */
    @Transactional
    public int flush(MeasurementBatch batch) {
        locationDAO.save(batch.locationSet());
        sensorDAO.save(batch.sensorSet());
        int saved = measurementDAO.save(batch.getMeasurements());
        batch.clear();
        return saved;
    }

    /**
     * Represents a batch of measurement objects intended to be saved to the database all at once.
     * In fact it is a list wrapper implemented as an inner class with private methods for only the
     * MeasurementBatchService methods could manipulate the underlying list.
     */
    public static class MeasurementBatch {

        private final List<Measurement> measurements;

        private MeasurementBatch(int maxBatchSize) {
            measurements = new ArrayList<>(maxBatchSize);
        }

        private void add(Measurement measurement) {
            measurements.add(measurement);
        }

        private void clear() {
            measurements.clear();
        }

        /**
         * Gets a list of measurements in the batch
         * @return an immutable list of measurements
         */
        public java.util.List<Measurement> getMeasurements() {
            return Collections.unmodifiableList(measurements);
        }

        /**
         * Returns a set of sensors that are referenced by measurements in the batch.
         * Additionally latestValue and latestTime fields of each sensor of the set contain the latest values of all
         * the measurements in the batch that are related to that sensor.
         * @return a set of Sensor objects that are referenced by measurement data in the batch
         */
        public Set<Sensor> sensorSet() {
            //Have to use a double map since there's no effective way to get a matching instance from a Set
            Map<Sensor, Sensor> map = new HashMap<>();
            measurements.forEach(m -> {
                Sensor newSensor = new Sensor(m.getSensorId(), m.getObjectId(), m.getTime(), m.getValue());
                Sensor sensor = map.get(newSensor);
                if (sensor != null) { //if we find more recent value, we update the latest_time and value for this sensor
                    if (sensor.getLatestTime() == null || sensor.getLatestTime() < newSensor.getLatestTime()) {
                        sensor.setLatestTime(newSensor.getLatestTime());
                        sensor.setLatestValue(newSensor.getLatestValue());
                    }
                } else {
                    map.put(newSensor, newSensor);
                }
            });
            return map.keySet();
        }

        /**
         * Returns a set of locations that are referenced by measurements in the batch.
         * @return a set of Location objects that are referenced by measurement data in the batch
         */
        public Set<Location> locationSet() {
            return
                measurements.stream()
                        .collect(
                                HashSet::new,
                                (s, m) -> s.add(new Location(m.getObjectId())),
                                Set::addAll
                        );
        }
    }

}
