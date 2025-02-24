# Use the official OpenJDK image as the base image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR file from the local system to the container
COPY build/libs/*.jar app.jar

# Expose the port that Ktor runs on
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "app.jar"]
