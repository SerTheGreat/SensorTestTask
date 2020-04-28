package model;

import java.util.Objects;

/**
 * Represents a location (building etc.) where sensors are placed.
 * Isn't named as "Object" to not confuse with the java Object class.
 */
public class Location {

    private int id;

    public Location() {
    }

    public Location(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return id == location.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
