# SensorTestTask

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
Use generator to generate a file with test json content (generator isn't included).
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
