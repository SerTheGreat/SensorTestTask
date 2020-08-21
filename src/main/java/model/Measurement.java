package model;

import app.Views;
import com.fasterxml.jackson.annotation.JsonView;

/**
 * Represents single measurement data of a certain sensor
 */
public class Measurement {

    //This field is redundant for measurement data as sensorId is enough. It is needed only when reading API input.
    //One of the possible solutions would be to split this class into DTO for accepting full data from API
    //and a pure model class, and implement methods to convert between them. But that would require additional memory
    //overhead, so the class serves dual DTO+model purpose with JsonView being used to control serialization instead.
    private Integer objectId;

    @JsonView(Views.History.class)
    private Integer sensorId;

    @JsonView(Views.History.class)
    private Long time;

    @JsonView(Views.History.class)
    private Double value;

    public Measurement() {
    }

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
