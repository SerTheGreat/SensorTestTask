package dao;

import model.Measurement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MeasurementDAOImpl implements MeasurementDAO {

    public static final String TABLE = "measurements";

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public void save(Collection<Measurement> measurements) {
        jdbcTemplate.batchUpdate(
                "INSERT INTO " + TABLE + " (sensorId, time, value) VALUES (?, ?, ?)"
                + " ON CONFLICT ON CONSTRAINT pk_measurements DO NOTHING",
                measurements
                        .stream()
                        .map(m ->  new Object[] {
                                m.getSensorId(),
                                m.getTime(),
                                m.getValue()})
                        .collect(Collectors.toList())
        );
    }

    @Override
    public List<Measurement> findBySensorIdAndPeriod(int sensorId, long from, long to, int limit, int offset) {
        List<Measurement> measurements = new ArrayList<>();
        jdbcTemplate.query("SELECT * FROM " + TABLE + " WHERE sensorId=? AND time >=? AND time <=? "
                + " ORDER BY time LIMIT ? OFFSET ?",
                new Object[] {sensorId, from, to, limit, offset},
                row -> {
                    Measurement measurement = new Measurement();
                    measurement.setSensorId(row.getInt("sensorId"));
                    measurement.setTime(row.getLong("time"));
                    measurement.setValue(row.getDouble("value"));
                    measurements.add(measurement);
                });
        return measurements;
    }

}
