package app;

import dao.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public LocationDAO locationDAO() {
        return new LocationDAOImpl();
    }

    @Bean
    public SensorDAO sensorDAO() {
        return new SensorDAOImpl();
    }

    @Bean
    public MeasurementDAO measurementDAO() {
        return new MeasurementDAOImpl();
    }
}
