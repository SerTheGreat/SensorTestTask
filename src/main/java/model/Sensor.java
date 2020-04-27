package model;

import java.util.Objects;

/**
 * Represents a sensor
 */
public class Sensor {

    private int id;

    private int objectId;

    private Long latestTime;

    private Double latestValue;

    public Sensor() {
    }

    public Sensor(int id, int objectId, Long latestTime, Double latestValue) {
        this.id = id;
        this.objectId = objectId;
        this.latestTime = latestTime;
        this.latestValue = latestValue;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getObjectId() {
        return objectId;
    }

    public void setObjectId(int objectId) {
        this.objectId = objectId;
    }

    public Long getLatestTime() {
        return latestTime;
    }

    public void setLatestTime(Long latestTime) {
        this.latestTime = latestTime;
    }

    public Double getLatestValue() {
        return latestValue;
    }

    public void setLatestValue(Double latestValue) {
        this.latestValue = latestValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sensor sensor = (Sensor) o;
        return id == sensor.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
