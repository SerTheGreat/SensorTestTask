package model;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Represents single measurement data of a certain sensor
 */
public class Measurement {

    @JsonIgnore
    //This field is redundant for measurement data as sensorId is enough. It is needed only when reading API input.
    private Integer objectId;

    private Integer sensorId;

    public Measurement() {
    }

    private Long time;

    private Double value;

    public Integer getObjectId() {
        return objectId;
    }

    public void setObjectId(Integer objectId) {
        this.objectId = objectId;
    }

    public Integer getSensorId() {
        return sensorId;
    }

    public void setSensorId(Integer sensorId) {
        this.sensorId = sensorId;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

}
