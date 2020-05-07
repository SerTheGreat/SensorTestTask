package dao;

import model.Sensor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class SensorDAOImpl implements SensorDAO {

    public static final String TABLE = "sensors";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void save(Collection<Sensor> sensors) {
        jdbcTemplate.batchUpdate(
                "INSERT INTO " + TABLE + " (id, objectId, latestTime, latestValue) VALUES (?, ?, ? ,?)"
                + " ON CONFLICT (id)"
                + " DO UPDATE SET latestTime = ?, latestValue= ? WHERE " + TABLE + ".latestTime < ?",
                sensors.stream()
                        .map(s -> new Object[] {
                                s.getId(), s.getObjectId(), s.getLatestTime(), s.getLatestValue(), //VALUES
                                s.getLatestTime(), s.getLatestValue(), //UPDATE SET
                                s.getLatestTime() //WHERE
                        })
                        .collect(Collectors.toList())
        );
    }

    @Override
    public Map<Integer, Double> getLatestValuesByObjectId(int id) {
        Map<Integer, Double> result = new HashMap<>();
        jdbcTemplate.query(
                "SELECT id, latestValue FROM " + TABLE + " WHERE objectId=?",
                new Object[] {id},
                row -> {
                    result.put(row.getInt("id"), row.getDouble("latestValue"));
                });
        return result;
    }

    @Override
    public Map<Integer, Double> currentAverages() {
        Map<Integer, Double> result = new HashMap<>();
        jdbcTemplate.query(
                "SELECT objectId, AVG(latestValue) as av FROM " + TABLE + " GROUP BY objectId",
                row -> {
                    result.put(row.getInt("objectId"), row.getDouble("av"));
                }
        );
        return result;
    }

}
