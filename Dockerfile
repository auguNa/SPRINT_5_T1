# Build stage
FROM maven:3.8.6-eclipse-temurin-17 AS build

# Set the working directory inside the container for the build
WORKDIR /app

# Copy the pom.xml and project files to the container
COPY pom.xml .
COPY src ./src

# Package the application (skip tests for faster build)
RUN mvn clean package -DskipTests

# Run stage
FROM openjdk:17-jdk-slim

# Set the working directory inside the container for running the app
WORKDIR /app

# Copy the jar file from the build stage
COPY --from=build /app/target/blackjack-docker.jar app.jar

# Expose the port that the application will run on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
