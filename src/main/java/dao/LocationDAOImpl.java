package dao;

import model.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.stream.Collectors;

@Repository
public class LocationDAOImpl implements LocationDAO {

    public static final String TABLE = "locations";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void save(Collection<Location> locations) {
        jdbcTemplate.batchUpdate(
                "INSERT INTO " + TABLE + "(id) VALUES (?) ON CONFLICT (id) DO NOTHING",
                locations.stream()
                        .map(loc -> new Object[] {loc.getId()})
                        .collect(Collectors.toList())
                );
    }
}
