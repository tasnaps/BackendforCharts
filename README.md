# Project structure:
The controller package contains the RestControllers for handling HTTP requests.
The config package includes configuration classes such as AsyncConfig.java for enabling asynchronous processing.
The model package contains JPA Entities like Section.java and GeologicalClass.java.
The repository package includes JPA Repositories.
The service package contains the service classes to handle the business logic.

#Tech stack:
Java JDK version 23-preview
Spring Boot - for implementing the Microservice.
Spring MVC - for creating the RESTful APIs.
Spring Data JPA - for managing the data layer.
MySQL Database - for persisting data
Jakarta EE
Apache POI - for handling excel files.

#Running the service
Run the BackendforChartsApplication.java

#Configuration
Application's configurations are located in the application.properties file, edit as you wish
spring.application.name=BackendforCharts
spring.datasource.url=jdbc:mysql://localhost:3306/mydb
spring.datasource.username=root
spring.datasource.password=Tapsako@12@
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MariaDBDialect
spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
spring.jpa.hibernate.ddl-auto=create
server.port=8081

#API Endpoints
Sections API
GET /sections: Fetch all sections
POST /sections: Create a new section. An input JSON of section details should be included in the body of the request.
POST /sections/upload: Upload an Excel file to add/update sections from the file.
GET /sections/{id}: Fetch the section with given ID.
GET /sections/by-code: Fetch sections by GeologicalClass code.
PUT /sections/{id}: Update the section with given ID. An input JSON of section details should be included in the body of the request. If the section does not exist already, it returns a '404 Not Found' error.
DELETE /sections/{id}: Delete the section with given id. If the section does not exist, it returns a '404 Not Found' error.

GeologicalClass API
GET /geoclasses: Fetch all geological classes.
POST /geoclasses: Create a new geological class. The class details should be included in the body of the request in JSON format.
GET /geoclasses/{id}: Fetch the geological class with given ID.
PUT /geoclasses/{id}: Update the geological class with given ID. The updated details of the geological class should be included in the body of the request in JSON format.
DELETE /geoclasses/{id}: Delete the Geological class with given ID.

Jobs API
POST /import: Import data from provided file in the request. It returns the id of the created Job.
GET /import/{id}: Get the status of an import job by given {id}.
GET /export: Start an export job. The service will begin to prepare data for export and it return the id of the created Job.
GET /export/{id}: Get the status of an export job by given {id}
GET /export/{id}/file: Get the exported file for the job with given {id}. The service will return the exported file if the export job is completed.
