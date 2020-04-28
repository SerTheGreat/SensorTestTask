# SensorTestTask
The project is a solution of the test task to write a REST service

## The service API
* <i>/api/save</i> - save sensor measurements data passed as a json array
* <i>/api/history?id=1&from=1565654400&to=1565827200</i> - get measurements history for the specified sensor and time period
* <i>/api/latest?id=1</i> - get the current values of all sensors for the specified object
* <i>/api/avg</i> - get the average of the current values of all sensors for every object

## Requirements
To run the project Java 8 is required. Also you need to have PostgreSQL server installed.

## Build
Clone the repository. Run `gradlew assemble` to build the project. The build result will be an executable jar-file in <i>/build/libs/</i> directory.

## Test
Integration tests are intended to test the whole functionality of the service starting from HTTP endpoints to database storage.
To run integration tests you need to create a test Postgres database.
After that go to <i>/src/test/resources</i> and specify your database name, username and password in the <i>application.yaml</i> file:
```
spring:
  datasource:
    url: jdbc:postgresql://localhost/sensor_test
    username: admin
    password: admin
```    
and run `gradlew :test --tests app.ApiQuickIntegrationTest` from project's root. The database schema will be recreated automatically.

The above will run quick tests on a few lines of test data. You may also run tests with heavy load. 
Use generator to generate a file with test json content in the following format (generator isn't included):
```
[
{"objectId": 0, "sensorId": 0, "time": 1587634767, "value": 102.0},
{"objectId": 0, "sensorId": 1, "time": 1587634768, "value": 29.0},
{"objectId": 1, "sensorId": 2, "time": 1587634767, "value": -27.0},
{"objectId": 2, "sensorId": 3, "time": 1587634767, "value": -3.0}
...
]
```
Name it <i>data.json</i> and put into the project's root. After that run `gradlew :test --tests app.ApiStressIntegrationTest`

## Deploy and run
Create a new Postgres database. Build or download the jar file. Create an <i>application.yaml</i> file near the jar file with the following contents:
```
spring:
  datasource:
    url: jdbc:postgresql://localhost/sensor_task
    username: admin
    password: admin
```    
and specify your database name, username and password.
After that run `java -jar sensor-task-1.0.jar`

The server will listen on standard 80 HTTP port by default.

## Database initialization script
The script is specified in the /src/main/resources/db/changelog/changelog-master.xml
