package dao;

import model.Location;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public interface LocationDAO {

        void save(Collection<Location> locations);

}
