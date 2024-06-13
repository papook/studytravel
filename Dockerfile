# # This Dockerfile is used to build a Docker image for a Spring Boot application.
# # It starts with the openjdk:17-jdk-alpine base image as it is a 
# # lightweight image that contains the Java Development Kit (JDK) 
# # and the Alpine Linux operating system.

# Use a Maven image to build the application
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app

# Copy the project descriptor files
COPY pom.xml .
COPY src src

# Build the application with Maven wrapper
RUN mvn clean package -DskipTests

# Use Alpine OpenJDK image as the base image for runtime
FROM openjdk:17-jdk-alpine

# Create a user and group named "spring" to run the application with restricted privileges.
RUN addgroup -S spring && adduser -S spring -G spring

# Set the user to "spring" for subsequent commands.
USER spring

# Environment Variables to configure the Spring Boot application
ENV SPRING_APPLICATION_NAME="Study Travel"
ENV SPRING_DATASOURCE_URL=jdbc:h2:mem:memorydb
ENV SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.h2.Driver
ENV SPRING_DATASOURCE_USERNAME=sa
ENV SPRING_DATASOURCE_PASSWORD=
ENV SERVER_PORT=8080

# Expose port 8080 to allow external access to the Spring Boot application
EXPOSE 8080

# Copy the JAR file of the Spring Boot application to the Docker image
COPY --from=build /app/target/studytravel.jar app.jar

# Run the Spring Boot application when the Docker container starts
ENTRYPOINT ["java", "-jar", "/app.jar"]