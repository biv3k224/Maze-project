# First stage: Build the JAR using Maven
FROM maven:3.8.4-openjdk-17 AS build

# Set the working directory
WORKDIR /app

# Copy the source code to the container
COPY . .

# Build the JAR file
RUN mvn clean package

# Second stage: Use a smaller image to run the JAR
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/maze-project.jar /app/maze-project.jar

# Expose the port the app runs on
EXPOSE 8080

# Run the JAR file
CMD ["java", "-jar", "/app/maze-project.jar"]
