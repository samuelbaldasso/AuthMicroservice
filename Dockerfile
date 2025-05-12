# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the project jar file into the container
COPY target/auth-microservice.jar auth-microservice.jar

# Expose the port the app runs on
EXPOSE 8081

# Run the jar file
ENTRYPOINT ["java", "-jar", "auth-microservice.jar"]
